package com.epitech.diabetips.managers

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type

abstract class AObjectManager<T: Any>(key: String) : AManager("saved_objects", key)  {

    protected fun saveObject(context: Context, objectToSave: T) {
        saveString(context, Gson().toJson(objectToSave))
    }

    protected fun getObject(context: Context, type: Type): T? {
        val obj: String = getString(context)
        return if (obj.isNotBlank()) Gson().fromJson(obj, type) else null
    }

}