package com.epitech.diabetips.managers

import android.content.Context

class AuthManager : AManager("auth_token") {

    private val ACCESS_TOKEN : String = "access_token"
    private val REFRESH_TOKEN : String = "refresh_token"

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

    fun hasRefreshToken(context: Context) : Boolean {
        return getString(context, REFRESH_TOKEN).isNotBlank()
    }

}