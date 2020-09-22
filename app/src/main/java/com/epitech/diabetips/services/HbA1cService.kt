package com.epitech.diabetips.services

import com.epitech.diabetips.storages.HbA1cObject

class HbA1cService : AObjectService<HbA1cObject>("/users/me/hba1c") {

    private object Holder { val INSTANCE = HbA1cService() }

    companion object {
        val instance: HbA1cService by lazy { Holder.INSTANCE }
    }

}