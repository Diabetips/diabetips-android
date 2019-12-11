package com.epitech.diabetips.storages

import java.io.Serializable

data class FoodObject (
    var id: Int = 0,
    var name: String = "",
    var unit: String = "") : Serializable