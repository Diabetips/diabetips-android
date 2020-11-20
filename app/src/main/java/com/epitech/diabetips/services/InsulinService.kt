package com.epitech.diabetips.services

import com.epitech.diabetips.storages.AggregateCalculationObject
import com.epitech.diabetips.storages.CalculationOptionObject
import com.epitech.diabetips.storages.InsulinCalculationObject
import com.epitech.diabetips.storages.InsulinObject

class InsulinService : AObjectService<InsulinObject>("/users/me/insulin") {

    private object Holder { val INSTANCE = InsulinService() }

    companion object {
        val instance: InsulinService by lazy { Holder.INSTANCE }
    }

    fun getCalculations(calculationOption: CalculationOptionObject) : FuelResponse<InsulinCalculationObject> {
        return getRequest("/calculations?" + calculationOption.getRequestParameters())
    }
}