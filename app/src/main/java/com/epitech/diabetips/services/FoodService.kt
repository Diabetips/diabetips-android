package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*
class FoodService : AService("/food") {

    private object Holder { val INSTANCE = FoodService() }

    companion object {
        val instance: FoodService by lazy { Holder.INSTANCE }
    }

    fun getAllFood() : FuelResponse<Array<FoodObject>> {
        return getRequest()
    }

    fun getFood(id: String) : FuelResponse<FoodObject> {
        return getRequest("/" + id)
    }

}