package com.sample.feature.compose_layout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints

@Composable
fun LayoutSampleModifyColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {
        Text(text = "first", modifier = Modifier.background(Color.Black))
        Text(text = "second", modifier = Modifier.background(Color.Blue))
        Text(text = "third", modifier = Modifier.background(Color.Cyan))
    }
) {
    Layout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Gray),
        content = content
    ) { measureable: List<Measurable>, constraints: Constraints ->
        val minWidthHeightZeroConstraint = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measureable.map { it.measure(minWidthHeightZeroConstraint) }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            var y = 0
            placeables.forEach { placeable ->
                Log.d(TAG, "y $y")
                placeable.placeRelative(x = 0, y = y)
                y += placeable.height
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LayoutSampleModifyColumnPreview() {
    LayoutSampleModifyColumn()
}