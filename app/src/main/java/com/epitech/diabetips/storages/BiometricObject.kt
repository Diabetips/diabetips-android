package com.epitech.diabetips.storages

import java.io.Serializable

data class BiometricObject (
    var date_of_birth: String = "",
    var weight: Int? = null,
    var height: Int? = null,
    var sex: String = "") : Serializable {

    enum class Sex {m, f}

}