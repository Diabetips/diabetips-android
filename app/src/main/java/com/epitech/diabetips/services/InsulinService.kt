package com.epitech.diabetips.services

import com.epitech.diabetips.storages.InsulinObject

class InsulinService : AObjectService<InsulinObject>("/users/me/insulin") {

    private object Holder { val INSTANCE = InsulinService() }

    companion object {
        val instance: InsulinService by lazy { Holder.INSTANCE }
    }

}