package com.epitech.diabetips.storages

import java.io.Serializable

data class FoodObject (
    var id: Int = 0,
    var name: String = "",
    var unit: String = "",
    var sugars_100g: Float = 0f) : Serializable {

    fun getNutritionalValues(quantity: Float = 100f) : ArrayList<NutritionalObject> {
        val nutritionalValues = ArrayList<NutritionalObject>()
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.CALORIE, 666f / 100f * quantity))
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.CARBOHYDRATE, 50f / 100f * quantity))
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.SUGAR, 35f / 100f * quantity))
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.FAT, 40f / 100f * quantity))
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.SATURATED_FAT, 30f / 100f * quantity))
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.FIBER, 5f / 100f * quantity))
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.PROTEIN, 25f / 100f * quantity))
        return nutritionalValues
    }
}