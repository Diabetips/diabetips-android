package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class BloodSugarObject (
    var value: Int = 0,
    var start: String = "",
    var interval: Int? = null,
    var time: String = "",
    var measures: Array<Int> = arrayOf()) : Serializable {
}

class BloodSugarObjectAdapter : TypeAdapter<BloodSugarObject>() {

    override fun write(writer: JsonWriter?, bsObject: BloodSugarObject?) {
        writer?.beginObject()
        writer?.name("start")?.value(bsObject?.start)
        writer?.name("interval")?.value(bsObject?.interval)
        writer?.name("measures")
        writer?.beginArray()
        bsObject?.measures?.forEach {
            writer?.value(it)
        }
        writer?.endArray()
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): BloodSugarObject {
        return BloodSugarObject()
    }
}