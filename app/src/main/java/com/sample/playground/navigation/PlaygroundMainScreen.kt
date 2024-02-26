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
        playgroundItems.forEach {
            PlaygroundItem(
                name = it.name,
                description = it.description,
                destinationRoute = it.navigationRoute.route,
                onNavigate = navHostController::navigate
            )
        }
    }
}

private val playgroundItems = listOf(
    PlaygroundItem(
        name = "Window",
        description = "Cutout, Immersive, SystemBars에 대한 예제입니다.",
        navigationRoute = PlaygroundNavigation.Window
    ),
    PlaygroundItem(
        name = "Layout",
        description = "Layout에 대한 예제입니다.",
        navigationRoute = PlaygroundNavigation.Layout
    ),
)

data class PlaygroundItem(
    val name: String,
    val description: String,
    val navigationRoute: PlaygroundNavigation
)