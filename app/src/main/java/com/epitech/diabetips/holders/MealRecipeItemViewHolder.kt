package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.RecipeObject
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.item_meal_recipe.view.*

class MealRecipeItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_meal_recipe, parent, false)) {
    private var mealRecipeText: TextView? = null
    private var mealRecipeUnit: TextView? = null
    private var mealRecipeQuantity: EditText? = null
    private var mealRecipeRemove: MaterialButton? = null

    init {
        mealRecipeText = itemView.mealRecipeText
        mealRecipeUnit = itemView.mealRecipeUnit
        mealRecipeQuantity = itemView.mealRecipeQuantity
        mealRecipeRemove = itemView.mealRecipeRemove
    }

    fun getMealRecipeQuantity() : EditText? {
        return mealRecipeQuantity
    }

    fun getMealRecipeRemove() : MaterialButton? {
        return mealRecipeRemove
    }

    fun bind(recipe: RecipeObject) {
        mealRecipeText?.text = recipe.name
        //mealRecipeUnit?.text = recipe.unit //TODO add unit in recipes
        //mealRecipeQuantity?.setText(recipe.quantity.toString()) //TODO add quantity in recipes
    }
}

