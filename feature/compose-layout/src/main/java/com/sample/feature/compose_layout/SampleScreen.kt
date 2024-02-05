package com.sample.feature.compose_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout

@Composable
fun SampleScreen(
    modifier: Modifier = Modifier,
) {
    
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Text(text = "Sample Screen")
    }
}