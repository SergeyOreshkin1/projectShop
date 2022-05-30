package com.example.graduationwork.activity.auth

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivityForgotPasswordBinding
import com.example.graduationwork.activity.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.button.setOnClickListener {
            val login: String = binding.textLogin.text.toString().trim()
            if (login.isEmpty()) {
                showSnackBar(resources.getString(R.string.error_msg_empty), true)
            } else {
                showProgressDialog(resources.getString(R.string.wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(login)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                resources.getString(R.string.email_sent_success),
                                Toast.LENGTH_LONG
                            ).show()

                            finish()

                        } else {
                            showSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }
    }
}