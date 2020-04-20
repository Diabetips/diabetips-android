package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.epitech.diabetips.holders.RecipeFoodItemViewHolder
import com.epitech.diabetips.storages.IngredientObject

class RecipeFoodAdapter(private val foods: ArrayList<IngredientObject> = arrayListOf(),
                        private val onItemClickListener : ((IngredientObject, TextView?) -> Unit)? = null) :
    AVisibilityAdapter<RecipeFoodItemViewHolder>() {

    override fun getItemCount(): Int = foods.size

    fun getFoods(): ArrayList<IngredientObject> {
        return foods
    }

    fun setFoods(foodList: ArrayList<IngredientObject>) {
        setFoods(foodList.toTypedArray())
    }

    fun setFoods(foodList: Array<IngredientObject>) {
        foods.clear()
        foods.addAll(foodList)
        notifyDataSetChanged()
        updateVisibility()
    }

    fun addFood(food: IngredientObject) {
        foods.add(food)
        notifyItemInserted(foods.size)
        updateVisibility()
    }

    private fun removeFood(position: Int) {
        foods.removeAt(position)
        notifyDataSetChanged()
        updateVisibility()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeFoodItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecipeFoodItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecipeFoodItemViewHolder, position: Int) {
        holder.bind(foods[position], onItemClickListener)
        holder.getRecipeFoodButton()?.setOnClickListener {
            removeFood(position)
        }
    }

}
