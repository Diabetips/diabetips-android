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
    var recipes: Array<RecipeObject> = arrayOf()) : Serializable

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
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): MealObject {
        return MealObject()
    }
}