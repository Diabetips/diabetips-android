package com.epitech.diabetips.textWatchers

import android.text.TextWatcher
import android.text.Editable

open class TextChangedWatcher(private val onTextChangedListener : ((Editable?) -> Unit)) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        onTextChangedListener(s)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
}
