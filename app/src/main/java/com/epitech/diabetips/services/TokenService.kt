package com.epitech.diabetips.services

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.storages.*
import com.github.kittinunf.fuel.core.FuelManager
import java.util.*
import kotlin.concurrent.schedule

class TokenService : AService("/auth") {

    private object Holder { val INSTANCE = TokenService() }

    companion object {
        val instance: TokenService by lazy { Holder.INSTANCE }
    }

    private val refreshTimer = Timer("refreshTimer", false)
    private var refreshTask : TimerTask? = null

    fun getToken(context: Context, username: String, password: String) : FuelResponse<TokenObject> {
        return tokenRequest(context,
            AuthorizationObject(
                grant_type = AuthorizationObject.Type.password.name,
                username = username,
                password = password,
                scope = context.getString(R.string.scope)))
    }

    fun refreshToken(context: Context) : FuelResponse<TokenObject> {
        return tokenRequest(context,
            AuthorizationObject(
                grant_type = AuthorizationObject.Type.refresh_token.name,
                refresh_token = AuthManager.instance.getRefreshToken(context),
                scope = context.getString(R.string.scope)))
    }

    private fun tokenRequest(context: Context, authorization: AuthorizationObject): FuelResponse<TokenObject> {
        resetRefreshTimer()
        val oldHeaders = setBasicHeader(context)
        return postUrlEncodedRequest<TokenObject>(authorization.getParameters(), "/token").doOnSuccess {
                if (it.second.component2() == null) {
                    saveToken(context, it.second.component1()!!)
                } else {
                    FuelManager.instance.baseHeaders = oldHeaders
                }
            }.doOnError {
                FuelManager.instance.baseHeaders = oldHeaders
            }
    }


    fun resetPassword(context: Context, email: String) : FuelResponse<UserObject> {
        return resetHeader(setBasicHeader(context, R.string.content_json), postRequest(UserObject("", email), "/reset-password"))
    }

    private fun saveToken(context: Context, token: TokenObject) {
        AuthManager.instance.saveAccessToken(context, token.access_token)
        AuthManager.instance.saveRefreshToken(context, token.refresh_token)
        FuelManager.instance.baseHeaders = mapOf(
            "Content-Type" to context.getString(R.string.content_json),
            "Authorization" to "${token.token_type.capitalize()} ${token.access_token}")
        refreshTask = refreshTimer.schedule((token.expires_in * 750).toLong()) {
            refreshToken(context).subscribe()
        }
    }

    private fun resetRefreshTimer() {
        refreshTask?.cancel()
        refreshTimer.purge()
    }

    fun <T : Any> resetHeader(oldHeaders: Map<String, String>?, request: FuelResponse<T>) : FuelResponse<T> {
        return request.doOnSuccess {
            FuelManager.instance.baseHeaders = oldHeaders
        }.doOnError {
            FuelManager.instance.baseHeaders = oldHeaders
        }
    }

    fun setBasicHeader(context: Context, contentType: Int = R.string.content_urlencoded) : Map<String, String>? {
        val oldHeaders = FuelManager.instance.baseHeaders
        FuelManager.instance.baseHeaders = mapOf(
            "Content-Type" to context.getString(contentType),
            "Authorization" to "Basic ${android.util.Base64.encodeToString("${context.getString(R.string.client_id)}:${context.getString(R.string.client_secret)}".toByteArray(), android.util.Base64.NO_WRAP)}")
        return oldHeaders
    }
}