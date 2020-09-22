package com.epitech.diabetips.storages

import java.io.Serializable

data class HbA1cObject (
    var id: Int = 0,
    var value: Float = 0f,
    var time: String = "") : Serializable