package com.epitech.diabetips.storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
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

class AccountObjectAdapter : TypeAdapter<AccountObject>() {

    override fun write(writer: JsonWriter?, accountObject: AccountObject?) {
        writer?.beginObject()
        writer?.name("email")?.value(accountObject?.email)
        writer?.name("first_name")?.value(accountObject?.first_name)
        writer?.name("last_name")?.value(accountObject?.last_name)
        if (accountObject?.password!!.isNotEmpty()) {
            writer?.name("password")?.value(accountObject.password)
        }
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): AccountObject {
        return AccountObject()
    }
}