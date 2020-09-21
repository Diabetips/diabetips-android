package com.epitech.diabetips.storages

import java.io.Serializable

data class AuthorizationObject (
    var grant_type: String = "",
    var username: String = "",
    var password: String = "",
    var refresh_token: String = "",
    var scope: String = "") : Serializable {

    enum class Type {password, refresh_token}

    fun getParameters() : List<Pair<String, String>> {
        val map = ArrayList<Pair<String, String>>()
        map.add(Pair("grant_type", grant_type))
        map.add(Pair("scope", scope))
        when (grant_type) {
            Type.password.name -> {
                map.add(Pair("username", username))
                map.add(Pair("password", password))
            }
            Type.refresh_token.name -> map.add(Pair("refresh_token", refresh_token))
        }
        return map.toList()
    }

}