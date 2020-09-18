package com.epitech.diabetips.services

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.FavoriteManager
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.storages.UserObject

class UserService : AObjectPictureService<UserObject>("/users") {

    private object Holder { val INSTANCE = UserService() }

    companion object {
        val instance: UserService by lazy { Holder.INSTANCE }
    }

    fun registerUser(context: Context, user: UserObject) : FuelResponse<UserObject>  {
        return TokenService.instance.resetHeader(TokenService.instance.setBasicHeader(context, R.string.content_json), add(user))
    }

    fun getUserRecipe(page: PaginationObject, name: String = "") : FuelResponse<Array<RecipeObject>> {
        return getRequest("/me/recipes" + (if (name.isBlank()) "?" else "?name=$name&") + page.getRequestParameters())
    }

    fun getUserFavoriteRecipe(page: PaginationObject, name: String = "") : FuelResponse<Array<RecipeObject>> {
        return getRequest("/me/recipes/favorites" + (if (name.isBlank()) "?" else "?name=$name&") + page.getRequestParameters())
    }

    fun addFavoriteRecipe(id: Int) : FuelResponse<RecipeObject> {
        return postRequest(RecipeObject(id), "/me/recipes/favorites/$id").doOnSuccess {
            if (it.second.component2() == null) {
                FavoriteManager.instance.addFavorite(id)
            }
        }
    }

    fun removeFavoriteRecipe(id: Int) : FuelResponse<RecipeObject> {
        return deleteRequest<RecipeObject>("/me/recipes/favorites/$id").doOnSuccess {
            if (it.second.component2() == null) {
                FavoriteManager.instance.removeFavorite(id)
            }
        }
    }

}