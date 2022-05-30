package com.example.graduationwork.activity.training

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivitySelectedTrainingConcreteBinding
import android.widget.Toast
import com.example.graduationwork.activity.profile.ProfileActivity
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class SelectedTrainingConcreteActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivitySelectedTrainingConcreteBinding
    private lateinit var youtubePlayer: YouTubePlayerView
    private lateinit var videoId: String
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    private val youtubeApikey = "AIzaSyD0paaYm8VCg_vu-yysciiSbqsT0jcXgrw"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTrainingConcreteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getStringExtra("id")) {
            "1" -> {
                videoId = "BeVVY2vYyqA"
                binding.author.text = "Перевод видео: YouTube канал SMART TRAINING \nАвтор оригинального видео: Джефф Кавальер "

            }
            "2" -> {
                videoId = "VGNuRAtOQMs"
                binding.author.text = "Перевод видео: YouTube канал Invincible \nАвтор оригинального видео: Джефф Ниппард "

            }
            "3" -> {
                videoId = "9XO6EHnfYFI"
                binding.author.text = "Перевод видео: YouTube канал SciApp \nАвтор оригинального видео: Джефф Кавальер "

            }
            "4" -> {
                videoId = "z45rM0u2-tY"
                binding.author.text = "Перевод видео: YouTube канал GimFit INFO \nАвтор оригинального видео: Крис Хериа "

            }
            "5" -> {
                videoId = "Mol6gQ-qrhs"
                binding.author.text = "Перевод видео: YouTube канал SciApp \nАвтор оригинального видео: Джефф Кавальер "

            }
            "6" -> {
                videoId = "OT9yrJOX-A0"
                binding.author.text = "Автор видео: YouTube канал PopSport"
            }
            "7" -> {
                videoId = "cvNtumpDzA8"
                binding.author.text = "Автор видео: YouTube канал ФитоМашка"
            }
            "8" -> {
                videoId = "479FXxs89mQ"
                binding.author.text = "Автор видео: Котельникова Дарья, YouTube канал IMAGINE fitness"
            }
            "9" -> {
                videoId = "Qr6NCv-kXyk"
                binding.author.text = "Автор видео: Юлия Пегасова, YouTube канал IMAGINE fitness"
            }
            "10" -> {
                videoId = "xuMrxeDLAF4"
                binding.author.text = "Автор видео: YouTube канал 22x22"
            }
            else -> { }
        }

        binding.title.text = intent.getStringExtra("title")
        binding.description.text = intent.getStringExtra("description")
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.goToProfile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }
            }
            true
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        youtubePlayer = findViewById(R.id.yotubePlayer1)

        youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1?.loadVideo(videoId)
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {

                val text = "Failed"
                Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
            }

        }
        youtubePlayer.initialize(youtubeApikey, youtubePlayerInit)

      }
}