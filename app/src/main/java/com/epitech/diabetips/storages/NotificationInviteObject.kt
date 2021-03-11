package com.epitech.diabetips.storages

import android.content.Context
import com.epitech.diabetips.R
import org.json.JSONException
import org.json.JSONObject

data class NotificationInviteObject(
    var from_uid: String = "") : NotificationObject() {

    constructor(notificationObject: NotificationObject) : this() {
        id = notificationObject.id
        time = notificationObject.time
        read = notificationObject.read
        type = notificationObject.type
        data = notificationObject.data.toString()
        try {
            val jsonObject = JSONObject(data.toString())
            from_uid = jsonObject.getString("from_uid")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun send(context: Context, title: String?, body: String?) {
        displayNotification(context, title, body)
    }

    override fun getNotificationChannel(context: Context) : String {
        return context.getString(R.string.notification_channel_invite)
    }

    override fun getTitle(context: Context) : String {
        return context.getString(R.string.notification_invite_title)
    }

    override fun getBody(context: Context) : String {
        return context.getString(R.string.notification_invite_body)
    }
}