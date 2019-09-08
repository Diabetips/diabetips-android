package com.epitech.diabetips.Storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

data class AccountObject (
    var uid: String = "",
    var email: String = "",
    var password: String = "",
    var first_name: String = "",
    var last_name: String = "") : Serializable {

    class Deserializer : ResponseDeserializable<AccountObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, AccountObject::class.java)
    }
    class ArrayDeserializer : ResponseDeserializable<Array<AccountObject>> {
        override fun deserialize(content: String) = Gson().fromJson(content, Array<AccountObject>::class.java)
    }
}