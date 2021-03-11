package com.epitech.diabetips.storages

import android.content.Context
import com.epitech.diabetips.R

data class NotificationPredictionEnabledObject(var enabled: Boolean = true) : NotificationObject() {

    constructor(notificationObject: NotificationObject) : this() {
        id = notificationObject.id
        time = notificationObject.time
        read = notificationObject.read
        type = notificationObject.type
        data = notificationObject.data.toString()
    }

    override fun send(context: Context, title: String?, body: String?) {
        displayNotification(context, title, body)
    }

    override fun getNotificationChannel(context: Context) : String {
        return context.getString(R.string.notification_channel_AI)
    }

    override fun getTitle(context: Context) : String {
        return context.getString(R.string.notification_prediction_enabled_title)
    }

    override fun getBody(context: Context) : String {
        return context.getString(R.string.notification_prediction_enabled_body)
    }
}