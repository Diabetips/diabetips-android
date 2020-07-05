package com.epitech.diabetips.services

import com.epitech.diabetips.storages.BiometricObject

class BiometricService : AObjectService<BiometricObject>("/users/me/biometrics") {

    private object Holder { val INSTANCE = BiometricService() }

    companion object {
        val instance: BiometricService by lazy { Holder.INSTANCE }
    }

}