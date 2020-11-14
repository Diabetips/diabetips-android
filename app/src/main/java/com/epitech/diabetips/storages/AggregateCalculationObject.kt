package com.epitech.diabetips.storages

import java.io.Serializable

data class AggregateCalculationObject (
    var start: String = "",
    var end: String = "",
    var average: Array<Float?> = arrayOf(),
    var first: Array<Float?> = arrayOf(),
    var third: Array<Float?> = arrayOf()) : Serializable