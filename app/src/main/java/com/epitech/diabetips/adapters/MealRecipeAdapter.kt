package com.epitech.diabetips.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.storages.RecipeObject

class MealRecipeAdapter(private val recipes: ArrayList<RecipeObject> = arrayListOf())
    : RecyclerView.Adapter<MealRecipeItemViewHolder>() {

    override fun getItemCount(): Int = recipes.size

    fun getRecipes() : ArrayList<RecipeObject> {
        return recipes
    }

    fun setRecipes(recipeList: Array<RecipeObject>) {
        recipes.clear()
        recipes.addAll(recipeList)
        notifyDataSetChanged()
    }

    fun addRecipe(recipe: RecipeObject) {
        recipes.add(recipe)
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
        holder.getMealRecipeQuantityInput()?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //recipes[position].quantity = if (s.toString().toFloatOrNull() == null) 0.0f else s.toString().toFloat() //TODO add quantity in recipes
            }
        })
        holder.getMealRecipeQuantityInputLayout()?.setEndIconOnClickListener {
            removeRecipe(position)
        }
    }

}

