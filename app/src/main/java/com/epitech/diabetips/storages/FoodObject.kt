package com.epitech.diabetips.storages

import java.io.Serializable

data class FoodObject (
    var id: Int = 0,
    var name: String = "",
    var unit: String = "",
    var energy_100g: Float? = null,
    var carbohydrates_100g: Float? = null,
    var sugars_100g: Float? = null,
    var fat_100g: Float? = null,
    var saturated_fat_100g: Float? = null,
    var fiber_100g: Float? = null,
    var proteins_100g: Float? = null,
    var nutriscore: String? = null) : Serializable {

    fun getNutritionalValues(quantity: Float = 100f) : ArrayList<NutritionalObject> {
        val multiplier = quantity * 0.01f
        val nutritionalValues = ArrayList<NutritionalObject>()
        if (energy_100g != null)
            nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.CALORIE, energy_100g!! * multiplier))
        if (carbohydrates_100g != null)
            nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.CARBOHYDRATE, carbohydrates_100g!! * multiplier))
        if (sugars_100g != null)
            nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.SUGAR, sugars_100g!! * multiplier))
        if (fat_100g != null)
            nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.FAT, fat_100g!! * multiplier))
        if (saturated_fat_100g != null)
            nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.SATURATED_FAT, saturated_fat_100g!! * multiplier))
        if (fiber_100g != null)
            nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.FIBER, fiber_100g!! * multiplier))
        if (proteins_100g != null)
            nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.PROTEIN, proteins_100g!! * multiplier))
        nutritionalValues.add(NutritionalObject(NutritionalObject.NutritionalType.NUTRI_SCORE, nutriscore?.get(0)?.toFloat() ?: 0f))
        return nutritionalValues
    }
}