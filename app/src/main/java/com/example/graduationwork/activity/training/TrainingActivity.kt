package com.example.graduationwork.activity.training

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivityTrainingBinding
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class TrainingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        var clientType: String
        binding.timer.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TimerActivity::class.java
                )
            )
            finish()

        }

        radioGroup.setOnCheckedChangeListener { _, optionId ->
            run {
                when (optionId) {
                    R.id.Man -> {

                        clientType = resources.getString(R.string.titleChoiceMan)

                        binding.trainNext.setOnClickListener {

                                val intent = Intent(
                                    this,
                                    SelectedTrainingActivity::class.java
                                )
                            intent.putExtra("clientType",clientType)
                            startActivity(intent)
                            finish()
                        }
                        animate()
                        binding.image.postDelayed({
                            binding.image.setImageResource(R.drawable.man)
                            animateAlphaTo1()
                            binding.title.text = resources.getString(R.string.titleChoiceMan)

                        }, 1500)

                    }
                    R.id.Woman -> {

                        clientType = resources.getString(R.string.titleChoiceWoman)

                        binding.trainNext.setOnClickListener {

                            val intent = Intent(
                                this,
                                SelectedTrainingActivity::class.java
                            )
                            intent.putExtra("clientType",clientType)
                            startActivity(intent)
                            finish()
                        }
                        animate()
                        binding.image.postDelayed({
                            binding.image.setImageResource(R.drawable.ball)
                            animateAlphaTo1()
                            binding.title.text = resources.getString(R.string.titleChoiceWoman)

                        }, 1500)
                    }
                }
            }
        }
    }

    private fun animate(){
        binding.trainNext.animate().alpha(1.0f)
        binding.image.animate().alpha(0.0f).duration = 1500
        binding.imageChoice.animate().alpha(0.0f).duration = 1500
        binding.title.animate().alpha(0.0f).duration = 1500
    }

    private fun animateAlphaTo1(){
        binding.title.animate().alpha(1.0f).duration = 1500
        binding.image.animate().alpha(1.0f).duration = 1500
        binding.imageChoice.animate().alpha(1.0f).duration = 1500
    }
}