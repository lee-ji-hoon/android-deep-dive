@file:OptIn(ExperimentalCoilApi::class)

package com.sample.feature.coil

import android.graphics.PixelFormat
import android.graphics.PostProcessor
import android.graphics.drawable.Animatable2
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.RequiresApi
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import coil3.annotation.ExperimentalCoilApi
import coil3.gif.AnimatedTransformation
import coil3.gif.PixelOpacity
import okio.BufferedSource
import java.nio.ByteBuffer

internal fun animatable2CompatCallbackOf(
    onStart: (() -> Unit)?,
    onEnd: (() -> Unit)?
) = object : Animatable2Compat.AnimationCallback() {
    override fun onAnimationStart(drawable: Drawable) {
        onStart?.invoke()
    }

    override fun onAnimationEnd(drawable: Drawable) {
        onEnd?.invoke()
    }
}

internal val Drawable.width: Int
    get() = (this as? BitmapDrawable)?.bitmap?.width ?: intrinsicWidth

internal val Drawable.height: Int
    get() = (this as? BitmapDrawable)?.bitmap?.height ?: intrinsicHeight

@RequiresApi(28)
internal fun AnimatedTransformation.asPostProcessor() =
    PostProcessor { canvas -> transform(canvas).flag }

internal val PixelOpacity.flag: Int
    get() = when (this) {
        PixelOpacity.UNCHANGED -> PixelFormat.UNKNOWN
        PixelOpacity.TRANSLUCENT -> PixelFormat.TRANSLUCENT
        PixelOpacity.OPAQUE -> PixelFormat.OPAQUE
    }

internal fun animatable2CallbackOf(
    onStart: (() -> Unit)?,
    onEnd: (() -> Unit)?
) = object : Animatable2.AnimationCallback() {
    override fun onAnimationStart(drawable: Drawable?) {
        onStart?.invoke()
    }

    override fun onAnimationEnd(drawable: Drawable?) {
        onEnd?.invoke()
    }
}

internal fun BufferedSource.squashToDirectByteBuffer(): ByteBuffer {
    // Squash bytes to BufferedSource inner buffer then we know total byteCount.
    request(Long.MAX_VALUE)

    val byteBuffer = ByteBuffer.allocateDirect(buffer.size.toInt())
    while (!buffer.exhausted()) buffer.read(byteBuffer)
    byteBuffer.flip()
    return byteBuffer
}
