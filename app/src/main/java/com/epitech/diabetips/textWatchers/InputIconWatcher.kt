package com.epitech.diabetips.textWatchers

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.google.android.material.textfield.TextInputLayout

open class InputIconWatcher(context: Context?,
                       inputLayout: TextInputLayout,
                       emptyCheck: Boolean = false,
                       emptyErrorId: Int = R.string.empty_field_error) : InputWatcher(context, inputLayout, emptyCheck, emptyErrorId) {

    private var iconColorStateList: ColorStateList? = null
    private var errorColorStateList: ColorStateList? = null

    init {
        if (context != null) {
            iconColorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorHint))
            errorColorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWarm))
        }
    }

    override fun setError(errorId: Int?) {
        super.setError(errorId)
        inputLayout.apply {
            setEndIconTintList(if (error == null) iconColorStateList else errorColorStateList)
        }
    }
}
