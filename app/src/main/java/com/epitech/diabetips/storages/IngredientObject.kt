package com.epitech.diabetips.storages

import java.io.Serializable

data class IngredientObject (
    var id: Int = 0,
    var quantity: Float = 0f,
    var food: FoodObject = FoodObject()) : Serializable