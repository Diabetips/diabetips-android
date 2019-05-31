package com.epitech.diabetips.Services

import android.util.Log
import com.epitech.diabetips.Storages.AccountObject
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

typealias FuelResponse<ResponseObject> = Single<Pair<Response, Result<ResponseObject, FuelError>>>

class DiabetipsService {


    private object Holder { val INSTANCE = DiabetipsService() }

    companion object {
        val instance: DiabetipsService by lazy { Holder.INSTANCE }
    }

    fun login(account: AccountObject) : FuelResponse<AccountObject> {
        return "login".httpPost()
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("login error", err.message)
            }
    }

    fun signUp(account: AccountObject) : FuelResponse<AccountObject> {
        return "signUp".httpPost()
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("signUp error", err.message)
            }
    }

    fun getAccount() : FuelResponse<AccountObject> {
        FuelManager.instance.basePath = "https://next.json-generator.com/api/json/get/" // remove this line when using the real API
        return "4yMg6WlnL".httpGet()
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getAccount error", err.message)
            }
    }

    fun changeEmail(newEmail: String) : FuelResponse<ByteArray> {
        return "changeEmail".httpPost().rx_response()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("changeEmail error", err.message)
            }
    }

    fun changePassword(newPassword: String) : FuelResponse<ByteArray> {
        return "changePassword".httpPost().rx_response()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("changePassword error", err.message)
            }
    }

}