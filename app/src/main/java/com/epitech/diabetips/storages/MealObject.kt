package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class MealObject (
    var id: Int = 0,
    var time: String = "",
    var description: String = "",
    var total_energy: Float = 0f,
    var total_carbohydrates: Float = 0f,
    var total_sugars: Float = 0f,
    var total_fat: Float = 0f,
    var total_saturated_fat: Float = 0f,
    var total_fiber: Float = 0f,
    var total_proteins: Float = 0f,
    var recipes: Array<MealRecipeObject> = arrayOf(),
    var foods: Array<IngredientObject> = arrayOf()) : Serializable {

    fun calculateTotalSugars() : Float {
        total_sugars = 0f
        recipes.forEach {
            total_sugars += it.calculateTotalSugars()
        }
        foods.forEach {
            total_sugars += it.calculateTotalSugars()
        }
        return total_sugars
    }

    fun getSummary(separator: String = "\n") : String {
        var summary = ""
        recipes.forEach { recipe ->
            if (summary.isNotEmpty()) {
                summary += separator
            }
            summary += recipe.recipe.name
        }
        foods.forEach { ingredient ->
            if (summary.isNotEmpty()) {
                summary += separator
            }
            summary += ingredient.food.name
        }
        return summary
    }


    fun getQuantity(): Float {
        var quantity = 0f
        recipes.forEach { recipe ->
            quantity += recipe.getQuantity()
        }
        foods.forEach { food ->
            quantity += food.quantity
        }
        return quantity
    }

    fun getNutritionalValues() : ArrayList<NutritionalObject> {
        val nutritionalValues =  NutritionalObject.getDefaultValues()
        recipes.forEach { recipe ->
            recipe.getNutritionalValues().forEach { recipeNutrition ->
                val index = nutritionalValues.indexOfFirst { it.type == recipeNutrition.type }
                if (index >= 0) {
                    nutritionalValues[index].value += recipeNutrition.value
                } else {
                    nutritionalValues.add(recipeNutrition)
                }
            }
        }
        foods.forEach { ingredient ->
            ingredient.getNutritionalValues().forEach { ingredientNutrition ->
                val index = nutritionalValues.indexOfFirst { it.type == ingredientNutrition.type }
                if (index >= 0) {
                    nutritionalValues[index].value += ingredientNutrition.value
                } else {
                    nutritionalValues.add(ingredientNutrition)
                }
            }
        }
        return nutritionalValues
    }
}

class MealObjectAdapter : TypeAdapter<MealObject>() {

    override fun write(writer: JsonWriter?, mealObject: MealObject?) {
        writer?.beginObject()

        writer?.name("description")?.value(mealObject?.description)
        writer?.name("time")?.value(mealObject?.time)

        writer?.name("recipes")
        writer?.beginArray()
        mealObject?.recipes?.forEach { recipe ->
            writer?.beginObject()
            writer?.name("id")?.value(recipe.recipe.id)
            writer?.name("portions_eaten")?.value(recipe.portions_eaten)
            writer?.name("modifications")
            writer?.beginArray()
            recipe.modifications.forEach {
                writer?.beginObject()
                writer?.name("food_id")?.value(it.food.id)
                writer?.name("quantity")?.value(it.quantity)
                writer?.endObject()
            }
            writer?.endArray()
            writer?.endObject()
        }
        writer?.endArray()

        writer?.name("foods")
        writer?.beginArray()
        mealObject?.foods?.forEach {
            writer?.beginObject()
            writer?.name("id")?.value(it.food.id)
            writer?.name("quantity")?.value(it.quantity)
            writer?.endObject()
        }
        writer?.endArray()

        writer?.endObject()
    }

    override fun read(reader: JsonReader?): MealObject {
        return MealObject()
    }
}