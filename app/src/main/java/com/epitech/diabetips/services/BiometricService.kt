package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class BiometricService : AService("/users/me/biometrics") {

    private object Holder { val INSTANCE = BiometricService() }

    companion object {
        val instance: BiometricService by lazy { Holder.INSTANCE }
    }

    fun getUserBiometric() : FuelResponse<BiometricObject> {
        return getRequest()
    }

    fun updateUserBiometric(biometric: BiometricObject) : FuelResponse<BiometricObject> {
        return postRequest(biometric)
    }

}