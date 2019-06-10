package com.epitech.diabetips.Managers

import android.content.Context

class AuthManager : AManager("auth_token") {

    val ACCESS_TOKEN : String = "access_token"
    val REFRESH_TOKEN : String = "refresh_token"

    private object Holder {
        val INSTANCE = AuthManager()
    }

    companion object {
        val instance: AuthManager by lazy { Holder.INSTANCE }
    }

    fun saveAccessToken(context: Context, accessToken: String) {
        saveString(context, accessToken, ACCESS_TOKEN)
    }

    fun getAccessToken(context: Context) : String {
        return getString(context, ACCESS_TOKEN)
    }

    fun saveRefreshToken(context: Context, refreshToken: String) {
        saveString(context, refreshToken, REFRESH_TOKEN)
    }

    fun getRefreshToken(context: Context) : String {
        return getString(context, REFRESH_TOKEN)
    }

}