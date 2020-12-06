package com.epitech.diabetips.storages

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.ChatActivity
import com.epitech.diabetips.activities.NavigationActivity
import org.json.JSONException
import org.json.JSONObject

data class NotificationChatObject(
    var msg_id: String = "",
    var from: String = "") : NotificationObject() {

    constructor(notificationObject: NotificationObject) : this() {
        id = notificationObject.id
        time = notificationObject.time
        read = notificationObject.read
        type = notificationObject.type
        data = notificationObject.data.toString()
        try {
            val jsonObject = JSONObject(data.toString())
            msg_id = jsonObject.getString("msg_id")
            from = jsonObject.getString("from")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun send(context: Context, title: String?, body: String?) {
            NavigationActivity.setUnreadMessage(true)
            ChatActivity.instance?.getLastMessage() ?: displayNotification(context, title, body)
    }

    override fun getNotificationChannel(context: Context) : String {
        return context.getString(R.string.notification_channel_chat)
    }

    override fun getTitle(context: Context) : String {
        return context.getString(R.string.notification_chat_title)
    }

    override fun getBody(context: Context) : String {
        return context.getString(R.string.notification_chat_body)
    }
}