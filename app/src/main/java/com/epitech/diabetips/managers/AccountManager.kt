package com.epitech.diabetips.managers

import android.content.Context
import com.epitech.diabetips.storages.AccountObject
import com.epitech.diabetips.storages.BiometricObject

class AccountManager : AObjectManager<AccountObject>("account_object")  {

    private val ACCOUNT : String = "account"
    private val BIOMETRIC : String = "biometric"

    private object Holder { val INSTANCE = AccountManager() }

    companion object {
        val instance: AccountManager by lazy { Holder.INSTANCE }
    }

    fun saveAccount(context: Context, account: AccountObject) {
        saveObject(context, account, ACCOUNT)
    }

    fun saveBiometric(context: Context, biometric: BiometricObject) {
        saveObject(context, biometric, BIOMETRIC)
    }

    fun getAccount(context: Context) : AccountObject {
        return getObject(context, AccountObject::class.java, ACCOUNT) ?: AccountObject()
    }

    fun getBiometric(context: Context) : BiometricObject {
        return getObject(context, BiometricObject::class.java, BIOMETRIC) ?: BiometricObject()
    }

    fun removeAccount(context: Context) {
        return removePreferenceKey(context)
    }

}