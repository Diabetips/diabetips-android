package com.epitech.diabetips.Services

import android.util.Log
import com.epitech.diabetips.Storages.AccountObject
import com.epitech.diabetips.Storages.AccountObjectAdapter
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

typealias FuelResponse<ResponseObject> = Single<Pair<Response, Result<ResponseObject, FuelError>>>

class DiabetipsService {

    val customGson = GsonBuilder()
        .registerTypeAdapter(AccountObject::class.java, AccountObjectAdapter())
        .create()

    private object Holder { val INSTANCE = DiabetipsService() }

    companion object {
        val instance: DiabetipsService by lazy { Holder.INSTANCE }
    }

    fun login(account: AccountObject) : FuelResponse<AccountObject> {
        return "login".httpPost().body(customGson.toJson(account))
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("login error", err.message)
            }
    }

    fun postUser(account: AccountObject) : FuelResponse<AccountObject> {
        return "v1/users".httpPost().body(customGson.toJson(account))
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("signUp error", err.message)
            }
    }

    fun getUser(uid: String) : FuelResponse<AccountObject> {
        //FuelManager.instance.basePath = "https://next.json-generator.com/api/json/get/4yMg6WlnL" // remove this line when using the real API
        return ("v1/users/" + uid).httpGet()
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getUser error", err.message)
            }
    }

    fun getUsers(uid: String) : FuelResponse<Array<AccountObject>> {
        return ("v1/users/" + uid).httpGet()
            .rx_responseObject(AccountObject.ArrayDeserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getUsers error", err.message)
            }
    }

    fun putUser(account: AccountObject) : FuelResponse<AccountObject> {
        return ("v1/users/" + account.uid).httpPut().body(customGson.toJson(account))
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("changeEmail error", err.message)
            }
    }

}