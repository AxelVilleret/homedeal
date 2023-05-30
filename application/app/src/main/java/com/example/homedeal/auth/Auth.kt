package com.example.homedeal.auth

import android.widget.TextView
import com.example.homedeal.model.User

interface Auth {

    fun signIn(email: String, password: String, mTxtPassword: TextView,
               mTxtEmail: TextView, onResult : TaskResult)

    fun signUp(email: String, username : String, password : String, mTxtPassword: TextView,
               mTxtEmail: TextView, onResult: TaskResult)

    fun getCurrentUser(): User?

    fun updatePassword(password: String, mTxtPassword: TextView,
                       mTxtEmail: TextView, onResult: TaskResult)

    fun updateUsername(username: String, onResult: () -> Unit)

    fun signOut()
}