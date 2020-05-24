package com.epitech.diabetips.services

import android.graphics.Bitmap
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.utils.ImageHandler
import com.github.kittinunf.fuel.core.FuelManager

class RecipeService : AService("/recipes") {

    private object Holder { val INSTANCE = RecipeService() }

    companion object {
        val instance: RecipeService by lazy { Holder.INSTANCE }
    }

    fun getAllRecipes(page: PaginationObject, name: String = "") : FuelResponse<Array<RecipeObject>> {
        return getRequest("?name=" + name + "&" + page.getRequestParameters())
    }

    fun getRecipe(id: String) : FuelResponse<RecipeObject> {
        return getRequest("/" + id)
    }

    fun removeRecipe(id: String) : FuelResponse<RecipeObject> {
        return deleteRequest("/" + id)
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

    fun updateRecipePicture(image: Bitmap, id: Int) : FuelResponse<RecipeObject> {
        return postData(ImageHandler.instance.encodeImage(image, 300), "/" + id + "/picture")
    }

    fun removeRecipePicture(id: Int) : FuelResponse<RecipeObject> {
        return deleteRequest("/" + id + "/picture")
    }

    fun getRecipePictureUrl(id: Int) : String  {
        return FuelManager.instance.basePath + baseRoute + "/" + id + "/picture"
    }

}