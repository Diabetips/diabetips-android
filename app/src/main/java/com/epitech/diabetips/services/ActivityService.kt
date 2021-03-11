package com.epitech.diabetips.services

import com.epitech.diabetips.storages.ActivityObject

class ActivityService : AObjectService<ActivityObject>("/users/me/activity") {

    private object Holder { val INSTANCE = ActivityService() }

    companion object {
        val instance: ActivityService by lazy { Holder.INSTANCE }
    }

}