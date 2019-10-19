package com.epitech.diabetips.storages

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

open class ObjectDeserializer<T : Any>(private val clazz: Class<T>) : ResponseDeserializable<T> {

     override fun deserialize(content: String) = Gson().fromJson(content, clazz)

}

inline fun <reified T : Any> ObjectDeserializer() = ObjectDeserializer(T::class.java)