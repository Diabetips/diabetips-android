package com.epitech.diabetips.services

import android.util.Log
import com.epitech.diabetips.storages.*
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

typealias FuelResponse<ResponseObject> = Single<Pair<Response, Result<ResponseObject, FuelError>>>

abstract class AService(protected var baseRoute : String = "") {

    protected val customGson : Gson = GsonBuilder()
        .registerTypeAdapter(AccountObject::class.java, AccountObjectAdapter())
        .registerTypeAdapter(RecipeObject::class.java, RecipeObjectAdapter())
        .registerTypeAdapter(MealObject::class.java, MealObjectAdapter())
        .create()

    protected inline fun <reified T : Any> getRequest(route : String = "") : FuelResponse<T> {
        return subscribeToRequest((baseRoute + route).httpGet())
    }

    protected inline fun <reified T : Any> deleteRequest(route : String = "") : FuelResponse<T> {
        return subscribeToRequest((baseRoute + route).httpDelete())
    }

    protected inline fun <reified T : Any> postRequest(postObject : T, route : String = "") : FuelResponse<T> {
        return subscribeToRequest((baseRoute + route).httpPost().body(customGson.toJson(postObject)))
    }

    protected inline fun <reified T : Any> putRequest(putObject : T, route : String = "") : FuelResponse<T> {
        return subscribeToRequest((baseRoute + route).httpPut().body(customGson.toJson(putObject)))
    }

    protected inline fun <reified T : Any> subscribeToRequest(request: Request) : FuelResponse<T>  {
        return request.rx_responseObject(ObjectDeserializer<T>())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.e("API error on " + request.method.value + " " + request.url.toString(), err.message!!)
            }
    }

}