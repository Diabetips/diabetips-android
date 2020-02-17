package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.epitech.diabetips.holders.MealRecipeItemViewHolder
import com.epitech.diabetips.storages.RecipeObject

class MealRecipeAdapter(private val recipes: ArrayList<RecipeObject> = arrayListOf())
    : AVisibilityAdapter<MealRecipeItemViewHolder>() {

    override fun getItemCount(): Int = recipes.size

    fun getRecipes() : ArrayList<RecipeObject> {
        return recipes
    }

    fun setRecipes(recipeList: Array<RecipeObject>) {
        recipes.clear()
        recipes.addAll(recipeList)
        notifyDataSetChanged()
        updateVisibility()
    }

    fun addRecipe(recipe: RecipeObject) {
        recipes.add(recipe)
        notifyItemInserted(recipes.size)
        updateVisibility()
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
        holder.bind(recipes[position])
        holder.getMealRecipeButton()?.setOnClickListener {
            removeRecipe(position)
        }
    }

}

