package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class InsulinService : AService("/users/me/insulin") {

    private object Holder { val INSTANCE = InsulinService() }

    companion object {
        val instance: InsulinService by lazy { Holder.INSTANCE }
    }

    fun getAllUserInsulin(page: PaginationObject) : FuelResponse<Array<InsulinObject>> {
        return getRequest("?page=" + page.current + "&size=" + page.size)
    }

    fun getUserInsulin(id: Int) : FuelResponse<InsulinObject> {
        return getRequest("/" + id)
    }

    private fun addUserInsulin(insulin: InsulinObject) : FuelResponse<InsulinObject> {
        return postRequest(insulin)
    }

    private fun updateUserInsulin(insulin: InsulinObject) : FuelResponse<InsulinObject> {
        return putRequest(insulin, "/" + insulin.id)
    }

}