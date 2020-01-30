package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.storages.MealObject

class MealAdapter(private val meals: ArrayList<MealObject> = arrayListOf(),
                  private val onItemClickListener : ((MealObject) -> Unit)? = null)
    : RecyclerView.Adapter<MealItemViewHolder>() {

    fun setMeals(mealList: Array<MealObject>) {
        meals.clear()
        meals.addAll(mealList)
        notifyDataSetChanged()
    }

    fun addMeal(meal: MealObject) {
        meals.add(meal)
        notifyItemInserted(meals.size)
    }

    fun addMeals(mealList: Array<MealObject>) {
        meals.addAll(mealList)
        notifyItemRangeInserted(meals.size - mealList.size, mealList.size)
    }

    fun updateMeal(meal: MealObject) {
        meals.forEachIndexed { index, mealObject ->
            if (mealObject.id == meal.id) {
                meals[index] = meal
                notifyItemChanged(index)
                return
            }
        }
    }

    override fun getItemCount(): Int = meals.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MealItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MealItemViewHolder, position: Int) {
        holder.bind(meals[position], onItemClickListener)
    }

}
