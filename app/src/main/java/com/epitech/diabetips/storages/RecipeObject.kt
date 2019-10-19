package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class RecipeObject (
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var ingredients: Array<IngredientObject> = arrayOf()) : Serializable

class RecipeObjectAdapter : TypeAdapter<RecipeObject>() {

    override fun write(writer: JsonWriter?, recipeObject: RecipeObject?) {
        writer?.beginObject()
        writer?.name("name")?.value(recipeObject?.name)
        writer?.name("description")?.value(recipeObject?.description)
        writer?.name("ingredients")
        writer?.beginArray()
        recipeObject?.ingredients?.forEach {
            writer?.beginObject()
            writer?.name("foodID")?.value(it.id)
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