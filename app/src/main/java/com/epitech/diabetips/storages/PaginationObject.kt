package com.epitech.diabetips.storages

import java.io.Serializable

data class PaginationObject (
    var size: Int,
    var default: Int,
    var current: Int = default,
    var previous: Int = default,
    var next: Int = default,
    var last: Int = default,
    var start: Long = 0,
    var end: Long = 0,
    var periodEnable: Boolean = false) : Serializable {
    var updated: Boolean = false

    fun reset() {
        current = default
        previous = default
        next = default
        last = default
        updated = false
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
        return current >= last && updated
    }

    fun getRequestParameters() : String {
        updated = true
        if (periodEnable)
            return "page=$current&size=$size&start=$start&end=$end"
        return "page=$current&size=$size"
    }

    fun updateFromHeader(header: String?) {
        if (header == null)
            return
        val parameters = header.trim().split(";")
        val values = mutableMapOf<String, Any?>()
        parameters.forEach {
            val parameter = it.trim().split(":", "=")
            values[parameter[0]] = parameter[1].trim().toIntOrNull()
        }
        if (values["previous"] != null)
            previous = values["previous"]!! as Int
        if (values["next"] != null)
            next = values["next"]!! as Int
        if (values["last"] != null)
            last = values["last"]!! as Int
    }

    fun setInterval(start: Long, end: Long) {
        this.start = start
        this.end = end
        this.periodEnable = true
    }

}