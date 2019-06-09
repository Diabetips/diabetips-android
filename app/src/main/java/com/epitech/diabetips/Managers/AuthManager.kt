package com.epitech.diabetips.Managers

class AuthManager : AManager("auth_token", "access_token") {

    private object Holder { val INSTANCE = AuthManager() }

    companion object {
        val instance: AuthManager by lazy { Holder.INSTANCE }
    }

}