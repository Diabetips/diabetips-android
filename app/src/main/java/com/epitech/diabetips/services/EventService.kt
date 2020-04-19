package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class EventService : AService("/users/me/events") {

    private object Holder { val INSTANCE = EventService() }

    companion object {
        val instance: EventService by lazy { Holder.INSTANCE }
    }

    fun getAllUserEvent(page: PaginationObject) : FuelResponse<Array<EventObject>> {
        return getRequest("?" + page.getRequestParameters())
    }

    fun getUserEvent(id: Int) : FuelResponse<EventObject> {
        return getRequest("/" + id)
    }

    fun createOrUpdateUserEvent(event: EventObject) : FuelResponse<EventObject> {
        if (event.id > 0) {
            return updateUserEvent(event)
        }
        return addUserEvent(event)
    }

    private fun addUserEvent(event: EventObject) : FuelResponse<EventObject> {
        return postRequest(event)
    }

    private fun updateUserEvent(event: EventObject) : FuelResponse<EventObject> {
        return putRequest(event, "/" + event.id)
    }

}