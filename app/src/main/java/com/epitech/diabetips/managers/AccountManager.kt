package com.epitech.diabetips.managers

import android.content.Context
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.storages.AccountObject

class AccountManager : AObjectManager<AccountObject>("account_object")  {

    private object Holder { val INSTANCE = AccountManager() }

    companion object {
        val instance: AccountManager by lazy { Holder.INSTANCE }
    }

    fun saveAccount(context: Context, account: AccountObject) {
        saveObject(context, account)
    }

    fun getAccount(context: Context) : AccountObject {
        return getObject(context, AccountObject::class.java)
    }

    fun removeAccount(context: Context) {
        return removePreferenceKey(context)
    }

}