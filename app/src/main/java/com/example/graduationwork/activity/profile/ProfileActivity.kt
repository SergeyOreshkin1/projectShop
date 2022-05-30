package com.example.graduationwork.activity.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.graduationwork.R
import com.example.graduationwork.activity.auth.SignInActivity
import com.example.graduationwork.activity.catalog.CategoriesActivity
import com.example.graduationwork.activity.training.TrainingActivity
import com.example.graduationwork.data.local.SharedPreference
import com.example.graduationwork.databinding.ActivityProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class ProfileActivity : AppCompatActivity() {

    private var name: String? = ""

    private var avatar: String? = ""
    private lateinit var binding: ActivityProfileBinding

    override fun onBackPressed() {
        exitDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding.ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP


        val user = Firebase.auth.currentUser
        if (user != null) {
            name = user.displayName

        }
        binding.tvNameProfile.text = name

        getImage()
        binding.cardViewOrders.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    OrdersActivity::class.java
                )
            )


        }
        binding.cardViewShop.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.cardViewExit.setOnClickListener { exitDialog() }
        binding.cardViewSettings.setOnClickListener {
            val intent = Intent(
                this,
                SettingsActivity::class.java
            )
            intent.putExtra("name", name)
            intent.putExtra("avatar", avatar)
            startActivity(intent)
        }
        binding.cardViewVideo.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    VideoActivity::class.java
                )
            )

        }
        binding.cardViewVideoTraining.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TrainingActivity::class.java
                )
            )

        }

    }

    private fun exitDialog() {
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialAlertDialog)

        dialog.setMessage(R.string.dialog_message)

        dialog.setPositiveButton(R.string.exit) { _, _ ->
            val sharedPreference = SharedPreference(this)
            sharedPreference.delete()
            Firebase.auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        dialog.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getImage() {

        val sharedPreference = SharedPreference(this)
        val fileName = sharedPreference.getValueString("UID")
        val imageRef = FirebaseStorage.getInstance().reference.child("images/$fileName")
        imageRef.downloadUrl.addOnSuccessListener { Uri ->
            if (Uri != null) {
                binding.profileAvatarLayout.visibility = GONE
                avatar = Uri.toString()
                Glide.with(this)
                    .load(avatar)
                    .into(binding.ivProfile)
            } else binding.profileAvatarLayout.visibility = VISIBLE
        }
    }
}