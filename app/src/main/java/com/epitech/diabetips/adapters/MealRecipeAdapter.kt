package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.epitech.diabetips.holders.MealRecipeItemViewHolder
import com.epitech.diabetips.storages.MealRecipeObject

class MealRecipeAdapter(private val recipes: ArrayList<MealRecipeObject> = arrayListOf(),
                        private val onItemClickListener : ((MealRecipeObject) -> Unit)? = null)
    : AVisibilityAdapter<MealRecipeItemViewHolder>() {

    override fun getItemCount(): Int = recipes.size

    fun getRecipes() : ArrayList<MealRecipeObject> {
        return recipes
    }

    fun setRecipes(recipeList: Array<MealRecipeObject>) {
        recipes.clear()
        recipes.addAll(recipeList)
        notifyDataSetChanged()
        updateVisibility()
    }

    fun addRecipe(recipe: MealRecipeObject) {
        recipes.add(recipe)
        notifyItemInserted(recipes.size)
        updateVisibility()
    }

    fun updateRecipe(recipe: MealRecipeObject) {
        recipes.forEachIndexed { index, mealRecipeObject ->
            if (mealRecipeObject.recipe.id == recipe.recipe.id) {
                recipes[index] = recipe
                notifyItemChanged(index)
                return
            }
        }
    }

    private fun removeRecipe(position: Int) {
        recipes.removeAt(position)
        notifyDataSetChanged()
        updateVisibility()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealRecipeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MealRecipeItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MealRecipeItemViewHolder, position: Int) {
        holder.bind(recipes[position], onItemClickListener)
        holder.getMealRecipeButton()?.setOnClickListener {
            removeRecipe(position)
        }
    }

}