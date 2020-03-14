package com.epitech.diabetips.storages

import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable

data class InsulinObject (
    var id: Int = 0,
    var description: String = "",
    var quantity: Int = 0,
    var timestamp: Long = TimeHandler.instance.currentTimeSecond(),
    var type: String = "") : Serializable {

    enum class InsulinType {slow, fast, very_fast}

}