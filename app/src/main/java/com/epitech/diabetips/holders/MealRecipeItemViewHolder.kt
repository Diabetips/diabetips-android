package com.epitech.diabetips.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.MealRecipeObject
import com.epitech.diabetips.utils.ImageHandler
import kotlinx.android.synthetic.main.item_meal_recipe.view.*

class MealRecipeItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_meal_recipe, parent, false)) {
    private var mealRecipeText: TextView? = null
    private var mealRecipeQuantity: TextView? = null
    private var mealRecipeButton: ImageButton? = null
    private var mealRecipeImage: ImageView? = null
    private var context: Context

    init {
        mealRecipeText = itemView.mealRecipeText
        mealRecipeQuantity = itemView.mealRecipeQuantity
        mealRecipeButton = itemView.mealRecipeButton
        mealRecipeImage = itemView.mealRecipeImage
        context = inflater.context
    }

    fun getMealRecipeButton(): ImageButton? {
        return mealRecipeButton
    }

    fun bind(recipe: MealRecipeObject, onItemClickListener : ((MealRecipeObject) -> Unit)? = null) {
        mealRecipeText?.text = recipe.recipe.name
        itemView.setOnClickListener {onItemClickListener?.invoke(recipe)}
        //mealRecipeQuantity?.text = recipe.quantity.toString() + recipe.unit //TODO add unit and quantity in recipes
        if (mealRecipeImage != null) {
            ImageHandler.instance.loadImage(mealRecipeImage!!, context, RecipeService.instance.getRecipePictureUrl(recipe.recipe.id), R.drawable.ic_unknown, false)
        }
    }
}

