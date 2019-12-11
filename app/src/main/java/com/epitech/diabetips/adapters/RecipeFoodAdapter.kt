package com.epitech.diabetips.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.storages.IngredientObject

class RecipeFoodAdapter(private val foods: ArrayList<IngredientObject> = arrayListOf())
    : RecyclerView.Adapter<RecipeFoodItemViewHolder>() {

    override fun getItemCount(): Int = foods.size

    fun getFoods() : ArrayList<IngredientObject> {
        return foods
    }

    fun setFoods(foodList: Array<IngredientObject>) {
        foods.clear()
        foods.addAll(foodList)
        notifyDataSetChanged()
    }

    fun addFood(food: IngredientObject) {
        foods.add(food)
        notifyItemInserted(foods.size)
    }

    private fun removeFood(position: Int) {
        foods.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeFoodItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecipeFoodItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecipeFoodItemViewHolder, position: Int) {
        holder.bind(foods[position])
        holder.getRecipeFoodQuantity()?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                foods[position].quantity = if (s.toString().toFloatOrNull() == null) 0.0f else s.toString().toFloat()
            }
        })
        holder.getRecipeFoodRemove()?.setOnClickListener {
            removeFood(position)
        }
    }

}
