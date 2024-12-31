package com.sample.feature.coil

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.util.DebugLogger
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CoilScreen(modifier: Modifier = Modifier) {
    var selectedLoader by remember { mutableStateOf(ImageLoaders.COIL) }
    val animatedImageDecoder = remember {
        if (Build.VERSION.SDK_INT >= 28) {
            CacheableAnimatedImageDecoder.Factory()
        } else {
            CacheableGifDecoder.Factory()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        ImageLoaderRadioButtons(
            selectedLoader = selectedLoader,
            onClick = { selectedLoader = it },
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = sampleWebpUrls) { url ->
                when (selectedLoader) {
                    ImageLoaders.COIL -> {
        AsyncImage(
            imageLoader = SingletonImageLoader.get(LocalContext.current)
                .newBuilder().logger(DebugLogger()).build(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .decoderFactory(animatedImageDecoder)
                .build(),
            contentDescription = null,
            modifier = Modifier.aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
                    }

                    ImageLoaders.GLIDE -> {
                        GlideImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                            requestBuilderTransform = {
                                it.diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .listener(GlideDebuggingListener())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageLoaderRadioButtons(
    selectedLoader: ImageLoaders,
    onClick: (selectedLoader: ImageLoaders) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RadioButton(
            selected = selectedLoader == ImageLoaders.COIL,
            onClick = { onClick(ImageLoaders.COIL) }
        )
        Text("Coil3")

        RadioButton(
            selected = selectedLoader == ImageLoaders.GLIDE,
            onClick = { onClick(ImageLoaders.GLIDE) }
        )
        Text("Glide")
    }
}

enum class ImageLoaders {
    COIL, GLIDE
}

private val sampleWebpUrls = List(100) {
    "https://picsum.photos/seed/${it % 5}/200/300.webp"
}

internal class GlideDebuggingListener<R : Any> : RequestListener<R> {

    override fun onResourceReady(
        resource: R,
        model: Any,
        target: Target<R>?,
        dataSource: DataSource,
        isFirstResource: Boolean
    ): Boolean {
        Log.d("Glide", "onResourceReady($model, $dataSource, $isFirstResource)")
        return false
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<R>,
        isFirstResource: Boolean
    ): Boolean {
        Log.d("Glide", "onException($e, $model, $isFirstResource)")
        return false
    }
}

@Preview
@Composable
private fun CoilScreenPreview() {
    CoilScreen()
}