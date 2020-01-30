package com.epitech.diabetips.storages

import java.io.Serializable

data class TokenObject (
    var access_token: String = "",
    var token_type: String = "",
    var expires_in: Int = 0,
    var refresh_token: String = "") : Serializable