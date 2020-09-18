package com.epitech.diabetips.managers

import android.content.Context
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.services.*
import com.epitech.diabetips.storages.*

class FavoriteManager {

    private var context: Context? = null
    private var page: PaginationObject? = null
    private val favorites = ArrayList<Int>()

    private object Holder {
        val INSTANCE = FavoriteManager()
    }

    companion object {
        val instance: FavoriteManager by lazy { Holder.INSTANCE }
    }

    fun init(context: Context) {
        this.context = context
        favorites.clear()
        page = PaginationObject(
            context.resources.getInteger(R.integer.pagination_size),
            context.resources.getInteger(R.integer.pagination_default))
        getFavorites()
    }

    private fun getFavorites() {
        UserService.instance.getUserFavoriteRecipe(page!!).doOnSuccess{
            if (it.second.component2() == null) {
                page?.updateFromHeader(it.first.headers[context?.getString(R.string.pagination_header)]?.get(0))
                it.second.component1()?.forEach { recipe -> favorites.add(recipe.id) }
                if (page?.isLast() == false) {
                    page?.nextPage()
                    getFavorites()
                }
            }
        }.subscribe()
    }

    fun addFavorite(recipeId: Int) {
        favorites.add(recipeId)
    }

    fun removeFavorite(recipeId: Int) {
        favorites.remove(recipeId)
    }

    fun isFavorite(recipeId: Int): Boolean {
        return favorites.contains(recipeId)
    }

    fun handleImageButton(recipeId: Int, imageButton: ImageButton) {
        handleImage(recipeId, imageButton)
        imageButton.setOnClickListener {
            if (isFavorite(recipeId)) {
                handleFavoriteResponse(recipeId, imageButton, UserService.instance.removeFavoriteRecipe(recipeId), false)
            } else {
                handleFavoriteResponse(recipeId, imageButton, UserService.instance.addFavoriteRecipe(recipeId), true)
            }
        }
    }

    private fun handleFavoriteResponse(recipeId: Int, imageButton: ImageButton, response: FuelResponse<RecipeObject>, isFavorite: Boolean) {
            response.doAfterSuccess {
                if (it.second.component2() == null) {
                    handleImage(recipeId, imageButton)
                    Toast.makeText(context, context?.getString(if (isFavorite) R.string.recipe_favorite_add else R.string.recipe_favorite_remove), Toast.LENGTH_SHORT).show()
                }
            }.subscribe()
    }

    private fun handleImage(recipeId: Int, imageView: ImageView) {
        if (isFavorite(recipeId)) {
            imageView.drawable.setTint(ContextCompat.getColor(context!!, R.color.colorAccent))
            imageView.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_favorite))
        } else {
            val attribute = intArrayOf(R.attr.colorBackgroundText)
            val array = context!!.theme.obtainStyledAttributes(attribute)
            val color = array.getColor(0, ContextCompat.getColor(context!!, R.color.colorTextDark))
            array.recycle()
            imageView.drawable.setTint(color)
            imageView.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_favorite_border))
        }
    }
}
