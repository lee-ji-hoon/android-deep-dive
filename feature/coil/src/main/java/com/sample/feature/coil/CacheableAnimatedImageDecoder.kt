@file:OptIn(ExperimentalCoilApi::class)

package com.sample.feature.coil

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import androidx.annotation.RequiresApi
import androidx.core.graphics.decodeDrawable
import androidx.core.util.component1
import androidx.core.util.component2
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.annotation.InternalCoilApi
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.decode.toImageDecoderSourceOrNull
import coil3.fetch.SourceFetchResult
import coil3.gif.animatedTransformation
import coil3.gif.animationEndCallback
import coil3.gif.animationStartCallback
import coil3.gif.isAnimatedHeif
import coil3.gif.isAnimatedWebP
import coil3.gif.isGif
import coil3.gif.repeatCount
import coil3.request.Options
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.request.colorSpace
import coil3.request.maxBitmapSize
import coil3.size.Precision
import coil3.size.ScaleDrawable
import coil3.util.component1
import coil3.util.component2
import coil3.util.isHardware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.BufferedSource
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import okio.ForwardingSource
import okio.Source
import okio.buffer
import kotlin.math.roundToInt

/**
 * A [Decoder] that uses [ImageDecoder] to decode GIFs, animated WebPs, and animated HEIFs.
 *
 * NOTE: Animated HEIF files are only supported on API 30 and above.
 *
 * @param enforceMinimumFrameDelay If true, rewrite a GIF's frame delay to a default value if
 *  it is below a threshold. See https://github.com/coil-kt/coil/issues/540 for more info.
 */
@OptIn(InternalCoilApi::class)
@RequiresApi(28)
class CacheableAnimatedImageDecoder(
    private val source: ImageSource,
    private val options: Options,
    // https://android.googlesource.com/platform/frameworks/base/+/2be87bb707e2c6d75f668c4aff6697b85fbf5b15
    private val enforceMinimumFrameDelay: Boolean = SDK_INT < 34
) : Decoder {

    override suspend fun decode(): DecodeResult {
        var isSampled = false
        val drawable = runInterruptible {
            maybeWrapImageSourceToRewriteFrameDelay(
                source,
                enforceMinimumFrameDelay
            ).use { source ->
                val imageSource = source.toImageDecoderSourceOrNull(options, animated = true)
                    ?: ImageDecoder.createSource(
                        source.source().use { it.squashToDirectByteBuffer() })
                imageSource.decodeDrawable { info, _ ->
                    // Configure the output image's size.
                    val (srcWidth, srcHeight) = info.size
                    val (dstWidth, dstHeight) = DecodeUtils.computeDstSize(
                        srcWidth = srcWidth,
                        srcHeight = srcHeight,
                        targetSize = options.size,
                        scale = options.scale,
                        maxSize = options.maxBitmapSize
                    )
                    if (srcWidth > 0 &&
                        srcHeight > 0 &&
                        (srcWidth != dstWidth || srcHeight != dstHeight)
                    ) {
                        val multiplier = DecodeUtils.computeSizeMultiplier(
                            srcWidth = srcWidth,
                            srcHeight = srcHeight,
                            dstWidth = dstWidth,
                            dstHeight = dstHeight,
                            scale = options.scale
                        )

                        // Set the target size if the image is larger than the requested dimensions
                        // or the request requires exact dimensions.
                        isSampled = multiplier < 1
                        if (isSampled || options.precision == Precision.EXACT) {
                            val targetWidth = (multiplier * srcWidth).roundToInt()
                            val targetHeight = (multiplier * srcHeight).roundToInt()
                            setTargetSize(targetWidth, targetHeight)
                        }
                    }

                    // Configure any other attributes.
                    configureImageDecoderProperties()
                }
            }
        }
        return DecodeResult(
            image = wrapDrawable(drawable).asImage(shareable = true),
            isSampled = isSampled
        )
    }

    @OptIn(InternalCoilApi::class)
    private fun ImageDecoder.configureImageDecoderProperties() {
        allocator = if (options.bitmapConfig.isHardware) {
            ImageDecoder.ALLOCATOR_HARDWARE
        } else {
            ImageDecoder.ALLOCATOR_SOFTWARE
        }
        memorySizePolicy = if (options.allowRgb565) {
            ImageDecoder.MEMORY_POLICY_LOW_RAM
        } else {
            ImageDecoder.MEMORY_POLICY_DEFAULT
        }
        if (options.colorSpace != null) {
            setTargetColorSpace(options.colorSpace)
        }
        postProcessor = options.animatedTransformation?.asPostProcessor()
    }

    private suspend fun wrapDrawable(baseDrawable: Drawable): Drawable {
        if (baseDrawable !is AnimatedImageDrawable) {
            return baseDrawable
        }

        baseDrawable.repeatCount = options.repeatCount

        // Set the start and end animation callbacks if any one is supplied through the request.
        val onStart = options.animationStartCallback
        val onEnd = options.animationEndCallback
        if (onStart != null || onEnd != null) {
            // Animation callbacks must be set on the main thread.
            withContext(Dispatchers.Main.immediate) {
                baseDrawable.registerAnimationCallback(animatable2CallbackOf(onStart, onEnd))
            }
        }

        // Wrap AnimatedImageDrawable in a ScaleDrawable so it always scales to fill its bounds.
        return ScaleDrawable(baseDrawable, options.scale)
    }

    class Factory(
        // https://android.googlesource.com/platform/frameworks/base/+/2be87bb707e2c6d75f668c4aff6697b85fbf5b15
        private val enforceMinimumFrameDelay: Boolean = SDK_INT < 34
    ) : Decoder.Factory {

        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            if (!isApplicable(result.source.source())) {
                return null
            }
            return CacheableAnimatedImageDecoder(result.source, options, enforceMinimumFrameDelay)
        }

        private fun isApplicable(source: BufferedSource): Boolean {
            return DecodeUtils.isGif(source) ||
                    DecodeUtils.isAnimatedWebP(source) || (SDK_INT >= 30 && DecodeUtils.isAnimatedHeif(source
            ))
        }
    }
}

