package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class MealRecipeObject (
    var total_sugar: Float = 0f,
    var recipe: RecipeObject = RecipeObject(),
    var modifications: Array<IngredientObject> = arrayOf()) : Serializable {

    fun calculateTotalSugar() : Float {
        total_sugar = 0f
        recipe.ingredients.forEach {
            val ingredient = modifications.find { custom -> custom.food.id == it.food.id } ?: it
            total_sugar += ingredient.calculateTotalSugar()
        }
        return total_sugar
    }
}