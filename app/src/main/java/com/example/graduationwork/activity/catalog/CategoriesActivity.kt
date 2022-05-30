package com.example.graduationwork.activity.catalog

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivityCategoriesBinding
import com.example.graduationwork.activity.profile.ProfileActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding

    override fun onBackPressed() {
        exitDialog()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.goToProfile -> {
                    startActivity(
                        Intent(
                            this@CategoriesActivity, ProfileActivity::class.java
                        )
                    )
                    finish()
                }
            }
            true
        }

        binding.muscles.setOnClickListener {
            val intent = Intent(this@CategoriesActivity, CatalogActivity::class.java)
            intent.putExtra("category", "1")
            startActivity(intent)

        }
        binding.weight.setOnClickListener {
            val intent = Intent(this@CategoriesActivity, CatalogActivity::class.java)
            intent.putExtra("category", "2")
            startActivity(intent)

        }
        binding.immun.setOnClickListener {
            val intent = Intent(this@CategoriesActivity, CatalogActivity::class.java)
            intent.putExtra("category", "3")
            startActivity(intent)

        }
        binding.sale.setOnClickListener {
            val intent = Intent(this@CategoriesActivity, CatalogActivity::class.java)
            intent.putExtra("category", "4")
            startActivity(intent)

        }

    }

    private fun exitDialog() {
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialAlertDialog)

        dialog.setMessage(R.string.dialog_message_exit)

        dialog.setPositiveButton(R.string.exit) { _, _ ->
            finish()
        }

        dialog.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        dialog.show()
    }
}