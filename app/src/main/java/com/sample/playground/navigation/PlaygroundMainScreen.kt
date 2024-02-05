package com.sample.playground.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PlaygroundMainScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        playgroundItems.forEach {
            PlaygroundItems(
                name = it.name,
                description = it.description,
                destinationRoute = it.navigationRoute.route,
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun PlaygroundItems(
    name: String,
    description: String,
    destinationRoute: String,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navHostController.navigate(destinationRoute)
            },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall)
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