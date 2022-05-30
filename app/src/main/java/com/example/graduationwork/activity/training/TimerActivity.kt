package com.example.graduationwork.activity.training

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import android.os.CountDownTimer
import android.text.InputType
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationwork.activity.profile.ProfileActivity
import com.example.graduationwork.adapter.TypeAdapter
import com.example.graduationwork.data.entity.Type
import com.example.graduationwork.databinding.ActivityTimerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class TimerActivity : AppCompatActivity() {

    var start = 30_000L
    var timer = start

    var count = 1
    var name = R.raw.tabata

    private var mp: MediaPlayer? = null

    lateinit var countDownTimer: CountDownTimer

    private val typeAdapter by lazy { TypeAdapter() }
    lateinit var type: String
    lateinit var typeTimer: String
    lateinit var typeMelody: String
    private lateinit var binding: ActivityTimerBinding

    override fun onBackPressed() {
        stopMedia()
        startActivity(
            Intent(
                this,
                TrainingActivity::class.java
            )
        )
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding.toolbar.setNavigationOnClickListener {

            stopMedia()
            startActivity(
                Intent(
                    this,
                    TrainingActivity::class.java
                )
            )
            finish()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.goToProfile -> {
                    stopMedia()
                    finish()
                }
            }
            true
        }

        countDownTimer = object : CountDownTimer(timer,1000){

            override fun onFinish() {

                binding.btnMainPause.isEnabled = false

                val sb = Snackbar.make(findViewById(android.R.id.content),getString(R.string.timerMessage), Snackbar.LENGTH_LONG)
                sb.view.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TimerActivity,
                        R.color.snackBarError
                    )
                )

                sb.show()

            }

            override fun onTick(millisUntilFinished: Long) {
                timer = millisUntilFinished
                setTextTimer()
            }

        }

        binding.textTypeTimer.setText(resources.getString(R.string.timerStandart))
        binding.textType.setText(resources.getString(R.string.timerBase))
        binding.textSong.setText(resources.getString(R.string.melody1))

        setTextTimer()

        binding.btnTabataInfo.isVisible = false
        binding.btnTabataStart.isVisible = false
        binding.btnTabataRest.isVisible = false

        binding.btnMainStart.isVisible = true
        binding.btnMainPause.isVisible = true
        binding.btnMainRest.isVisible = true

        binding.btnMainPause.isEnabled = false
        binding.btnMainRest.isEnabled = false
        binding.btnMainStart.isEnabled = true

        binding.layoutType.isVisible = true
        binding.countRound.isVisible = false
        binding.songType.isVisible = false

        binding.textType.inputType = InputType.TYPE_NULL
        binding.textType.setOnClickListener {
            typeBottomSheetDialog()
        }

        binding.textTypeTimer.inputType = InputType.TYPE_NULL
        binding.textTypeTimer.setOnClickListener {
            typeBottomSheetDialogTypeTimer()
        }

        binding.textSong.inputType = InputType.TYPE_NULL
        binding.textSong.setOnClickListener {
            typeBottomSheetDialogTypeMelody()
        }

        binding.btnTabataInfo.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TabataInfoActivity::class.java
                )
            )

        }

    }

    fun on(view: View) {
        when(view.id){
            R.id.btn_main_start -> startTimer()
            R.id.btn_main_pause -> pauseTimer()
            R.id.btn_main_rest -> restTimer()
            R.id.btn_tabata_rest -> restTabata()
            R.id.btn_tabata_start -> startTabata()
        }
    }

    private fun startTimer() {

        binding.btnMainPause.isEnabled = true
        binding.btnMainRest.isEnabled = true
        binding.btnMainStart.isEnabled = false

        countDownTimer = object : CountDownTimer(timer,1000){

            override fun onFinish() {

                binding.btnMainPause.isEnabled = false

                if (mp == null) {
                    mp = MediaPlayer.create(this@TimerActivity, R.raw.timerstop)
                    mp?.start()
                }

                val sb = Snackbar.make(findViewById(android.R.id.content),getString(R.string.timerMessage), Snackbar.LENGTH_LONG)
                sb.view.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TimerActivity,
                        R.color.snackBarError
                    )
                )

                sb.show()

            }

            override fun onTick(millisUntilFinished: Long) {
                timer = millisUntilFinished
                setTextTimer()
            }

        }.start()
    }

    fun stopMedia(){

        if (mp != null){
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null

        }

    }

    private fun pauseTimer() {

        binding.btnMainPause.isEnabled = false
        binding.btnMainRest.isEnabled = true
        binding.btnMainStart.isEnabled = true

        countDownTimer.cancel()
    }

       private fun restTimer() {

        binding.btnMainPause.isEnabled = false
        binding.btnMainRest.isEnabled = false
        binding.btnMainStart.isEnabled = true

        stopMedia()

        countDownTimer.cancel()
        timer = start
        setTextTimer()
    }

       fun setTextTimer() {
        val m = (timer / 1000) / 60
        val s = (timer / 1000) % 60

        val format = String.format("%02d:%02d", m, s)

           binding.time.text = format
    }

    private fun typeBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewBottomSheet)
        rv.apply {
            layoutManager = LinearLayoutManager(
                this@TimerActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = typeAdapter
            typeAdapter.submitList(
                mutableListOf(
                    Type("30 секунд"),
                    Type("45 секунд"), Type("1 минута"), Type("2 минуты"), Type("3 минуты"),
                    Type("5 минут"), Type("10 минут"), Type("15 минут"), Type("20 минут"),
                    Type("30 минут"), Type("45 минут"), Type("1 час")
                )
            )

            typeAdapter.listener = {
                when (it.value) {
                    "30 секунд" -> {
                        start = 30_000L
                    }
                    "45 секунд" -> {
                        start = 45_000L

                    }
                    "1 минута" -> {
                        start = 60_000L

                    }
                    "2 минуты" -> {
                        start = 120_000L
                    }
                    "3 минуты" -> {
                        start = 180_000L
                    }
                    "5 минут" -> {
                        start = 300_000L
                    }
                    "10 минут" -> {
                        start = 600_000L
                    }
                    "15 минут" -> {
                        start = 900_000L
                    }
                    "20 минут" -> {
                        start = 1200_000L
                    }
                    "30 минут" -> {
                        start = 1800_000L
                    }
                    "45 минут" -> {
                        start = 2700_000L
                    }
                    "1 час" -> {
                        start = 3600_000L
                    }
                    else -> {}
                }
                type = it.value
                binding.textType.setText(it.value)
                restTimer()
                dialog.dismiss()

            }
            dialog.show()
        }
    }

    private fun typeBottomSheetDialogTypeTimer() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewBottomSheet)
        rv.apply {
            layoutManager = LinearLayoutManager(
                this@TimerActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = typeAdapter
            typeAdapter.submitList(
                mutableListOf(
                    Type(resources.getString(R.string.timerStandart)),
                    Type(resources.getString(R.string.timerTabata))

                )
            )

            typeAdapter.listener = {
                when (it.value) {
                    resources.getString(R.string.timerStandart) -> {
                        binding.btnTabataInfo.isVisible = false
                        binding.btnTabataStart.isVisible = false
                        binding.btnTabataRest.isVisible = false

                        binding.btnMainStart.isVisible = true
                        binding.btnMainPause.isVisible = true
                        binding.btnMainRest.isVisible = true

                        binding.btnMainPause.isEnabled = false
                        binding.btnMainRest.isEnabled = false
                        binding.btnMainStart.isEnabled = true

                        binding.layoutType.isVisible = true
                        binding.countRound.isVisible = false
                        start = 30_000L
                        binding.textType.setText(resources.getString(R.string.timerBase))
                        setTextTimer()

                        binding.songType.isVisible = false

                    }
                    resources.getString(R.string.timerTabata) -> {
                        binding.btnMainStart.isVisible = false
                        binding.btnMainPause.isVisible = false
                        binding.btnMainRest.isVisible = false

                        binding.btnTabataInfo.isVisible = true
                        binding.btnTabataStart.isVisible = true
                        binding.btnTabataRest.isVisible = true
                        binding.layoutType.isVisible = false

                        binding.btnTabataRest.isEnabled = false
                        binding.btnTabataStart.isEnabled = true
                        binding.btnTabataInfo.isEnabled = true
                        binding.countRound.text = ""
                        binding.textSong.setText(resources.getString(R.string.melody1))

                        binding.countRound.isVisible = true
                        start = 14_000L
                        timer = start
                        setTextTimer()

                        binding.songType.isVisible = true

                    }

                    else -> {}
                }
                typeTimer = it.value
                binding.textTypeTimer.setText(it.value)
                restTimer()
                stopMedia()

                if (mp == null) {
                    mp = MediaPlayer.create(this@TimerActivity, R.raw.tabata)

                }
                dialog.dismiss()

            }
            dialog.show()
        }
    }

    private fun typeBottomSheetDialogTypeMelody() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewBottomSheet)
        rv.apply {
            layoutManager = LinearLayoutManager(
                this@TimerActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = typeAdapter
            typeAdapter.submitList(
                mutableListOf(
                    Type(resources.getString(R.string.melody1)),
                    Type(resources.getString(R.string.melody2)), Type(resources.getString(R.string.melody3))
                )
            )

            typeAdapter.listener = {
                when (it.value) {
                    resources.getString(R.string.melody1) -> {

                        stopMedia()
                        if (mp == null) {
                            mp = MediaPlayer.create(this@TimerActivity, R.raw.tabata)

                        }
                        name = R.raw.tabata

                    }
                    resources.getString(R.string.melody2) -> {
                        stopMedia()
                        if (mp == null) {
                            mp = MediaPlayer.create(this@TimerActivity, R.raw.tabata2)

                        }
                        name = R.raw.tabata2


                    }
                    resources.getString(R.string.melody3) -> {
                        stopMedia()
                        if (mp == null) {
                            mp = MediaPlayer.create(this@TimerActivity, R.raw.tabata3)

                        }
                        name = R.raw.tabata3


                    }

                    else -> {}
                }
                typeMelody = it.value
                binding.textSong.setText(it.value)

                binding.btnTabataRest.isEnabled = false
                binding.btnTabataStart.isEnabled = true
                binding.btnTabataInfo.isEnabled = true

                binding.countRound.text = ""
                countDownTimer.cancel()
                start = 14_000L
                count = 1
                timer = start
                setTextTimer()
                dialog.dismiss()

            }
            dialog.show()
        }
    }


    @SuppressLint("SetTextI18n")
    private  fun startTabata() {

        binding.btnTabataInfo.isEnabled = true
        binding.btnTabataRest.isEnabled = true
        binding.btnTabataStart.isEnabled = false

        binding.countRound.text = resources.getString(R.string.Ready)

        if (mp != null) {
            mp?.start()
        }
        countDownTimer = object : CountDownTimer(timer,1000){

            override fun onFinish() {

                binding.btnMainPause.isEnabled = false

                binding.countRound.text = resources.getString(R.string.Raund) + " $count"

                    start = 20_000L
                    timer = start
                    create()

            }

            override fun onTick(millisUntilFinished: Long) {
                timer = millisUntilFinished
                setTextTimer()
            }

        }.start()

    }

    fun create(){
        countDownTimer = object : CountDownTimer(timer,1000){

            override fun onFinish() {

                countDownTimer.cancel()

                if (count <= 7) {

                binding.countRound.text = resources.getString(R.string.Rest)
                start = 10_000L
                timer = start
                countDownTimer = object : CountDownTimer(timer, 1000) {

                    override fun onFinish() {

                        count += 1
                        binding.countRound.text = resources.getString(R.string.Raund) + " $count"
                        countDownTimer.cancel()
                        start = 20_000L
                        timer = start
                        create()

                    }

                    override fun onTick(millisUntilFinished: Long) {
                        timer = millisUntilFinished
                        setTextTimer()
                    }

                }.start()
            }
                else{
                    binding.countRound.text = resources.getString(R.string.End)
                    stopMedia()
                }
                }

                override fun onTick(millisUntilFinished: Long) {
                    timer = millisUntilFinished
                    setTextTimer()
                }


        }.start()
    }

    private fun restTabata() {

        binding.btnTabataRest.isEnabled = false
        binding.btnTabataStart.isEnabled = true
        binding.btnTabataInfo.isEnabled = true

        binding.countRound.text = ""
        countDownTimer.cancel()
        start = 14_000L
        count = 1
        timer = start
        setTextTimer()
        stopMedia()
        if (mp == null) {
            mp = MediaPlayer.create(this@TimerActivity, name)


        }
    }
 }