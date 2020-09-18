package com.epitech.diabetips.storages

import java.io.Serializable

data class NutritionalObject (
    var type: NutritionalType = NutritionalType.CALORIE,
    var value: Float = 0f) : Serializable {

    enum class NutritionalType {CALORIE, CARBOHYDRATE, SUGAR, FAT, SATURATED_FAT, FIBER, PROTEIN}

}