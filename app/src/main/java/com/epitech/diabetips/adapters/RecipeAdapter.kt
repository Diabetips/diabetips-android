package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.storages.RecipeObject

class RecipeAdapter(private val recipes: ArrayList<RecipeObject> = arrayListOf(),
                  private val onItemClickListener : ((RecipeObject) -> Unit)? = null)
    : RecyclerView.Adapter<RecipeItemViewHolder>() {

    fun setRecipes(recipeList: Array<RecipeObject>) {
        recipes.clear()
        recipes.addAll(recipeList)
        notifyDataSetChanged()
    }

    fun addRecipe(recipe: RecipeObject) {
        recipes.add(recipe)
        notifyItemInserted(recipes.size)
    }

    fun addRecipes(recipeList: Array<RecipeObject>) {
        recipes.addAll(recipeList)
        notifyItemRangeInserted(recipes.size - recipeList.size, recipeList.size)
    }

    fun updateRecipe(recipe: RecipeObject) {
        recipes.forEachIndexed { index, recipeObject ->
            if (recipeObject.id == recipe.id) {
                recipes[index] = recipe
                notifyItemChanged(index)
                return
            }
        }
    }

    override fun getItemCount(): Int = recipes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecipeItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecipeItemViewHolder, position: Int) {
        holder.bind(recipes[position], onItemClickListener)
    }

}