/**
 * A [ForwardingSource] that rewrites the GIF frame delay in every graphics control block if it's
 * below a threshold.
 */
internal class FrameDelayRewritingSource(delegate: Source) : ForwardingSource(delegate) {

    // An intermediary buffer so we can read and alter the data before it's written to the destination.
    private val buffer = Buffer()

    override fun read(sink: Buffer, byteCount: Long): Long {
        // Ensure our buffer has enough bytes to satisfy this read.
        request(byteCount)

        // Short circuit if there are no bytes in the buffer.
        if (buffer.size == 0L) {
            return if (byteCount == 0L) 0L else -1L
        }

        // Search through the buffer and rewrite any frame delays below the threshold.
        var bytesWritten = 0L
        while (true) {
            val index = indexOf(FRAME_DELAY_START_MARKER)
            if (index == -1L) break

            // Write up until the end of the frame delay start marker.
            bytesWritten += write(sink, index + FRAME_DELAY_START_MARKER_SIZE_BYTES)

            // Check for the end of the graphics control extension block.
            if (!request(5) || buffer[4] != 0.toByte()) continue

            // Rewrite the frame delay if it is below the threshold.
            // The frame delay is stored as two unsigned bytes in reverse order
            // (i.e. the most significant bits are in the byte that's read second).
            val frameDelay = (buffer[2].toUByte().toInt() shl 8) or buffer[1].toUByte().toInt()
            if (frameDelay < MINIMUM_FRAME_DELAY) {
                sink.writeByte(buffer[0].toInt())
                sink.writeByte(DEFAULT_FRAME_DELAY)
                sink.writeByte(0)
                buffer.skip(3)
            }
        }

        // Write anything left in the source.
        if (bytesWritten < byteCount) {
            bytesWritten += write(sink, byteCount - bytesWritten)
        }
        return if (bytesWritten == 0L) -1 else bytesWritten
    }

    private fun indexOf(bytes: ByteString): Long {
        var index = -1L
        while (true) {
            index = buffer.indexOf(bytes[0], index + 1)
            if (index == -1L) break
            if (request(bytes.size.toLong()) && buffer.rangeEquals(index, bytes)) break
        }
        return index
    }

    private fun write(sink: Buffer, byteCount: Long): Long =
        buffer.read(sink, byteCount).coerceAtLeast(0)

    private fun request(byteCount: Long): Boolean {
        if (buffer.size >= byteCount) return true
        val toRead = byteCount - buffer.size
        return super.read(buffer, toRead) == toRead
    }

    private companion object {
        // https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
        // See: "Graphics Control Extension"
        private val FRAME_DELAY_START_MARKER = "0021F904".decodeHex()
        private const val FRAME_DELAY_START_MARKER_SIZE_BYTES = 4
        private const val MINIMUM_FRAME_DELAY = 2
        private const val DEFAULT_FRAME_DELAY = 10
    }
}

internal fun maybeWrapImageSourceToRewriteFrameDelay(
    source: ImageSource,
    enforceMinimumFrameDelay: Boolean
): ImageSource = if (enforceMinimumFrameDelay && DecodeUtils.isGif(source.source())) {
    // Wrap the source to rewrite its frame delay as it's read.
    ImageSource(
        source = FrameDelayRewritingSource(source.source()).buffer(),
        fileSystem = source.fileSystem
        // Intentionally don't copy any metadata.
    )
} else {
    source
}
