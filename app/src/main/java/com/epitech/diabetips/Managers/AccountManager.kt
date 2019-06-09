package com.epitech.diabetips.Managers

import android.content.Context
import com.epitech.diabetips.Storages.AccountObject

class AccountManager : AObjectManager<AccountObject>("account_object")  {

    private object Holder { val INSTANCE = AccountManager() }

    companion object {
        val instance: AccountManager by lazy { Holder.INSTANCE }
    }

    fun getAccount(context: Context) : AccountObject {
        return getObject(context, AccountObject::class.java)
    }
}