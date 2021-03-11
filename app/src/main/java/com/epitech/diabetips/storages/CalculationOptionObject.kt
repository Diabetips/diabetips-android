package com.epitech.diabetips.storages

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable

data class CalculationOptionObject (
    var start: String = "",
    var end: String = "",
    var format: String = "",
    var count: Boolean = false,
    var average: Boolean = false,
    var first: Boolean = false,
    var third: Boolean = false) : Serializable {

    enum class FormatType { time, percentage }

    fun getRequestParameters() : String {
        var parameters = "start=$start&end=$end"
        if (format.isNotEmpty())
            parameters += "&format=$format"
        if (average)
            parameters += "&calcs=average"
        if (count)
            parameters += "&calcs=count"
        if (first)
            parameters += "&calcs=first"
        if (third)
            parameters += "&calcs=third"
        return parameters
    }

    fun setInterval(context: Context, interval: Pair<String, String>) {
        setInterval(context, interval.first, interval.second)
    }

    fun setInterval(context: Context, start: String, end: String) {
        this.start = TimeHandler.instance.changeTimeFormat(start, context.getString(R.string.format_time_api), context.getString(R.string.format_time_api_UTC), false, true) ?: start
        this.end = TimeHandler.instance.changeTimeFormat(end, context.getString(R.string.format_time_api), context.getString(R.string.format_time_api_UTC), false, true) ?: end
    }

}