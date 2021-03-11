package com.epitech.diabetips.services

import com.epitech.diabetips.storages.FoodObject

class FoodService : AObjectPictureService<FoodObject>("/food") {

    private object Holder { val INSTANCE = FoodService() }

    companion object {
        val instance: FoodService by lazy { Holder.INSTANCE }
    }

    fun getBarcode(barcode: String) : FuelResponse<Array<FoodObject>> {
        return getRequest("?code=$barcode")
    }

}