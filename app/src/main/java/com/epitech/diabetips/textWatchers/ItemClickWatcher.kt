package com.epitech.diabetips.textWatchers

import android.view.View
import android.widget.AdapterView

open class ItemClickWatcher(private val values: List<String>,
                            private val onItemChanged : ((String) -> Unit)) : AdapterView.OnItemClickListener {

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemChanged.invoke(values.getOrNull(position) ?: "")
    }
}