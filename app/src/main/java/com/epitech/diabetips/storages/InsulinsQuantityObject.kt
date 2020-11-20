package com.epitech.diabetips.storages

import java.io.Serializable

data class InsulinsQuantityObject(var slow: Float? = null,
                                  var fast: Float? = null,
                                  var very_fast: Float? = null) : Serializable