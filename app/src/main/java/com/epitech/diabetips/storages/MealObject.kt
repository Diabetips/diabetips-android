package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class MealObject (
    var id: Int = 0,
    var time: String = "",
    var description: String = "",
    var total_sugar: Float = 0f,
    var recipes: Array<MealRecipeObject> = arrayOf(),
    var foods: Array<IngredientObject> = arrayOf()) : Serializable {

    fun calculateTotalSugar() : Float {
        total_sugar = 0f
        recipes.forEach {
            total_sugar += it.calculateTotalSugar()
        }
        foods.forEach {
            total_sugar += it.calculateTotalSugar()
        }
        return total_sugar
    }

    fun getSummary(separator: String = "\n") : String {
        var summary = ""
        recipes.forEach {
            if (summary.isNotEmpty()) {
                summary += separator
            }
            summary += it.recipe.name
        }
        foods.forEach {
            if (summary.isNotEmpty()) {
                summary += separator
            }
            summary += it.food.name
        }
        return summary
    }
}

class MealObjectAdapter : TypeAdapter<MealObject>() {

    override fun write(writer: JsonWriter?, mealObject: MealObject?) {
        writer?.beginObject()

        writer?.name("description")?.value(mealObject?.description)
        writer?.name("timestamp")?.value(mealObject?.time)

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