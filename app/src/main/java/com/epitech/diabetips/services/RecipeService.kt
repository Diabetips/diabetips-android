package com.epitech.diabetips.services

import com.epitech.diabetips.storages.RecipeObject

class RecipeService : AObjectPictureService<RecipeObject>("/recipes") {

    private object Holder { val INSTANCE = RecipeService() }

    companion object {
        val instance: RecipeService by lazy { Holder.INSTANCE }
    }

}