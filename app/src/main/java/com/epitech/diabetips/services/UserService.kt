package com.epitech.diabetips.services

import android.graphics.Bitmap
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.utils.ImageHandler
import com.github.kittinunf.fuel.core.FuelManager

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
        return getRequest("?" + page.getRequestParameters())
    }

    fun updateUser(account: AccountObject) : FuelResponse<AccountObject> {
        return putRequest(account, "/" + account.uid)
    }

    fun updatePicture(image: Bitmap, uid: String = "me") : FuelResponse<AccountObject> {
        return postData(ImageHandler.instance.encodeImage(image, 300), "/" + uid + "/picture")
    }

    fun getPictureUrl(uid: String = "me") : String  {
        return FuelManager.instance.basePath + baseRoute + "/" + uid + "/picture"
    }

}