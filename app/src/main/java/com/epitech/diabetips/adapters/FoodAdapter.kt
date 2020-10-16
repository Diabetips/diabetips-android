package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.epitech.diabetips.holders.FoodItemViewHolder
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.storages.IngredientObject

class FoodAdapter(private val foods: ArrayList<FoodObject> = arrayListOf(),
                  private val onItemClickListener : ((FoodObject, CheckBox?) -> Unit)? = null)
    : AVisibilityAdapter<FoodItemViewHolder>() {

    private val selectedIngredients: ArrayList<IngredientObject> = arrayListOf()

    fun setFoods(foodList: Array<FoodObject>) {
        foods.clear()
        foods.addAll(foodList)
        notifyDataSetChanged()
        updateVisibility()
    }

    fun addFoods(foodList: Array<FoodObject>) {
        foods.addAll(foodList)
        notifyItemRangeInserted(foods.size - foodList.size, foodList.size)
        updateVisibility()
    }

    fun getSelectedIngredients() : ArrayList<IngredientObject> {
        return selectedIngredients
    }

    fun getSelectedIngredientOrNew(food: FoodObject) : IngredientObject {
        val ingredient: IngredientObject? = getSelectedIngredient(food)
        return ingredient ?: food.getIngredient()
    }

    fun getSelectedIngredient(food: FoodObject) : IngredientObject? {
      return selectedIngredients.find { ingredientObject ->
            ingredientObject.food.id == food.id
        }
    }

    fun setSelectedIngredients(ingredientList: ArrayList<IngredientObject>) {
        foods.clear()
        selectedIngredients.addAll(ingredientList)
    }

    fun addSelectedIngredient(ingredient: IngredientObject) {
        selectedIngredients.forEach {ingredientObject ->
            if (ingredientObject.food.id == ingredient.food.id) {
                ingredientObject.quantity = ingredient.quantity
                return
            }
        }
        selectedIngredients.add(ingredient)
    }

    fun removeSelectedIngredient(food: FoodObject) {
        selectedIngredients.forEach {ingredientObject ->
            if (ingredientObject.food.id == food.id) {
                selectedIngredients.remove(ingredientObject)
                return
            }
        }
    }

    override fun getItemCount(): Int = foods.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FoodItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.bind(foods[position], getSelectedIngredient(foods[position]) != null, onItemClickListener)
        holder.getFoodCheckBox()?.setOnClickListener { checkBoxView ->
            val checkBox = checkBoxView as CheckBox
            if (!checkBox.isChecked) {
                removeSelectedIngredient(foods[position])
            }
            onItemClickListener?.invoke(foods[position], holder.getFoodCheckBox()!!)
        }
    }

}

