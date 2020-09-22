package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class RecipeObject (
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var portions: Float = 0f,
    var total_energy: Float = 0f,
    var total_carbohydrates: Float = 0f,
    var total_sugars: Float = 0f,
    var total_fat: Float = 0f,
    var total_saturated_fat: Float = 0f,
    var total_fiber: Float = 0f,
    var total_proteins: Float = 0f,
    var ingredients: Array<IngredientObject> = arrayOf(),
    var author: UserObject = UserObject()) : Serializable {

    fun calculateTotalSugars() : Float {
        total_sugars = 0f
        ingredients.forEach {
            total_sugars += it.calculateTotalSugars()
        }
        return total_sugars
    }


    fun getQuantity(portion: Float = portions): Float {
        if (portion == 0f || portions == 0f)
            return 0f
        var quantity = 0f
        ingredients.forEach {ingredient ->
            quantity += ingredient.quantity
        }
        return quantity * portion / portions
    }

    fun getNutritionalValues(portion: Float = portions) : ArrayList<NutritionalObject> {
        val nutritionalValues = NutritionalObject.getDefaultValues()
        ingredients.forEach { ingredient ->
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
            nutrition.value *= if (portions == 0f) 0f else (portion / portions)
        }
        return nutritionalValues
    }
}

class RecipeObjectAdapter : TypeAdapter<RecipeObject>() {

    override fun write(writer: JsonWriter?, recipeObject: RecipeObject?) {
        writer?.beginObject()
        writer?.name("name")?.value(recipeObject?.name)
        writer?.name("description")?.value(recipeObject?.description)
        writer?.name("portions")?.value(recipeObject?.portions)
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