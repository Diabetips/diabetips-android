package com.epitech.diabetips.services

import com.epitech.diabetips.storages.MealObject

class MealService : AObjectService<MealObject>("/users/me/meals") {

    private object Holder { val INSTANCE = MealService() }

    companion object {
        val instance: MealService by lazy { Holder.INSTANCE }
    }

}