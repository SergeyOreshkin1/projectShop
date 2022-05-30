package com.example.graduationwork.activity.training

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivityTabataInfoBinding
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class TabataInfoActivity : AppCompatActivity() {


    private lateinit var binding: ActivityTabataInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabataInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        binding.toolbar.setNavigationOnClickListener {

            finish()

        }
    }
}