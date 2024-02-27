package com.sample.feature.compose_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private fun getRandomColor(): Color {
    val randomColorValue = Random.nextLong(from = 0xFF000000, until = 0xFFFFFFFF)
    return Color(randomColorValue)
}

@Composable
fun LayoutSampleModifyGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    itemPadding: PaddingValues = PaddingValues(10.dp),
    content: @Composable () -> Unit = {
        val sizes = listOf(150.dp, 200.dp, 300.dp, 150.dp, 100.dp, 100.dp)
        val createBox: @Composable (Dp) -> Unit = { size ->
            Box(
                modifier = Modifier
                    .size(size)
                    .background(getRandomColor())
            )
        }
        sizes.forEach { size ->
            createBox(size)
        }
    }
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val left = itemPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx()
        val right = itemPadding.calculateRightPadding(LayoutDirection.Ltr).roundToPx()
        val top = itemPadding.calculateTopPadding().roundToPx()
        val bottom = itemPadding.calculateBottomPadding().roundToPx()

        val placeables: List<List<Placeable>> = measurables.map { measurable ->
            measurable.measure(itemConstraints)
        }.chunked(columns)

        val gridHeight = placeables.sumOf { gridPlaceables -> gridPlaceables.maxOf { it.height } }

        layout(constraints.maxWidth, gridHeight) {
            var y = top
            placeables.forEach { currentColumnPlaceables ->
                val currentColumnMaxHeight = currentColumnPlaceables.maxOf { it.height }
                var x = left
                currentColumnPlaceables.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width + right
                }
                y += currentColumnMaxHeight + bottom
            }
        }
    }
}

@Preview
@Composable
private fun LayoutSampleModifyGridPreview() {
    LayoutSampleModifyGrid()
}