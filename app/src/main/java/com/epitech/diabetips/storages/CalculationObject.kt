package com.epitech.diabetips.storages

import java.io.Serializable

data class CalculationObject (
    var start: String = "",
    var end: String = "",
    var average: Float? = null,
    var first: Float? = null,
    var third: Float? = null) : Serializable