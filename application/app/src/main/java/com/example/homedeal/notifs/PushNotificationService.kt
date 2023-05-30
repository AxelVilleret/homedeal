package com.example.homedeal.notifs

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging

class PushNotificationService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "PushNotificationService"
        const val MAIN_TOPIC = "allUsers"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.containsKey("unsubscribeFromTopic")) {
            val topic = remoteMessage.data["unsubscribeFromTopic"]
            if (topic != MAIN_TOPIC && topic != null) {
                Firebase.messaging.unsubscribeFromTopic(topic)
            }
        }
    }
}
