package com.epitech.diabetips.storages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Serializable

data class ChatObject (
    var id: String = "",
    var time: String = "",
    var content: String = "",
    var edited: Boolean = false,
    var with: String = "",
    var from: String = "",
    var to: String = "",
    var attachement: Array<FileObject> = arrayOf()) : Serializable

class ChatObjectAdapter : TypeAdapter<ChatObject>() {

    override fun write(writer: JsonWriter?, ChatObject: ChatObject?) {
        writer?.beginObject()
        writer?.name("content")?.value(ChatObject?.content)
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): ChatObject {
        return ChatObject()
    }
}