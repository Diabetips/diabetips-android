package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.RecipeObject
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.item_meal_recipe.view.*

class MealRecipeItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_meal_recipe, parent, false)) {
    private var mealRecipeText: TextView? = null
    private var mealRecipeQuantityInput: TextInputEditText? = null
    private var mealRecipeQuantityInputLayout: TextInputLayout? = null

    init {
        mealRecipeText = itemView.mealRecipeText
        mealRecipeQuantityInput = itemView.mealRecipeQuantityInput
        mealRecipeQuantityInputLayout = itemView.mealRecipeQuantityInputLayout
    }

    fun getMealRecipeQuantityInput() : TextInputEditText? {
        return mealRecipeQuantityInput
    }

    fun getMealRecipeQuantityInputLayout() : TextInputLayout? {
        return mealRecipeQuantityInputLayout
    }

    fun bind(recipe: RecipeObject) {
        mealRecipeText?.text = recipe.name
        //recipeFoodQuantityInputLayout?.suffixText = recipe.unit //TODO add unit in recipes
        // recipeFoodQuantityInput?.setText(recipe.quantity.toString()) //TODO add quantity in recipes
    }
}

