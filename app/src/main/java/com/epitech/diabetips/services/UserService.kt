package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class UserService : AService("/users") {

    private object Holder { val INSTANCE = UserService() }

    companion object {
        val instance: UserService by lazy { Holder.INSTANCE }
    }

    fun login(account: AccountObject) : FuelResponse<AccountObject> {
        return postRequest(account, "/login")
    }

    fun registerUser(account: AccountObject) : FuelResponse<AccountObject> {
        return postRequest(account)
    }

    fun getUser(uid: String) : FuelResponse<AccountObject> {
        return getRequest("/" + uid)
    }

    fun getAllUsers() : FuelResponse<Array<AccountObject>> {
        return getRequest()
    }

    fun updateUser(account: AccountObject) : FuelResponse<AccountObject> {
        return putRequest(account, "/" + account.uid)
    }

}