package com.example.homedeal
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.auth.TaskResult
import com.example.homedeal.databinding.ActivitySignupBinding
import com.example.homedeal.utils.ViewUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.lang.Exception

class SignupActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "SignupActivity"
        private const val TITLE = "S'inscrire"
        private val AUTH: Auth = AuthWithFirebase
    }

    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailview = binding.signupEmail
        val usernameView = binding.signupName
        val passwordView = binding.signupPassword
        val confirmPasswordView = binding.signupConfirm

        supportActionBar?.title = TITLE
        binding.signupButton.setOnClickListener{
            val email = emailview.text.toString()
            val username = usernameView.text.toString()
            val password = passwordView.text.toString()
            val confirmPassword = confirmPasswordView.text.toString()
            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){
                    AUTH.signUp(email, username, password, passwordView, emailview, object : TaskResult {
                        override fun onSuccess() {
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onFailure(exception: Exception) {}
                    })
                } else {
                    passwordView.error = ViewUtils.PASSWORDS_DONT_MATCH
                    confirmPasswordView.error = ViewUtils.PASSWORDS_DONT_MATCH
                }
            } else {
                if (email.isEmpty()){
                    emailview.error = ViewUtils.FIELD_REQUIRED
                    emailview.requestFocus()
                }
                if (username.isEmpty()){
                    usernameView.error = ViewUtils.FIELD_REQUIRED
                    usernameView.requestFocus()
                }
                if (password.isEmpty()){
                    passwordView.error = ViewUtils.FIELD_REQUIRED
                    passwordView.requestFocus()
                }
                if (confirmPassword.isEmpty()){
                    confirmPasswordView.error = ViewUtils.FIELD_REQUIRED
                    confirmPasswordView.requestFocus()
                }
            }
        }
        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        passwordView.setOnTouchListener { v, event ->
            ViewUtils.setPasswordInputType(event, v as EditText)
            false
        }

        confirmPasswordView.setOnTouchListener { v, event ->
            ViewUtils.setPasswordInputType(event, v as EditText)
            false
        }

        emailview.setOnFocusChangeListener { v, hasFocus ->
            ViewUtils.alertEmptyField(hasFocus, v as EditText)
        }

        usernameView.setOnFocusChangeListener { v, hasFocus ->
            ViewUtils.alertEmptyField(hasFocus, v as EditText)
        }

        passwordView.setOnFocusChangeListener { v, hasFocus ->
            ViewUtils.alertEmptyField(hasFocus, v as EditText)
        }

        confirmPasswordView.setOnFocusChangeListener { v, hasFocus ->
            ViewUtils.alertEmptyField(hasFocus, v as EditText)
        }
    }

}
