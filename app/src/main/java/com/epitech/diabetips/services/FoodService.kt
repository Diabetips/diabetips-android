package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*
import com.github.kittinunf.fuel.core.FuelManager

class FoodService : AService("/food") {

    private object Holder { val INSTANCE = FoodService() }

    companion object {
        val instance: FoodService by lazy { Holder.INSTANCE }
    }

    fun getAllFood(page: PaginationObject, name: String = "") : FuelResponse<Array<FoodObject>> {
        return getRequest((if (name.isBlank()) "?" else "?name=$name&") + page.getRequestParameters())
    }

    fun getFood(id: Int) : FuelResponse<FoodObject> {
        return getRequest("/$id")
    }

    fun getFoodPictureUrl(id: Int) : String {
        return "${FuelManager.instance.basePath}$baseRoute/$id/picture"
    }

}