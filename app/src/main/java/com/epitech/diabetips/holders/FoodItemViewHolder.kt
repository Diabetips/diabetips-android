package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.FoodObject
import kotlinx.android.synthetic.main.item_food.view.*

class FoodItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_food, parent, false)) {
    private var foodText: TextView? = null

    init {
        foodText = itemView.foodText
    }

    fun bind(food: FoodObject, onItemClickListener : ((FoodObject) -> Unit)? = null) {
        foodText?.text = food.name
        itemView.setOnClickListener {onItemClickListener?.invoke(food)}
    }
}

