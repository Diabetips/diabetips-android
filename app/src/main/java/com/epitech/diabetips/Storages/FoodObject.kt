package com.epitech.diabetips.Storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

data class FoodObject (
    var uid: String = "",
    var name: String = "",
    var unit: String = "",
    var quantity: Float = 0f) : Serializable {

    class Deserializer : ResponseDeserializable<FoodObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, FoodObject::class.java)
    }

    class ArrayDeserializer : ResponseDeserializable<Array<FoodObject>> {
        override fun deserialize(content: String) = Gson().fromJson(content, Array<FoodObject>::class.java)
    }
}