package com.epitech.diabetips.storages

import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable

data class NoteObject (
    var id: Int = 0,
    var description: String = "",
    var timestamp: Long = TimeHandler.instance.currentTimeSecond()) : Serializable