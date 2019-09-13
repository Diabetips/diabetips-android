package com.epitech.diabetips.Storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable
import java.util.*

data class MealObject (
    var uid: String = "",
    var date: Date = Date(),
    var recipes: Array<RecipeObject> = arrayOf()) : Serializable {

    class Deserializer : ResponseDeserializable<MealObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, MealObject::class.java)
    }

    class ArrayDeserializer : ResponseDeserializable<Array<MealObject>> {
        override fun deserialize(content: String) = Gson().fromJson(content, Array<MealObject>::class.java)
    }
}