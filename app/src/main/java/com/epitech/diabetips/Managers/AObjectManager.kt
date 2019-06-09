package com.epitech.diabetips.Managers

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type

abstract class AObjectManager<T: Any>(key: String) : AManager("saved_objects", key)  {

    fun saveObject(context: Context, aobject: T) {
        saveString(context, Gson().toJson(aobject))
    }

    protected fun getObject(context: Context, type: Type): T {
        return Gson().fromJson(getString(context), type)
    }

}