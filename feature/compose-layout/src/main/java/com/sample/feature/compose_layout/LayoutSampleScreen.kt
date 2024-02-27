package com.sample.feature.compose_layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sample.base.PlaygroundItem

@Composable
fun LayoutSampleScreen(
    modifier: Modifier = Modifier,
    onNavigate: (route: String) -> Unit
) {
    Column(modifier.fillMaxSize()) {
        PlaygroundItem(
            name = "Row",
            description = "Layout으로 직접 만든 Row입니다.",
            destinationRoute = "row",
            onNavigate = onNavigate::invoke
        )

        PlaygroundItem(
            name = "Column",
            description = "Layout으로 직접 만든 Column입니다.",
            destinationRoute = "column",
            onNavigate = onNavigate::invoke
        )

        PlaygroundItem(
            name = "Grid",
            description = "Layout으로 직접 만든 Column입니다.",
            destinationRoute = "grid",
            onNavigate = onNavigate::invoke
        )
    }
}