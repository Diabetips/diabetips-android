package com.epitech.diabetips.services

import android.util.Log
import com.epitech.diabetips.storages.*
import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

typealias FuelResponse<ResponseObject> = Single<Pair<Response, Result<ResponseObject, FuelError>>>

abstract class AService(var baseRoute : String = "") {

    val customGson : Gson = GsonBuilder()
        .registerTypeAdapter(UserObject::class.java, UserObjectAdapter())
        .registerTypeAdapter(RecipeObject::class.java, RecipeObjectAdapter())
        .registerTypeAdapter(MealObject::class.java, MealObjectAdapter())
        .registerTypeAdapter(IngredientObject::class.java, IngredientObjectAdapter())
        .registerTypeAdapter(ChatObject::class.java, ChatObjectAdapter())
        .create()

    inline fun <reified T : Any> getRequest(route : String = "") : FuelResponse<T> {
        Log.d("REQUEST [GET]","$baseRoute$route")
        return subscribeToRequest("$baseRoute$route".httpGet())
    }

    inline fun <reified T : Any> deleteRequest(route : String = "") : FuelResponse<T> {
        Log.d("REQUEST [DELETE]","$baseRoute$route")
        return subscribeToRequest("$baseRoute$route".httpDelete())
    }

    inline fun <reified T : Any> postRequest(postObject : T, route : String = "") : FuelResponse<T> {
        Log.d("REQUEST [POST]","$baseRoute$route")
        return subscribeToRequest("$baseRoute$route".httpPost().body(customGson.toJson(postObject)))
    }

    inline fun <reified T : Any> postData(data : ByteArray, route : String = "") : FuelResponse<T> {
        Log.d("REQUEST [POST]","$baseRoute$route")
        return subscribeToRequest("$baseRoute$route".httpPost().body(data))
    }

    inline fun <reified T : Any> postUrlEncodedRequest(parameters : List<Pair<String, String>>, route : String = "") : FuelResponse<T> {
        Log.d("REQUEST [POST]","$baseRoute$route")
        return subscribeToRequest("$baseRoute$route".httpPost(parameters).header())
    }

    inline fun <reified T : Any> putRequest(putObject : T, route : String = "") : FuelResponse<T> {
        Log.d("REQUEST [PUT]","$baseRoute$route")
        return subscribeToRequest("$baseRoute$route".httpPut().body(customGson.toJson(putObject)))
    }

    inline fun <reified T : Any> subscribeToRequest(request: Request) : FuelResponse<T>  {
        return request.rx_responseObject(ObjectDeserializer<T>())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.e("API error on ${request.method.value} ${request.url}", err.message!!)
            }
    }
}