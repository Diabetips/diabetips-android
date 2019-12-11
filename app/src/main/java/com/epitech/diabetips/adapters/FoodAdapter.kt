package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.storages.FoodObject

class FoodAdapter(private val foods: ArrayList<FoodObject> = arrayListOf(),
                  private val onItemClickListener : ((FoodObject) -> Unit)? = null)
    : RecyclerView.Adapter<FoodItemViewHolder>() {

    fun setFoods(foodList: Array<FoodObject>) {
        foods.clear()
        foods.addAll(foodList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = foods.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FoodItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.bind(foods[position], onItemClickListener)
    }

}

