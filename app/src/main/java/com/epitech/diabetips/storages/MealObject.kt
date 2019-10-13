package com.epitech.diabetips.storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable
import java.util.*

data class MealObject (
    var id: Int = 0,
    var time: Date = Date(),
    var description: String = "",
    var recipes: Array<RecipeObject> = arrayOf()) : Serializable {

    class Deserializer : ResponseDeserializable<MealObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, MealObject::class.java)
    }

    class ArrayDeserializer : ResponseDeserializable<Array<MealObject>> {
        override fun deserialize(content: String) = Gson().fromJson(content, Array<MealObject>::class.java)
    }
}