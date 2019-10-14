package com.epitech.diabetips.storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class RecipeObject (
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var ingredients: Array<IngredientObject> = arrayOf()) : Serializable {

    class Deserializer : ResponseDeserializable<RecipeObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, RecipeObject::class.java)
    }

    class ArrayDeserializer : ResponseDeserializable<Array<RecipeObject>> {
        override fun deserialize(content: String) = Gson().fromJson(content, Array<RecipeObject>::class.java)
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