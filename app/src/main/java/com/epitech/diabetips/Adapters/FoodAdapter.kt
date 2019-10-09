package com.epitech.diabetips.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.Storages.FoodObject

class FoodAdapter(private val foods: ArrayList<FoodObject> = arrayListOf(),
                  private val onItemClickListener : ((FoodObject) -> Unit)? = null)
    : RecyclerView.Adapter<FoodItemViewHolder>() {

    override fun getItemCount(): Int = foods.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FoodItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.bind(foods[position], onItemClickListener)
    }

}

