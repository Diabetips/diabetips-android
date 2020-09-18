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

    fun getQuantity(portion: Float = portions_eaten): Float {
        var quantity = 0f
        getIngredients().forEach {ingredient ->
            quantity += ingredient.quantity
        }
        return if (recipe.portions == 0f) 0f else quantity * portion / recipe.portions
    }

    fun getNutritionalValues(portion: Float = portions_eaten) : ArrayList<NutritionalObject> {
        val nutritionalValues =  ArrayList<NutritionalObject>()
        getIngredients().forEach { ingredient ->
            ingredient.getNutritionalValues().forEach { ingredientNutrition ->
                val index = nutritionalValues.indexOfFirst { it.type == ingredientNutrition.type }
                if (index >= 0) {
                    nutritionalValues[index].value += ingredientNutrition.value
                } else {
                    nutritionalValues.add(ingredientNutrition)
                }
            }
        }
        nutritionalValues.forEach { nutrition ->
            nutrition.value *= if (recipe.portions == 0f) 0f else (portion / recipe.portions)
        }
        return nutritionalValues
    }
}