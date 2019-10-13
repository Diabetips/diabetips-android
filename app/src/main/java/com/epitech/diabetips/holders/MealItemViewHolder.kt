package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.MealObject
import kotlinx.android.synthetic.main.item_meal.view.*

class MealItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_meal, parent, false)) {
    private var mealDescription: TextView? = null

    init {
        mealDescription = itemView.mealDescription
    }

    fun bind(meal: MealObject, onItemClickListener : ((MealObject) -> Unit)? = null) {
        mealDescription?.text = meal.description
        itemView.setOnClickListener {onItemClickListener?.invoke(meal)}
    }
}

