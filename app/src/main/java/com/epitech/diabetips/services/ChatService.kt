package com.epitech.diabetips.services

import com.epitech.diabetips.storages.ChatObject
import com.epitech.diabetips.storages.PaginationObject

class ChatService : AObjectService<ChatObject>("/chat") {

    private object Holder {
        val INSTANCE = ChatService()
    }

    companion object {
        val instance: ChatService by lazy { Holder.INSTANCE }
    }

    fun getAllMessage(uid: String, page: PaginationObject) : FuelResponse<Array<ChatObject>> {
        return getRequest("/$uid?" + page.getRequestParameters())
    }

    fun addMessage(chatObject: ChatObject) : FuelResponse<ChatObject> {
        return postRequest(chatObject, "/${chatObject.to}")
    }

}