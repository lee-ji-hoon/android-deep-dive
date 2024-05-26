package com.sample.feature.inset_animation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sample.feature.window.databinding.ActivityInsetAnimationSampleBinding


class InsetAnimationSampleActivity : AppCompatActivity() {

    private val binding by lazy { ActivityInsetAnimationSampleBinding.inflate(layoutInflater) }

    private val adapter = ItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.recyclerView.adapter = adapter
        val items = List(1000) { index ->
            Item(index, "Item $index")
        }
        adapter.submitList(items)
    }
}
