package com.epitech.diabetips.storages

import java.io.Serializable

data class FileObject (
    var filename: String = "",
    var size: Int = 0) : Serializable