package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class IngredientObject (
    var quantity: Float = 0f,
    var total_sugar: Float = 0f,
    var food: FoodObject = FoodObject()) : Serializable

class IngredientObjectAdapter : TypeAdapter<IngredientObject>() {

    override fun write(writer: JsonWriter?, ingredientObject: IngredientObject?) {
        writer?.beginObject()
        writer?.name("food_id")?.value(ingredientObject?.food?.id)
        writer?.name("quantity")?.value(ingredientObject?.quantity)
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): IngredientObject {
        return IngredientObject()
    }
}