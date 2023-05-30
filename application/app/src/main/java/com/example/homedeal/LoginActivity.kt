package com.example.homedeal
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.auth.TaskResult
import com.example.homedeal.databinding.ActivityLoginBinding
import com.example.homedeal.utils.ViewUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
        private const val TITLE = "Se connecter"
        private val AUTH: Auth = AuthWithFirebase
    }

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = TITLE
        val emailView = binding.loginEmail
        val passwordView = binding.loginPassword

        binding.loginButton.setOnClickListener {
            val email = emailView.text.toString()
            val password = passwordView.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                AUTH.signIn(email, password, passwordView,
                    emailView, object : TaskResult {
                    override fun onSuccess() {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    override fun onFailure(exception: Exception) {}
                })
            } else {
                if (email.isEmpty()){
                    emailView.error = ViewUtils.FIELD_REQUIRED
                    emailView.requestFocus()
                }
                if (password.isEmpty()){
                    passwordView.error = ViewUtils.FIELD_REQUIRED
                    passwordView.requestFocus()
                }
            }
        }

        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }

        passwordView.setOnTouchListener { v, event ->
            ViewUtils.setPasswordInputType(event, v as EditText)
            false
        }
    }
}
