package com.example.graduationwork.activity.training

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.graduationwork.data.entity.Model
import com.example.graduationwork.R
import com.example.graduationwork.adapter.TrainingSelectAdapter
import com.example.graduationwork.databinding.ActivitySelectedTrainingBinding
import kotlinx.android.synthetic.main.activity_selected_training.viewPager
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class SelectedTrainingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedTrainingBinding
    private val trainingAdapter by lazy { TrainingSelectAdapter() }

    private lateinit var modelList: MutableList<Model>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_training)

        val clientType: String = intent.getStringExtra("clientType").toString()

        binding = ActivitySelectedTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.goToProfile -> {

                    finish()
                }
            }
            true
        }
        binding.toolbar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@SelectedTrainingActivity, TrainingActivity::class.java
                )
            )
            finish()

          }

        if (clientType == resources.getString(R.string.titleChoiceMan)) {
            modelList = mutableListOf(
                Model(
                    "Упражнения для рук",
                    "Самая быстрая тренировка рук | Джефф Кавальер",
                    "4 минуты",
                    R.drawable.arms,"1"
                ),
                Model(
                    "Упражнения для ног",
                    "Приседания: развитие выносливости и силы. Как приседать со штангой с идеальной техникой",
                    "9 минут",
                    R.drawable.legs,"2"
                ),
                Model(
                    "Упражнения на грудные мышцы",
                    "Тренировка всей груди в домашних условиях",
                    "4 минуты",
                    R.drawable.breast,"3"
                ),
                Model(
                    "Упражнения на пресс",
                    "тренировка для 6 кубиков пресса!",
                    "11 минут",
                    R.drawable.press,"4"
                ),
                Model(
                    "Проработка мышц спины",
                    "Лучшее видео о тренировке спины (Проработай все мышцы)",
                    "11 минут",
                    R.drawable.backk,"5"
                )
            )
        } else {

            binding.title.text = resources.getString(R.string.titleChoiceWoman)

            modelList = mutableListOf(
                Model("Упражнения на пресс", "Качаем пресс дома. Упражнения на пресс в домашних условиях | PopSport", "13 минут", R.drawable.presswoman,"6"),
                Model(
                    "Упражнения для ног",
                    "Силовая тренировка ног и ягодиц для девушек",
                    "20 минут",
                    R.drawable.legswoman,"7"
                ),
                Model(
                    "Шпагат",
                    "Как сесть на шпагат в домашних условиях",
                    "21 минута",
                    R.drawable.shpagat,"8"
                ),
                Model(
                    "Йога",
                    "Йога для бодрости в домашних условиях. Kомплекс на все тело для начинающих. Позы йоги",
                    "30 минут",
                    R.drawable.womanyogaa,"9"
                ),
                Model(
                    "Упражнения для спины",
                    "Красивая осанка за 10 минут в день. Тренировка для укрепления мышц спины дома",
                    "12 минут",
                    R.drawable.backwoman,"10"
                )
            )

        }
        trainingAdapter.submitList(modelList)

        viewPager.adapter = trainingAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        }.attach()
        trainingAdapter.listener = {
            val intent = Intent(this, SelectedTrainingConcreteActivity::class.java)
            intent.putExtra("title", it.title)
            intent.putExtra("id", it.id)
            intent.putExtra("description", it.description)
            startActivity(intent)
        }

    }

}