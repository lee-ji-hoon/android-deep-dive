package com.sample.playground

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.sample.playground.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO 우선 Window로 바로 보내는데 Compose Navigation 도입해두기
        startActivity(Intent(this, WindowSampleActivity::class.java))
    }
}