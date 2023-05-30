package com.example.homedeal.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import java.lang.Exception

interface TaskResult {
    fun onSuccess()
    fun onFailure(exception: Exception)
}