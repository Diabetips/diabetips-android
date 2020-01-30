package com.epitech.diabetips.services

import android.content.Context
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.storages.*
import com.github.kittinunf.fuel.core.FuelManager

class TokenService : AService("/auth") {

    private object Holder { val INSTANCE = TokenService() }

    companion object {
        val instance: TokenService by lazy { Holder.INSTANCE }
    }

    fun getToken(context: Context, username: String, password: String) : FuelResponse<TokenObject> {
        val oldHeaders = FuelManager.instance.baseHeaders
        FuelManager.instance.baseHeaders = null
        return postUrlEncodedRequest<TokenObject>(listOf("grant_type" to "password", "username" to username, "password" to password),"/token")
            .doOnSuccess {
                if (it.second.component2() == null) {
                    saveToken(context, it.second.component1()!!)
                }
            }.doAfterTerminate {
                if (FuelManager.instance.baseHeaders == null) {
                    FuelManager.instance.baseHeaders = oldHeaders
                }
            }
    }

    fun refreshToken(context: Context) : FuelResponse<TokenObject> {
        val oldHeaders = FuelManager.instance.baseHeaders
        FuelManager.instance.baseHeaders = null
        return postUrlEncodedRequest<TokenObject>(listOf("grant_type" to "refresh_token", "refresh_token" to AuthManager.instance.getRefreshToken(context)),"/token")
            .doOnSuccess {
                if (it.second.component2() == null) {
                    saveToken(context, it.second.component1()!!)
                }
            }.doAfterTerminate {
                if (FuelManager.instance.baseHeaders == null) {
                    FuelManager.instance.baseHeaders = oldHeaders
                }
            }
    }

    fun resetPassword(email: String) : FuelResponse<AccountObject> {
        return postRequest(AccountObject("", email), "/reset-password")
    }

    private fun saveToken(context: Context, token: TokenObject) {
        AuthManager.instance.saveAccessToken(context, token.access_token)
        AuthManager.instance.saveRefreshToken(context, token.refresh_token)
        FuelManager.instance.baseHeaders = mapOf(
            "Content-Type" to "application/json; charset=utf-8",
            "Authorization" to token.token_type.capitalize() + " " + token.access_token)
    }

}