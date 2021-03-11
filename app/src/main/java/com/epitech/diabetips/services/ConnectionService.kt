package com.epitech.diabetips.services

import com.epitech.diabetips.storages.UserObject

class ConnectionService : AObjectService<UserObject>("/users/me/connections") {

    private object Holder { val INSTANCE = ConnectionService() }

    companion object {
        val instance: ConnectionService by lazy { Holder.INSTANCE }
    }

}