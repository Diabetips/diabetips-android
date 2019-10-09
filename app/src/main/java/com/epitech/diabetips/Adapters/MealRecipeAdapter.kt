package com.epitech.diabetips.Adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.Storages.RecipeObject

class MealRecipeAdapter(private val recipes: ArrayList<RecipeObject> = arrayListOf())
    : RecyclerView.Adapter<MealRecipeItemViewHolder>() {

    override fun getItemCount(): Int = recipes.size

    fun getRecipes() : ArrayList<RecipeObject> {
        return recipes
    }

    fun addRecipe(food: RecipeObject) {
        recipes.add(food)
        notifyItemInserted(recipes.size)
    }

    private fun removeRecipe(position: Int) {
        recipes.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealRecipeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MealRecipeItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MealRecipeItemViewHolder, position: Int) {
        holder.bind(recipes[position])
        holder.getMealRecipeQuantity()?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //recipes[position].quantity = s.toString().toFloat() //TODO add quantity in recipes
            }
        })
        holder.getMealRecipeRemove()?.setOnClickListener {
            removeRecipe(position)
        }
    }

}

