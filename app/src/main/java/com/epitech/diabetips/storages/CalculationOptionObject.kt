package com.epitech.diabetips.storages

import java.io.Serializable

data class CalculationOptionObject (
    var start: String = "",
    var end: String = "",
    var average: Boolean = false,
    var first: Boolean = false,
    var third: Boolean = false) : Serializable {

    fun getRequestParameters() : String {
        var parameters = "start=$start&end=$end"
        if (average)
            parameters += "&calcs=average"
        if (first)
            parameters += "&calcs=first"
        if (third)
            parameters += "&calcs=third"
        return parameters
    }

    fun setInterval(interval: Pair<String, String>) {
        start = interval.first
        end = interval.second
    }

}