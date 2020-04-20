package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class NoteService : AService("/users/me/notes") {

    private object Holder { val INSTANCE = NoteService() }

    companion object {
        val instance: NoteService by lazy { Holder.INSTANCE }
    }

    fun getAllUserNote(page: PaginationObject) : FuelResponse<Array<NoteObject>> {
        return getRequest("?" + page.getRequestParameters())
    }

    fun getUserNote(id: Int) : FuelResponse<NoteObject> {
        return getRequest("/" + id)
    }

    fun createOrUpdateUserNote(note: NoteObject) : FuelResponse<NoteObject> {
        if (note.id > 0) {
            return updateUserNote(note)
        }
        return addUserNote(note)
    }

    private fun addUserNote(note: NoteObject) : FuelResponse<NoteObject> {
        return postRequest(note)
    }

    private fun updateUserNote(note: NoteObject) : FuelResponse<NoteObject> {
        return putRequest(note, "/" + note.id)
    }

}