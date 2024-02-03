package com.sample.playground

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.sample.feature.window.WindowSampleActivity
import com.sample.playground.navigation.PlaygroundMainScreen
import com.sample.playground.navigation.PlaygroundNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PlaygroundNavigation()
            }
        }
    }
}