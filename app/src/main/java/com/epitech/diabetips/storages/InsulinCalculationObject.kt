package com.epitech.diabetips.storages

import java.io.Serializable

data class InsulinCalculationObject (
    var start: String = "",
    var end: String = "",
    var average: Array<InsulinsQuantityObject?> = arrayOf(),
    var count: Array<InsulinsQuantityObject?> = arrayOf()) : Serializable