package com.epitech.diabetips.storages

import java.io.Serializable

data class PredictionObject (
    var id: Int = 0,
    var insulin: Float = 0f,
    var confidence: Float = 0f,
    var time: String = "") : Serializable