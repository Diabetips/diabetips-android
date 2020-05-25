package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class BloodSugarService : AService("/users/me/blood_sugar") {

    private object Holder { val INSTANCE = BloodSugarService() }

    companion object {
        val instance: BloodSugarService by lazy { Holder.INSTANCE }
    }

    fun getAllMeasures(page: PaginationObject) : FuelResponse<Array<BloodSugarObject>> {
        return getRequest("?" + page.getRequestParameters())
    }

    fun getMeasures(timestamp: Int) : FuelResponse<BloodSugarObject> {

        return getRequest()
    }

    fun postMeasures(biometric: BloodSugarObject) : FuelResponse<BloodSugarObject> {
        return putRequest(biometric)
    }

}