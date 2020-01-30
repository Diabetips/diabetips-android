package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class RecipeService : AService("/recipes") {

    private object Holder { val INSTANCE = RecipeService() }

    companion object {
        val instance: RecipeService by lazy { Holder.INSTANCE }
    }

    fun getAllRecipes(page: PaginationObject, name: String = "") : FuelResponse<Array<RecipeObject>> {
        return getRequest("?name=" + name + "&page=" + page.current + "&size=" + page.size)
    }

    fun getRecipe(id: String) : FuelResponse<RecipeObject> {
        return getRequest("/" + id)
    }

    fun createOrUpdateRecipe(recipe: RecipeObject) : FuelResponse<RecipeObject>  {
        if (recipe.id > 0) {
            return updateRecipe(recipe)
        }
        return createRecipe(recipe)
    }

    private fun createRecipe(recipe: RecipeObject) : FuelResponse<RecipeObject> {
        return postRequest(recipe)
    }

    private fun updateRecipe(recipe: RecipeObject) : FuelResponse<RecipeObject> {
        return putRequest(recipe, "/" + recipe.id)
    }

}