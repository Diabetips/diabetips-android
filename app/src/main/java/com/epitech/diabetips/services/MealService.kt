package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class MealService : AService("/users/me/meals") {

    private object Holder { val INSTANCE = MealService() }

    companion object {
        val instance: MealService by lazy { Holder.INSTANCE }
    }

    fun getAllUserMeals(page: PaginationObject) : FuelResponse<Array<MealObject>> {
        return getRequest("?${page.getRequestParameters()}")
    }

    fun getUserMeal(id: String) : FuelResponse<MealObject> {
        return getRequest("/$id")
    }

    fun removeUserMeal(id: String) : FuelResponse<MealObject> {
        return deleteRequest("/$id")
    }

    fun createOrUpdateUserMeal(meal: MealObject) : FuelResponse<MealObject> {
        if (meal.id > 0) {
            return updateUserMeal(meal)
        }
        return addUserMeal(meal)
    }

    private fun addUserMeal(meal: MealObject) : FuelResponse<MealObject> {
        return postRequest(meal)
    }

    private fun updateUserMeal(meal: MealObject) : FuelResponse<MealObject> {
        return putRequest(meal, "/${meal.id}")
    }

}