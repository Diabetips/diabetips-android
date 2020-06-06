package com.epitech.diabetips.services

import com.epitech.diabetips.storages.UserObject

class UserService : AObjectPictureService<UserObject>("/users") {

    private object Holder { val INSTANCE = UserService() }

    companion object {
        val instance: UserService by lazy { Holder.INSTANCE }
    }

}