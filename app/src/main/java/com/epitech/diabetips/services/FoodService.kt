package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class FoodService : AService("/food") {

    private object Holder { val INSTANCE = FoodService() }

    companion object {
        val instance: FoodService by lazy { Holder.INSTANCE }
    }

    fun getAllFood(page: PaginationObject, name: String = "") : FuelResponse<Array<FoodObject>> {
        return getRequest("?name=" + name + "&page=" + page.current + "&size=" + page.size)
    }

    fun getFood(id: String) : FuelResponse<FoodObject> {
        return getRequest("/" + id)
    }

}