package com.sample.feature.compose_layout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun LayoutSampleModifyRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {
        Text(text = "first")
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = "second")
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = "third")
    }
) {
    Layout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Gray),
        content = content
    ) { measureable: List<Measurable>, constraints: Constraints ->
        val placeables = measureable.map { it.measure(constraints.copy(minWidth = 0, minHeight =  0)) }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            var x = 0
            placeables.forEach {
                Log.d(TAG, "place x $x ")
                it.placeRelative(x, y = 0)
                x += it.width
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LayoutSampleModifyRowPreview() {
    LayoutSampleModifyRow()
}

val TAG = "TAG"