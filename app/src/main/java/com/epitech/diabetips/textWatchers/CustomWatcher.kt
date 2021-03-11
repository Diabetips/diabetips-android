package com.epitech.diabetips.textWatchers

import android.text.TextWatcher
import android.text.Editable
import com.google.android.material.textfield.TextInputLayout

class CustomWatcher(private val inputLayout: TextInputLayout,
                         private val callback: ((String) -> String?)) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        inputLayout.error = callback.invoke(s.toString())
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
}
