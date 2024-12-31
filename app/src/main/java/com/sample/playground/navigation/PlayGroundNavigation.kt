package com.sample.playground.navigation

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sample.feature.coil.CoilScreen
import com.sample.feature.compose_layout.LayoutSampleModifyColumn
import com.sample.feature.compose_layout.LayoutSampleModifyGrid
import com.sample.feature.compose_layout.LayoutSampleModifyRow
import com.sample.feature.compose_layout.LayoutSampleScreen
import com.sample.feature.inset_animation.InsetAnimationSampleActivity
import com.sample.feature.window.WindowSampleActivity

@Composable
fun PlaygroundNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val startWindowActivity = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        navController.navigate(PlaygroundNavigation.Main.route)
    }

    NavHost(
        navController = navController,
        startDestination = PlaygroundNavigation.Main.route,
    ) {
        composable(route = PlaygroundNavigation.Main.route) {
            PlaygroundMainScreen(navController)
        }

        composable(route = PlaygroundNavigation.Window.route) {
            Box(modifier = Modifier.fillMaxSize()) {
                LaunchedEffect(Unit) {
                    val intent = Intent(context, WindowSampleActivity::class.java)
                    startWindowActivity.launch(intent)
                }
            }
        }

        composable(route = PlaygroundNavigation.InsetAnimation.route) {
            Box(modifier = Modifier.fillMaxSize()) {
                LaunchedEffect(Unit) {
                    val intent = Intent(context, InsetAnimationSampleActivity::class.java)
                    startWindowActivity.launch(intent)
                }
            }
        }

        layoutSampleGraph(navController::navigate)

        composable(route = "${PlaygroundNavigation.Coil}") {
            CoilScreen()
        }
    }
}

fun NavGraphBuilder.layoutSampleGraph(onNavigate: (route: String) -> Unit) {
    composable(
        route = "${PlaygroundNavigation.Layout}"
    ) {
        LayoutSampleScreen {
            onNavigate("${PlaygroundNavigation.Layout}/$it")
        }
    }
    composable(
        route = "${PlaygroundNavigation.Layout}/row"
    ) {
        LayoutSampleModifyRow()
    }

    composable(
        route = "${PlaygroundNavigation.Layout}/column"
    ) {
        LayoutSampleModifyColumn()
    }

    composable(
        route = "${PlaygroundNavigation.Layout}/grid"
    ) {
        LayoutSampleModifyGrid()
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

    data object InsetAnimation : PlaygroundNavigation {
        override val route: String = "inset_animation"
    }

    data object Layout : PlaygroundNavigation {
        override val route: String = "layout"
    }

    data object Coil : PlaygroundNavigation {
        override val route: String = "coil"
    }
}

