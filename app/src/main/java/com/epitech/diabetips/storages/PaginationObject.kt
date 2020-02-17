package com.epitech.diabetips.storages

import java.io.Serializable

data class PaginationObject (
    var size: Int,
    var default: Int,
    var current: Int = default,
    var previous: Int = default,
    var next: Int = default,
    var last: Int = default) : Serializable {

    fun reset() {
        current = default
        previous = default
        next = default
        last = default
    }

    fun previousPage() {
        current = previous
    }

    fun nextPage() {
        current = next
    }

    fun isFirst() : Boolean {
        return current <= previous
    }

    fun isLast() : Boolean {
        return current >= last
    }

    fun getRequestParameters() : String {
        return "page=" + current + "&size=" + size
    }

    fun updateFromHeader(header: String?) {
        if (header == null)
            return
        val parameters = header.trim().split(";")
        val values = mutableMapOf<String, Int?>()
        parameters.forEach {
            val parameter = it.trim().split(":")
            values[parameter[0]] = parameter[1].trim().toIntOrNull()
        }
        if (values["previous"] != null)
            previous = values["previous"]!!
        if (values["next"] != null)
            next = values["next"]!!
        if (values["last"] != null)
            last = values["last"]!!
    }

}