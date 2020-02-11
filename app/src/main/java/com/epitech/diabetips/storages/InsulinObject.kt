package com.epitech.diabetips.storages

import java.io.Serializable

data class InsulinObject (
    var id: Int = 0,
    var description: String = "",
    var quantity: Int = 0,
    var timestamp: Long = 0) : Serializable