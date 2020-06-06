package com.epitech.diabetips.services

import android.graphics.Bitmap
import com.epitech.diabetips.utils.ImageHandler
import com.github.kittinunf.fuel.core.FuelManager

abstract class AObjectPictureService<Object: Any>(baseRoute: String) : AObjectService<Object>(baseRoute) {

    inline fun <reified Obj: Object> updatePicture(image: Bitmap, id: Int) : FuelResponse<Obj> {
        return updatePicture(image, id.toString())
    }
    inline fun <reified Obj: Object> updatePicture(image: Bitmap, uid: String = "me") : FuelResponse<Obj> {
        return postData(ImageHandler.instance.encodeImage(image, 300), "/$uid/picture")
    }

    inline fun <reified Obj: Object> removePicture(id: Int) : FuelResponse<Obj> {
        return removePicture(id.toString())
    }

    inline fun <reified Obj: Object> removePicture(uid: String = "me") : FuelResponse<Obj> {
        return deleteRequest("/$uid/picture")
    }

    fun getPictureUrl(id: Int) : String  {
        return getPictureUrl(id.toString())
    }

    fun getPictureUrl(uid: String = "me") : String  {
        return "${FuelManager.instance.basePath}$baseRoute/$uid/picture"
    }

}