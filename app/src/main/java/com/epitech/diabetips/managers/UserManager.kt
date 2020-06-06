package com.epitech.diabetips.managers

import android.content.Context
import com.epitech.diabetips.storages.UserObject
import com.epitech.diabetips.storages.BiometricObject

class UserManager : AObjectManager<UserObject>("user_object")  {

    private val USER : String = "user"
    private val BIOMETRIC : String = "biometric"

    private object Holder { val INSTANCE = UserManager() }

    companion object {
        val instance: UserManager by lazy { Holder.INSTANCE }
    }

    fun saveUser(context: Context, user: UserObject) {
        saveObject(context, user, USER)
    }

    fun saveBiometric(context: Context, biometric: BiometricObject) {
        saveObject(context, biometric, BIOMETRIC)
    }

    fun getUser(context: Context) : UserObject {
        return getObject(context, UserObject::class.java, USER) ?: UserObject()
    }

    fun getBiometric(context: Context) : BiometricObject {
        return getObject(context, BiometricObject::class.java, BIOMETRIC) ?: BiometricObject()
    }

    fun removeUser(context: Context) {
        return removePreferenceKey(context, USER)
    }

    fun removeBiometric(context: Context) {
        return removePreferenceKey(context, BIOMETRIC)
    }

}