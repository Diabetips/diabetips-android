package com.epitech.diabetips.Storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

data class RecipeObject (
    var uid: String = "",
    var name: String = "",
    var description: String = "",
    var ingredients: Array<FoodObject> = arrayOf()) : Serializable {

    class Deserializer : ResponseDeserializable<RecipeObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, RecipeObject::class.java)
    }

    class ArrayDeserializer : ResponseDeserializable<Array<RecipeObject>> {
        override fun deserialize(content: String) = Gson().fromJson(content, Array<RecipeObject>::class.java)
    }
}