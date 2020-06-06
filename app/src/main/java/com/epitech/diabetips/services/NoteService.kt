package com.epitech.diabetips.services

import com.epitech.diabetips.storages.NoteObject

class NoteService : AObjectService<NoteObject>("/users/me/notes") {

    private object Holder { val INSTANCE = NoteService() }

    companion object {
        val instance: NoteService by lazy { Holder.INSTANCE }
    }

}