package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class UserObject (
    var uid: String = "",
    var email: String = "",
    var password: String = "",
    var first_name: String = "",
    var last_name: String = "",
    var lang: String = "") : Serializable

class UserObjectAdapter : TypeAdapter<UserObject>() {

    override fun write(writer: JsonWriter?, UserObject: UserObject?) {
        writer?.beginObject()
        writer?.name("email")?.value(UserObject?.email)
        writer?.name("first_name")?.value(UserObject?.first_name)
        writer?.name("last_name")?.value(UserObject?.last_name)
        writer?.name("lang")?.value(UserObject?.lang)
        if (UserObject?.password!!.isNotEmpty()) {
            writer?.name("password")?.value(UserObject.password)
        }
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): UserObject {
        return UserObject()
    }
}