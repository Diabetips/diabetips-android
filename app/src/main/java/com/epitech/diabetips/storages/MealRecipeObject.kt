package com.epitech.diabetips.storages

import java.io.Serializable

data class MealRecipeObject (
    var portions_eaten: Float = 0f,
    var total_energy: Float = 0f,
    var total_carbohydrates: Float = 0f,
    var total_sugars: Float = 0f,
    var total_fat: Float = 0f,
    var total_saturated_fat: Float = 0f,
    var total_fiber: Float = 0f,
    var total_proteins: Float = 0f,
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

    fun calculateTotalSugars() : Float {
        total_sugars = 0f
        getIngredients().forEach {
            total_sugars += it.calculateTotalSugars()
        }
        if (recipe.portions != 0f) {
            total_sugars *= (portions_eaten / recipe.portions)
        }
        return total_sugars
    }

    fun getQuantity(portion: Float = portions_eaten): Float {
        if (portion == 0f || recipe.portions == 0f)
            return 0f
        var quantity = 0f
        getIngredients().forEach {ingredient ->
            quantity += ingredient.quantity
        }
        return quantity * portion / recipe.portions
    }

    fun getNutritionalValues(portion: Float = portions_eaten) : ArrayList<NutritionalObject> {
        val nutritionalValues = NutritionalObject.getDefaultValues()
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