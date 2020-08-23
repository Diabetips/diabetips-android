package com.epitech.diabetips.storages

import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable

data class Hba1cObject (
    var id: Int = 0,
    var value: Float = 0f,
    var time: String = "") : Serializable