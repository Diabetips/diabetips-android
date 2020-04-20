package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class RecipeObject (
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var total_sugar: Float = 0f,
    var ingredients: Array<IngredientObject> = arrayOf(),
    var customization: Array<IngredientObject> = arrayOf()) : Serializable {

    fun calculateTotalSugar() : Float {
        total_sugar = 0f
        ingredients.forEach {
            val ingredient = customization.find { custom -> custom.food.id == it.food.id } ?: it
            total_sugar += ingredient.calculateTotalSugar()
        }
        return total_sugar
    }
}

class RecipeObjectAdapter : TypeAdapter<RecipeObject>() {

    override fun write(writer: JsonWriter?, recipeObject: RecipeObject?) {
        writer?.beginObject()
        writer?.name("name")?.value(recipeObject?.name)
        writer?.name("description")?.value(recipeObject?.description)
        writer?.name("ingredients")
        writer?.beginArray()
        recipeObject?.ingredients?.forEach {
            writer?.beginObject()
            writer?.name("food_id")?.value(it.food.id)
            writer?.name("quantity")?.value(it.quantity)
            writer?.endObject()
        }
        writer?.endArray()
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): RecipeObject {
        return RecipeObject()
    }
}