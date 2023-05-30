package com.example.homedeal.notifs

import android.util.Log
import com.example.homedeal.SplashActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class FirebaseNotification : Notification {

    companion object {
        private const val TAG = "FirebaseNotification"
        private const val MAIN_TOPIC = "allUsers"
        private val MESSAGING = Firebase.messaging
    }


    override fun subscribeToTopic(topic: String) {
        MESSAGING.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Abonnement réussi"
                if (!task.isSuccessful) {
                    msg = "Abonnement échoué"
                }
                Log.d(TAG, msg)
            }
    }
}