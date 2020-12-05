package com.epitech.diabetips.storages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.MainActivity
import com.epitech.diabetips.activities.NotificationActivity
import com.epitech.diabetips.services.NotificationService
import java.io.Serializable

open class NotificationObject (
    var id: String = "",
    var time: String = "",
    var read: Boolean = false,
    var type: String = "",
    var data: Any? = null) : Serializable {

    enum class Type { chat_message, user_invite, test }

    open fun send(context: Context, title: String? = null, body: String? = null) {
        getTypedNotification().send(context, title, body)
    }

    protected fun displayNotification(context: Context, title: String? = null, body: String? = null) {
        val channelId = getNotificationChannel(context)
        val intent = Intent(context, if (MainActivity.running) NotificationActivity::class.java else MainActivity::class.java)
            .putExtra(context.getString(R.string.param_notification), this)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_diabetips)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setColorized(true)
            .setContentTitle(title ?: getTitle(context))
            .setContentText(body ?: getBody(context))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT))

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0, notificationBuilder.build())
        markAsRead()
    }

    fun markAsRead() {
        if (!read && MainActivity.running) {
            read = true
            NotificationService.instance.remove<NotificationObject>(id).subscribe()
        }
    }

    fun getTypedNotification() : NotificationObject {
        return when (type) {
            Type.user_invite.name -> NotificationInviteObject(this)
            Type.chat_message.name -> NotificationChatObject(this)
            else -> NotificationTestObject(this)
        }
    }

    open fun getNotificationChannel(context: Context) : String {
        return context.getString(R.string.notification_channel_default)
    }

    open fun getTitle(context: Context) : String {
        return context.getString(R.string.notification_title)
    }

    open fun getBody(context: Context) : String {
        return context.getString(R.string.notification_body)
    }
}