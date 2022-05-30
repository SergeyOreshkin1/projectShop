package com.example.graduationwork.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivitySignUpBinding
import com.example.graduationwork.activity.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class SignUpActivity : BaseActivity() {

    override fun onBackPressed() {
        startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        finish()
    }

    private lateinit var binding: ActivitySignUpBinding

   // private val pattern = "[0-9]".toRegex()
   // private val pattern2 = "[A-Z]|[А-Я]".toRegex()
  //  private val pattern3 = "[a-z]|[а-я]".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }

        binding.buttonSignUp.setOnClickListener {
            registerUser()
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
       /* return if (pText.length < 6 || !pattern.containsMatchIn(pText) ||
            !pattern2.containsMatchIn(pText) ||
            !pattern3.containsMatchIn(pText) */
        return if (pText.length < 6)
         {
            password.error = resources.getString(R.string.error_msg_sign_in)
            false
        } else {
            password.error = null
            true
        }
    }

    private fun repeatPassword(): Boolean {
        val repeatPassword = binding.layoutRepeatPassword
        return if (binding.textPassword.text.toString().trim() !=
            binding.textRepeatPassword.text.toString().trim()
        ) {
            repeatPassword.error = resources.getString(R.string.error_msg_passwords_mismatch)
            false
        } else {
            repeatPassword.error = null
            true
        }
    }

    private fun registerUser() {
        if (login() && password() && repeatPassword()) {
            // прогресс обработки
            showProgressDialog(resources.getString(R.string.wait))

            val login: String = binding.textLogin.text.toString().trim()
            val password: String = binding.textPassword.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener { task -> hideProgressDialog()

                    if (task.isSuccessful) {
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document("countUsers")
                            .update("count", FieldValue.increment(1))
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showSnackBar(
                                        "Вы успешно зарегистрировались.",
                                        false
                                    )
                                    FirebaseAuth.getInstance().signOut()
                                    val intent =
                                        Intent(this@SignUpActivity, SignInActivity::class.java)
                                    intent.putExtra("login", login)
                                    intent.putExtra("password", password)
                                    startActivity(intent)
                                    finish()
                                }
                                else {
                                    showSnackBar(task.exception!!.message.toString(), true)
                                }
                            }
                    } else {
                        showSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
}
