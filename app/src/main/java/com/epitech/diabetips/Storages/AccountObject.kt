package com.epitech.diabetips.Storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

data class AccountObject (
        val email: String = "",
        val password: String = "",
        val name: String = "",
        val firstname: String = "") : Serializable {

    class Deserializer : ResponseDeserializable<AccountObject> {
        override fun deserialize(content: String) = Gson().fromJson(content, AccountObject::class.java)
    }
}