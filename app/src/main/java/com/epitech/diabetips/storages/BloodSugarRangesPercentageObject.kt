package com.epitech.diabetips.storages

import java.io.Serializable

data class BloodSugarRangesPercentageObject (
    var hypoglycemia: Float? = 0f,
    var hyperglycemia: Float? = 0f,
    var in_target: Float? = 100f,
    var start: String? = null,
    var end: String? = null,
    var format: String? = null
) : Serializable