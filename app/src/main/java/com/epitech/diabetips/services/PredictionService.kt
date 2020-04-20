package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class PredictionService : AService("/users/me/predictions") {

    private object Holder { val INSTANCE = PredictionService() }

    companion object {
        val instance: PredictionService by lazy { Holder.INSTANCE }
    }

    fun getUserPrediction() : FuelResponse<PredictionObject> {
        return getRequest("/predict")
    }

    fun getUserPredictionSettings() : FuelResponse<PredictionSettingsObject> {
        return getRequest("/settings")
    }

}