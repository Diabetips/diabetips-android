package com.epitech.diabetips.storages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.MainActivity
import com.epitech.diabetips.activities.NotificationActivity
import com.epitech.diabetips.services.NotificationService
import java.io.Serializable

data class NotificationObject (
    var id: String = "",
    var time: String = "",
    var read: Boolean = false,
    var type: String = "",
    var data: Any? = null) : Serializable {

    fun send(context: Context) {
        read = read or MainActivity.running
        val channelId = context.getString(R.string.default_notification_channel_id)
        val intent = Intent(context, if (MainActivity.running) NotificationActivity::class.java else MainActivity::class.java)
            .putExtra(context.getString(R.string.param_notification), this)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_diabetips)
            .setContentTitle(type)
            .setContentText(data.toString())
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT))
            //.setWhen(TimeHandler.instance.getTimestampFromFormat(time, context.getString(R.string.format_time_api)) ?: System.currentTimeMillis())

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0, notificationBuilder.build())
        if (MainActivity.running) {
            NotificationService.instance.remove<NotificationObject>(id).subscribe()
        }
    }
}