package com.example.homedeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.notifs.FirebaseNotification
import com.example.homedeal.notifs.Notification
import com.example.homedeal.notifs.PushNotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
        private val AUTH: Auth = AuthWithFirebase
        private val NOTIF : Notification = FirebaseNotification()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        NOTIF.subscribeToTopic(PushNotificationService.MAIN_TOPIC)

        Handler(Looper.getMainLooper()).postDelayed({
            if (AUTH.getCurrentUser() != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportActionBar?.show()
    }
}
