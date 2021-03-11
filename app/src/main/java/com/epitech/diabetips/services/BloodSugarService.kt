package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class BloodSugarService : AService("/users/me/blood_sugar") {

    private object Holder { val INSTANCE = BloodSugarService() }

    companion object {
        val instance: BloodSugarService by lazy { Holder.INSTANCE }
    }

    fun getAllMeasures(page: PaginationObject) : FuelResponse<Array<BloodSugarObject>> {
        return getRequest("?${page.getRequestParameters()}")
    }

    fun deleteAllMeasures(start: String = "", end: String = "") : FuelResponse<BloodSugarObject> {
        return deleteRequest("?start=${start}&end=${end}")
    }

    fun getLastMeasure() : FuelResponse<BloodSugarObject> {
        return getRequest("/last")
    }

    fun postMeasures(biometric: BloodSugarObject) : FuelResponse<BloodSugarObject> {
        return putRequest(biometric)
    }

    fun getRangesPercentage(calculationOption: CalculationOptionObject): FuelResponse<BloodSugarRangesPercentageObject> {
        return getRequest("/target?" + calculationOption.getRequestParameters())
    }

    fun getCalculations(calculationOption: CalculationOptionObject) : FuelResponse<CalculationObject> {
        return getRequest("/calculations?" + calculationOption.getRequestParameters())
    }

    fun getAggregateCalculations(calculationOption: CalculationOptionObject) : FuelResponse<AggregateCalculationObject> {
        return getRequest("/calculations/aggregate?" + calculationOption.getRequestParameters())
    }

}