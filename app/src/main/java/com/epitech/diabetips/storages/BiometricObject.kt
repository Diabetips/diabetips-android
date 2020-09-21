package com.epitech.diabetips.storages

import android.content.Context
import com.epitech.diabetips.R
import java.io.Serializable

data class BiometricObject (
    var mass: Int? = null,
    var height: Int? = null,
    var hypoglycemia: Int? = null,
    var hyperglycemia: Int? = null,
    var date_of_birth: String? = null,
    var sex: String? = null,
    var diabetes_type: String? = null) : Serializable {

    fun getDiabetesType(context: Context) : String {
        return getStringFromResources(diabetes_type,
            context.resources.getStringArray(R.array.diabetes_type_api),
            context.resources.getStringArray(R.array.diabetes_type)) ?: ""
    }

    fun getSex(context: Context) : String {
        return getStringFromResources(sex,
            context.resources.getStringArray(R.array.sex_api),
            context.resources.getStringArray(R.array.sex)) ?: ""
    }

    fun setDiabetesType(context: Context, diabetesType: String?) {
        diabetes_type = getStringFromResources(diabetesType,
            context.resources.getStringArray(R.array.diabetes_type),
            context.resources.getStringArray(R.array.diabetes_type_api))
    }

    fun setSex(context: Context, sexType: String?) {
        sex = getStringFromResources(sexType,
            context.resources.getStringArray(R.array.sex),
            context.resources.getStringArray(R.array.sex_api))
    }

    private fun getStringFromResources(string: String?, stringArrayFrom: Array<String>, stringArrayTo: Array<String>) : String? {
        if (string == null)
            return null
        val index = stringArrayFrom.indexOf(string)
        if (index < 0 || index >= stringArrayTo.size)
            return null
        return stringArrayTo[index]
    }


}