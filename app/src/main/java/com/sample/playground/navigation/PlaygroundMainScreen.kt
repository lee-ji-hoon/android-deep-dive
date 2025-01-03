package com.sample.playground.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.sample.base.PlaygroundItem

@Composable
fun PlaygroundMainScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        playGroundStates.forEach {
            PlaygroundItem(
                name = it.name,
                description = it.description,
                destinationRoute = it.navigationRoute.route,
                onNavigate = navHostController::navigate
            )
        }
    }
}

private val playGroundStates = listOf(
    PlayGroundState(
        name = "Window",
        description = "Cutout, Immersive, SystemBars에 대한 예제입니다.",
        navigationRoute = PlaygroundNavigation.Window
    ),
    PlayGroundState(
        name = "Inset_Animation",
        description = "Window Inset Animation Sample",
        navigationRoute = PlaygroundNavigation.InsetAnimation
    ),
    PlayGroundState(
        name = "Layout",
        description = "Layout에 대한 예제입니다.",
        navigationRoute = PlaygroundNavigation.Layout,
        subItems = listOf(
            PlayGroundState(
                name = "Layout/",
                description = "Custom Layout Row 예제",
                navigationRoute = PlaygroundNavigation.Layout,
            )
        )
    ),
    PlayGroundState(
        name = "Image-Loader-Webp",
        description = "Coil WebP Loader에 대한 예제입니다.",
        navigationRoute = PlaygroundNavigation.Coil,
    ),
)

data class PlayGroundState(
    val name: String,
    val description: String,
    val navigationRoute: PlaygroundNavigation,
    val subItems: List<PlayGroundState> = emptyList()
)