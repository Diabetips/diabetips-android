package com.epitech.diabetips.services

import com.epitech.diabetips.storages.FCMTokenObject
import com.epitech.diabetips.storages.NotificationObject
import com.epitech.diabetips.storages.TokenObject

class NotificationService : AObjectService<NotificationObject>("/users/me/notifications") {

    private object Holder { val INSTANCE = NotificationService() }

    companion object {
        val instance: NotificationService by lazy { Holder.INSTANCE }
    }

    fun register(token: FCMTokenObject) : FuelResponse<FCMTokenObject> {
        return postRequest(token, "/fcm_token")
    }

    fun test() : FuelResponse<TokenObject> {
        return getRequest("/test")
    }

}