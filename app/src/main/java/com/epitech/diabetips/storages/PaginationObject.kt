package com.epitech.diabetips.storages

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable

data class PaginationObject (
    var size: Int,
    var default: Int,
    var current: Int = default,
    var previous: Int = default,
    var next: Int = default,
    var last: Int = default,
    var start: String = "",
    var end: String = "",
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

    fun setInterval(context: Context, interval: Pair<String, String>) {
        setInterval(context, interval.first, interval.second)
    }

    fun setInterval(context: Context, start: String, end: String) {
        this.start = TimeHandler.instance.changeTimeFormat(start, context.getString(R.string.format_time_api), context.getString(R.string.format_time_api_UTC), false, true) ?: start
        this.end = TimeHandler.instance.changeTimeFormat(end, context.getString(R.string.format_time_api), context.getString(R.string.format_time_api_UTC), false, true) ?: end
        this.periodEnable = true
    }

    fun getTimestampInterval(context: Context) : Pair<Long, Long> {
        return Pair(
            TimeHandler.instance.getTimestampFromFormat(start, context.getString(R.string.format_time_api)) ?: 0,
            TimeHandler.instance.getTimestampFromFormat(end, context.getString(R.string.format_time_api)) ?: 0
        )
    }

}