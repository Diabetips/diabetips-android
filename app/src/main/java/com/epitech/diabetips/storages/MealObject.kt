package com.epitech.diabetips.storages

import com.epitech.diabetips.utils.TimeHandler
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable
import java.util.*

data class MealObject (
    var id: Int = 0,
    var timestamp: Long = TimeHandler.instance.currentTimeSecond(),
    var description: String = "",
    var total_sugar: Float = 0f,
    var recipes: Array<RecipeObject> = arrayOf(),
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
}

class MealObjectAdapter : TypeAdapter<MealObject>() {

    override fun write(writer: JsonWriter?, mealObject: MealObject?) {
        writer?.beginObject()

        writer?.name("description")?.value(mealObject?.description)
        writer?.name("timestamp")?.value(mealObject?.timestamp)

        writer?.name("recipes_ids")
        writer?.beginArray()
        mealObject?.recipes?.forEach {
            writer?.value(it.id)
        }
        writer?.endArray()

        writer?.name("recipes")
        writer?.beginArray()
        mealObject?.recipes?.forEach { recipe ->
            writer?.beginObject()
            writer?.name("id")?.value(recipe.id)
            writer?.name("modifications")
            writer?.beginArray()
            recipe.customization.forEach {
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