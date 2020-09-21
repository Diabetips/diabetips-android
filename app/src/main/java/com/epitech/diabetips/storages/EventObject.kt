package com.epitech.diabetips.storages

import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable

data class EventObject (
    var id: Int = 0,
    var description: String = "",
    var start: String = "",
    var end: String = "") : Serializable