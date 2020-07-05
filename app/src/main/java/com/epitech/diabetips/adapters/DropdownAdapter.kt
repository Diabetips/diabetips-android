package com.epitech.diabetips.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.epitech.diabetips.R

class DropdownAdapter(context: Context, arrayResourceId: Int) : ArrayAdapter<String>(context, R.layout.item_dropdown) {

    private val filter: Filter = NoFilter()
    var items: List<String> = context.resources.getTextArray(arrayResourceId)
        .map { charSequence -> charSequence.toString() }

    init {
        addAll(items)
    }

    override fun getFilter(): Filter {
        return filter
    }

    private inner class NoFilter : Filter() {

        override fun performFiltering(arg0: CharSequence?): FilterResults {
            val result = FilterResults()
            result.values = items
            result.count = items.size
            return result
        }

        override fun publishResults(arg0: CharSequence?, arg1: FilterResults?) {
            notifyDataSetChanged()
        }

    }
}