package com.sample.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.sample.playground.navigation.PlaygroundNavigation
import com.sample.playground.theme.PlaygroundTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaygroundTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PlaygroundNavigation()
                }
            }
        }
    }
}