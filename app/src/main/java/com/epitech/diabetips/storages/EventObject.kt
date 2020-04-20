package com.epitech.diabetips.storages

import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable

data class EventObject (
    var id: Int = 0,
    var description: String = "",
    var start: Long = TimeHandler.instance.currentTimeSecond(),
    var end: Long = TimeHandler.instance.currentTimeSecond()) : Serializable