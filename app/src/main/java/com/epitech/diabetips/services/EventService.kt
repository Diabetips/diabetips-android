package com.epitech.diabetips.services

import com.epitech.diabetips.storages.EventObject

class EventService : AObjectService<EventObject>("/users/me/events") {

    private object Holder { val INSTANCE = EventService() }

    companion object {
        val instance: EventService by lazy { Holder.INSTANCE }
    }

}