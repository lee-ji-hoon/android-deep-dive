package com.sample.playground.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun PlaygroundNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PlaygroundNavigation.Main.route
    ) {
        composable(route = PlaygroundNavigation.Main.route) {
            PlaygroundMainScreen(navController)
        }

        composable(route = PlaygroundNavigation.Window.route) {

        }
    }
}

sealed interface PlaygroundNavigation {

    val route: String

    data object Main : PlaygroundNavigation {
        override val route: String = "main"
    }

    data object Window : PlaygroundNavigation {
        override val route: String = "window"
    }
}

