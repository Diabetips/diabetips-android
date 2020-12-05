package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.epitech.diabetips.R
import com.epitech.diabetips.services.NotificationService
import com.epitech.diabetips.storages.NotificationInviteObject
import com.epitech.diabetips.storages.NotificationObject
import com.epitech.diabetips.utils.DialogHandler

class NotificationActivity : AppCompatActivity()  {

    private var notification: NotificationObject = NotificationObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(getString(R.string.param_notification))) {
            notification = (intent.getSerializableExtra(getString(R.string.param_notification)) as NotificationObject)
        }
        if (!MainActivity.running) {
            startActivity(Intent(this, MainActivity::class.java)
                .putExtra(getString(R.string.param_notification), notification))
            finish()
        } else if (notification.id.isEmpty() || notification.read) {
            finish()
        } else {
            DialogHandler.dialogInvite(this, this.layoutInflater, (NotificationInviteObject()), {}, { finish() })
            when (notification.type) {
                NotificationObject.Type.user_invite.name -> { DialogHandler.dialogInvite(this, this.layoutInflater, (notification.getTypedNotification() as NotificationInviteObject), {}, { finish() }) }
                else -> NotificationService.instance.remove<NotificationObject>(notification.id).doOnSuccess {
                    finish()
                }.subscribe()
            }
        }
    }
}
