package com.epitech.diabetips.managers

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type

abstract class AObjectManager<T: Any>(key: String) : AManager("saved_objects", key)  {

    protected fun<T: Any> saveObject(context: Context, objectToSave: T, prefKey: String = key) {
        saveString(context, Gson().toJson(objectToSave), prefKey)
    }

    protected fun<T: Any> getObject(context: Context, type: Type, prefKey: String = key): T? {
        val obj: String = getString(context, prefKey)
        return if (obj.isNotBlank()) Gson().fromJson(obj, type) else null
    }

}