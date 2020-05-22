package com.epitech.diabetips.storages

import java.io.Serializable

data class MealRecipeObject (
    var total_sugar: Float = 0f,
    var portions_eaten: Float = 0f,
    var recipe: RecipeObject = RecipeObject(),
    var modifications: Array<IngredientObject> = arrayOf()) : Serializable {

    fun getIngredients() : Array<IngredientObject> {
        val ingredients = ArrayList<IngredientObject>()
        recipe.ingredients.forEach {
            val ingredient = modifications.find { custom -> custom.food.id == it.food.id } ?: it
            if (ingredient.quantity > 0) {
                ingredients.add(ingredient)
            }
        }
        modifications.forEach {
            if (recipe.ingredients.find { ingredient -> ingredient.food.id == it.food.id } == null) {
                if (it.quantity > 0) {
                    ingredients.add(it)
                }
            }
        }
        return ingredients.toTypedArray()
    }

    fun calculateTotalSugar() : Float {
        total_sugar = 0f
        getIngredients().forEach {
            total_sugar += it.calculateTotalSugar()
        }
        if (recipe.portions != 0f) {
            total_sugar *= (portions_eaten / recipe.portions)
        }
        return total_sugar
    }
}