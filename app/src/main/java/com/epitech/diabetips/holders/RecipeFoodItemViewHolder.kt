package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.IngredientObject
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.item_recipe_food.view.*

class RecipeFoodItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_recipe_food, parent, false)) {
    private var recipeFoodText: TextView? = null
    private var recipeFoodUnit: TextView? = null
    private var recipeFoodQuantity: EditText? = null
    private var recipeFoodRemove: MaterialButton? = null

    init {
        recipeFoodText = itemView.recipeFoodText
        recipeFoodUnit = itemView.recipeFoodUnit
        recipeFoodQuantity = itemView.recipeFoodQuantity
        recipeFoodRemove = itemView.recipeFoodRemove
    }

    fun getRecipeFoodQuantity() : EditText? {
        return recipeFoodQuantity
    }

    fun getRecipeFoodRemove() : MaterialButton? {
        return recipeFoodRemove
    }

    fun bind(food: IngredientObject) {
        recipeFoodText?.text = food.food.name
        recipeFoodUnit?.text = food.food.unit
        recipeFoodQuantity?.setText(food.quantity.toString())
    }
}

