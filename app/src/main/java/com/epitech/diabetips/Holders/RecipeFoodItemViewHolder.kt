package com.epitech.diabetips.Adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.Storages.FoodObject
import kotlinx.android.synthetic.main.item_recipe_food.view.*
import ru.dimorinny.floatingtextbutton.FloatingTextButton

class RecipeFoodItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_recipe_food, parent, false)) {
    private var recipeFoodText: TextView? = null
    private var recipeFoodUnit: TextView? = null
    private var recipeFoodQuantity: EditText? = null
    private var recipeFoodRemove: FloatingTextButton? = null

    init {
        recipeFoodText = itemView.recipeFoodText
        recipeFoodUnit = itemView.recipeFoodUnit
        recipeFoodQuantity = itemView.recipeFoodQuantity
        recipeFoodRemove = itemView.recipeFoodRemove
    }

    fun getRecipeFoodQuantity() : EditText? {
        return recipeFoodQuantity
    }

    fun getRecipeFoodRemove() : FloatingTextButton? {
        return recipeFoodRemove
    }

    fun bind(food: FoodObject) {
        recipeFoodText?.text = food.name
        recipeFoodUnit?.text = food.unit
        recipeFoodQuantity?.setText(food.quantity.toString())
    }
}

