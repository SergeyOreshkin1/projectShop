package com.example.graduationwork.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivitySignInBinding
import com.example.graduationwork.data.local.SharedPreference
import com.example.graduationwork.activity.BaseActivity
import com.example.graduationwork.activity.catalog.CategoriesActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onBackPressed() {
        exitDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        val login = intent.getStringExtra("login")
        val password = intent.getStringExtra("password")
        binding.textLogin.setText(login)
        binding.textPassword.setText(password)

        val sharedPreference = SharedPreference(this)
        val uid: String? = sharedPreference.getValueString("UID")
        if (uid != null) {
            startActivity(
                Intent(this@SignInActivity, CategoriesActivity::class.java)
            )
            finish()
        }

        binding.buttonSignInToUp.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }

        binding.buttonForgotPassword.setOnClickListener {
            startActivity(Intent(this@SignInActivity, ForgotPasswordActivity::class.java))
        }

        binding.buttonSignIn.setOnClickListener {
            authUser(sharedPreference)
        }
    }

    private fun login(): Boolean {
        val login = binding.layoutLogin
        val lText = binding.textLogin.text.toString().trim()
        return if (!Patterns.EMAIL_ADDRESS.matcher(lText).matches()) {
            login.error = resources.getString(R.string.error_msg_sign_up)
            false
        } else {
            login.error = null
            true
        }
    }

    private fun password(): Boolean {

        val password = binding.layoutPassword
        val pText = binding.textPassword.text.toString().trim()
        return if (pText.length < 6)
         {
            password.error = resources.getString(R.string.error_msg_sign_in)
            false
        } else {
            password.error = null
            true
        }
    }

    private fun authUser(sharedPreference: SharedPreference) {
        if (login() && password()) {

            showProgressDialog(resources.getString(R.string.wait))

            val login: String = binding.textLogin.text.toString().trim()
            val password: String = binding.textPassword.text.toString().trim()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(login, password)
                .addOnCompleteListener { task ->

                    hideProgressDialog()

                    if (task.isSuccessful) {
                        sharedPreference.save("UID", task.result!!.user!!.uid)
                        startActivity(
                            Intent(this@SignInActivity, CategoriesActivity::class.java)
                        )
                        finish()
                        showSnackBar("Вы успешно авторизовались.", false)
                    } else {
                        showSnackBar(task.exception!!.message.toString(), true)
                    }
                }
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
