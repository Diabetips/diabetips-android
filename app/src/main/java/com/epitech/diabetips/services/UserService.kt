package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

class UserService : AService("/users") {

    private object Holder { val INSTANCE = UserService() }

    companion object {
        val instance: UserService by lazy { Holder.INSTANCE }
    }

    fun registerUser(account: AccountObject) : FuelResponse<AccountObject> {
        return postRequest(account)
    }

    fun getUser(uid: String = "me") : FuelResponse<AccountObject> {
        return getRequest("/" + uid)
    }

    fun getAllUsers(page: PaginationObject) : FuelResponse<Array<AccountObject>> {
        return getRequest("?page=" + page.current + "&size=" + page.size)
    }

    fun updateUser(account: AccountObject) : FuelResponse<AccountObject> {
        return putRequest(account, "/" + account.uid)
    }

    fun getPicture(uid: String = "me"): FuelResponse<AccountObject>  {
        return getRequest("/" + uid + "/picture")

    }

    fun updatePicture(account: AccountObject) : FuelResponse<AccountObject> {
        return postRequest(account, "/" + account.uid + "/picture")
    }

}