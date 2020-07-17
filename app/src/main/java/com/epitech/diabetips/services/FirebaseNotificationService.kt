package com.epitech.diabetips.services

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ComponentInfo
import android.util.Log
import com.epitech.diabetips.activities.NavigationActivity
import com.epitech.diabetips.storages.FCMTokenObject
import com.epitech.diabetips.storages.NotificationObject
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class FirebaseNotificationService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        Log.d("FirebaseNotification", "From: ${remoteMessage.from}")
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FirebaseNotification", "Message data payload: ${remoteMessage.data}")
            Gson().fromJson(remoteMessage.data["notification"].toString(), NotificationObject::class.java).send(this)
        }
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("FirebaseNotification", "Message Notification Body: ${it.body}")
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("FirebaseNotification", "New token: $token")
        sendRegistrationToServer(token)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        if (token != null) {
            NotificationService.instance.register(FCMTokenObject(token)).doOnSuccess {
                if (it.second.component2() == null) {
                    Log.d("FirebaseNotification", "Registered new device")
                } else {
                    Log.d("FirebaseNotification", it.second.component2()!!.exception.message.toString())
                }
            }.subscribe()
        }
    }
}