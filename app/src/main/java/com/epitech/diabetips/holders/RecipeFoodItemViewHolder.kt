package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.IngredientObject
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.item_recipe_food.view.*

class RecipeFoodItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_recipe_food, parent, false)) {
    private var recipeFoodText: TextView? = null
    private var recipeFoodQuantityInput: TextInputEditText? = null
    private var recipeFoodQuantityInputLayout: TextInputLayout? = null

    init {
        recipeFoodText = itemView.recipeFoodText
        recipeFoodQuantityInput = itemView.recipeFoodQuantityInput
        recipeFoodQuantityInputLayout = itemView.recipeFoodQuantityInputLayout
    }

    fun getRecipeFoodQuantityInput() : TextInputEditText? {
        return recipeFoodQuantityInput
    }

    fun getRecipeFoodQuantityInputLayout() : TextInputLayout? {
        return recipeFoodQuantityInputLayout
    }

    fun bind(food: IngredientObject) {
        recipeFoodText?.text = food.food.name
        recipeFoodQuantityInputLayout?.suffixText = food.food.unit
        recipeFoodQuantityInput?.setText(food.quantity.toString())
    }
}

