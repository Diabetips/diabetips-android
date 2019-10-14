package com.epitech.diabetips.storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

data class FoodObject (
    var id: Int = 0,
    var name: String = "",
    var unit: String = "") : Serializable {

    class Deserializer : ResponseDeserializable<FoodObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, FoodObject::class.java)
    }

    class ArrayDeserializer : ResponseDeserializable<Array<FoodObject>> {
        override fun deserialize(content: String) = Gson().fromJson(content, Array<FoodObject>::class.java)
    }
}