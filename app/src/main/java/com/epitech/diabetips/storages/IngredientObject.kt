package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class IngredientObject (
    var quantity: Float = 0f,
    var total_energy: Float = 0f,
    var total_carbohydrates: Float = 0f,
    var total_sugars: Float = 0f,
    var total_fat: Float = 0f,
    var total_saturated_fat: Float = 0f,
    var total_fiber: Float = 0f,
    var total_proteins: Float = 0f,
    var food: FoodObject = FoodObject()) : Serializable {

    fun calculateTotalSugars() : Float {
        total_sugars = (food.carbohydrates_100g ?: 0f) * quantity / 100
        return total_sugars
    }

    fun getNutritionalValues(quantity: Float = this.quantity) : ArrayList<NutritionalObject> {
        return food.getNutritionalValues(quantity)
    }
}

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