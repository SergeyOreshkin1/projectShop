package com.example.graduationwork.activity.profile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationwork.R
import com.example.graduationwork.adapter.TypeAdapter
import com.example.graduationwork.data.entity.Type
import com.example.graduationwork.databinding.ActivityVideoBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class VideoActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivityVideoBinding
    private val typeAdapter by lazy { TypeAdapter() }
    // id видео
    lateinit var videoId: String
    lateinit var type: String
    // Представление для отображения видео YouTube
    private lateinit var youtubePlayer: YouTubePlayerView
    lateinit var typeProductText: String

    // Определение интерфейса, вызываемого при при успешной или неудачной инициализации
    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    // API ключ проекта
    val youtubeApikey = "AIzaSyD0paaYm8VCg_vu-yysciiSbqsT0jcXgrw"

    override fun onStart() {
        super.onStart()
        val intent1 = intent?.getStringExtra("videoId")
        videoId = intent1?.toString() ?: "1U30x87sN88"
        val intent2 = intent?.getStringExtra("type")
        type = intent2?.toString() ?: "Протеин"
        val intent3 = intent?.getStringExtra("typeProductText")
        typeProductText = intent3?.toString() ?: resources.getString(R.string.protein)
        binding.textType.setText(type)
        binding.typeProductText.setText(typeProductText)

        youtubePlayer.initialize(youtubeApikey, youtubePlayerInit)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        youtubePlayer = findViewById(R.id.yotubePlayer1)

        binding.textType.inputType = InputType.TYPE_NULL
        binding.textType.setOnClickListener {
            typeBottomSheetDialog()
        }

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


    }

    private fun typeBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewBottomSheet)
        rv.apply {
            layoutManager = LinearLayoutManager(
                this@VideoActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = typeAdapter
            typeAdapter.submitList(
                mutableListOf(
                    Type("Протеин"),
                    Type("Креатин"), Type("Глютамин"), Type("Гейнер")
                )
            )

            typeAdapter.listener = {
                when (it.value) {
                    "Протеин" -> {
                        videoId = "1U30x87sN88"
                        typeProductText = resources.getString(R.string.protein)
                    }
                    "Креатин" -> {
                        videoId = "64SRw6pw2rM"
                        typeProductText = resources.getString(R.string.creatine)
                    }
                    "Глютамин" -> {
                        videoId = "mQ2qB1wr7pw"
                        typeProductText = resources.getString(R.string.gluten)
                    }
                    "Гейнер" -> {
                        videoId = "Ip-eMIwKre8"
                        typeProductText = resources.getString(R.string.gainer)
                    }
                    else -> {}
                }
                type = it.value
                binding.textType.setText(it.value)
                dialog.dismiss()
                val intent = Intent(
                    this@VideoActivity,
                    VideoActivity::class.java
                )
                intent.putExtra("videoId", videoId)
                intent.putExtra("type", type)
                intent.putExtra("typeProductText",typeProductText)
                startActivity(intent)
                finish()
            }
            dialog.show()
        }
    }
}