package com.epitech.diabetips.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.MealRecipeObject
import kotlinx.android.synthetic.main.item_meal_recipe.view.*

class MealRecipeItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_meal_recipe, parent, false)) {
    private var mealRecipeText: TextView? = null
    private var mealRecipeQuantity: TextView? = null
    private var mealRecipeButton: ImageButton? = null

    init {
        mealRecipeText = itemView.mealRecipeText
        mealRecipeQuantity = itemView.mealRecipeQuantity
        mealRecipeButton = itemView.mealRecipeButton
    }

    fun getMealRecipeButton(): ImageButton? {
        return mealRecipeButton
    }

    fun bind(recipe: MealRecipeObject) {
        mealRecipeText?.text = recipe.recipe.name
        //mealRecipeQuantity?.text = recipe.quantity.toString() + recipe.unit //TODO add unit and quantity in recipes
    }
}

