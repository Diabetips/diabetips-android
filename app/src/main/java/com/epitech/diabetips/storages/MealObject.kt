package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable
import java.util.*

data class MealObject (
    var id: Int = 0,
    var time: Date = Date(),
    var description: String = "",
    var recipes: Array<RecipeObject> = arrayOf()) : Serializable

class MealObjectAdapter : TypeAdapter<MealObject>() {

    override fun write(writer: JsonWriter?, mealObject: MealObject?) {
        writer?.beginObject()
        writer?.name("description")?.value(mealObject?.description)
        writer?.name("recipeIDs")
        writer?.beginArray()
        mealObject?.recipes?.forEach {
            writer?.value(it.id)
        }
        writer?.endArray()
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): MealObject {
        return MealObject()
    }
}