package com.epitech.diabetips.services

import com.epitech.diabetips.storages.Hba1cObject

class Hba1cService : AObjectService<Hba1cObject>("/users/me/hba1c") {

    private object Holder { val INSTANCE = Hba1cService() }

    companion object {
        val instance: Hba1cService by lazy { Holder.INSTANCE }
    }

}