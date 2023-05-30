package com.example.homedeal.auth

import android.util.Log
import android.widget.TextView
import com.example.homedeal.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest


object AuthWithFirebase : Auth {

    private const val TAG = "AuthWithFirebase"
    private const val EMAIL_ALREADY_IN_USE = "L'adresse email est déjà utilisée par un autre compte."
    private const val INVALID_CREDENTIALS = "L'adresse email ou le mot de passe est incorrect."
    private const val WEAK_PASSWORD = "Le mot de passe est trop faible."
    private const val RECENT_LOGIN_REQUIRED = "L'utilisateur doit se reconnecter."
    private const val USER_DISABLED_OR_NOT_FOUND = "L'utilisateur n'existe pas ou a été désactivé."

    private val AUTH = FirebaseAuth.getInstance()
    override fun signIn(email: String, password: String, mTxtPassword: TextView,
                        mTxtEmail: TextView, onResult: TaskResult) {
        AUTH.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful){
                onResult.onSuccess()
            } else {
                handleFirebaseAuthExceptions(it.exception!!, mTxtPassword, mTxtEmail)
                onResult.onFailure(it.exception!!)
            }
        }
    }

    override fun signUp(email: String, username: String, password: String, mTxtPassword: TextView,
                        mTxtEmail: TextView, onResult: TaskResult) {
        AUTH.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                updateUsername(username) {
                    onResult.onSuccess()
                }
            } else {
                handleFirebaseAuthExceptions(it.exception!!, mTxtPassword, mTxtEmail)
                onResult.onFailure(it.exception!!)
            }
        }
    }

    override fun getCurrentUser(): User? {
        val user = AUTH.currentUser
        if (user != null) {
            return User(user.displayName, user.email)
        } else {
            return null
        }
    }

    override fun updatePassword(password: String, mTxtPassword: TextView,
                                mTxtEmail: TextView, onResult: TaskResult) {
        val user = AUTH.currentUser
        user!!.updatePassword(password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "User password updated.")
                onResult.onSuccess()
            }
            else {
                handleFirebaseAuthExceptions(it.exception!!, mTxtPassword, mTxtEmail)
                onResult.onFailure(it.exception!!)
            }
        }
    }

    override fun updateUsername(username: String, onResult: () -> Unit) {
        val user = AUTH.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult()
                }
            }
    }

    override fun signOut() {
        AUTH.signOut()
    }

    private fun handleFirebaseAuthExceptions(
        exception: Exception,
        mTxtPassword: TextView,
        mTxtEmail: TextView
    ) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                mTxtPassword.error = WEAK_PASSWORD
                mTxtPassword.requestFocus()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                mTxtEmail.error = INVALID_CREDENTIALS
                mTxtEmail.requestFocus()
            }
            is FirebaseAuthUserCollisionException -> {
                mTxtEmail.error = EMAIL_ALREADY_IN_USE
                mTxtEmail.requestFocus()
            }
            is FirebaseAuthInvalidUserException -> {
                mTxtEmail.error = USER_DISABLED_OR_NOT_FOUND
                mTxtEmail.requestFocus()
            }
            is FirebaseAuthRecentLoginRequiredException -> {
                mTxtPassword.error = RECENT_LOGIN_REQUIRED
                mTxtPassword.requestFocus()
            }
            else -> Log.e(TAG, exception.message!!)
        }
    }


}