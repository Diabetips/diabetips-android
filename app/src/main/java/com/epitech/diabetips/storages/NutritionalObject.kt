package com.epitech.diabetips.storages

import java.io.Serializable

data class NutritionalObject (
    var type: NutritionalType = NutritionalType.CALORIE,
    var value: Float = 0f) : Serializable {

    enum class NutritionalType {CALORIE, CARBOHYDRATE, SUGAR, FAT, SATURATED_FAT, FIBER, PROTEIN, NUTRI_SCORE}

    companion object {
        fun getDefaultValues(): ArrayList<NutritionalObject> {
            val nutritionalValues = ArrayList<NutritionalObject>()
            nutritionalValues.add(NutritionalObject(NutritionalType.CALORIE))
            nutritionalValues.add(NutritionalObject(NutritionalType.CARBOHYDRATE))
            nutritionalValues.add(NutritionalObject(NutritionalType.SUGAR))
            nutritionalValues.add(NutritionalObject(NutritionalType.FAT))
            nutritionalValues.add(NutritionalObject(NutritionalType.SATURATED_FAT))
            nutritionalValues.add(NutritionalObject(NutritionalType.FIBER))
            nutritionalValues.add(NutritionalObject(NutritionalType.PROTEIN))
            return nutritionalValues
        }
    }

}