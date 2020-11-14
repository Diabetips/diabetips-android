package com.epitech.diabetips.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.epitech.diabetips.R
import java.util.*
import kotlin.collections.ArrayList

class DropdownAdapter(context: Context, arrayResourceId: Int, filterResult: Boolean = false) : ArrayAdapter<String>(context, R.layout.item_dropdown) {

    private val filter: Filter = if (filterResult) BasicFilter() else NoFilter()
    private val items: List<String> = context.resources.getTextArray(arrayResourceId).map { charSequence -> charSequence.toString() }

    init {
        addAll(items)
    }

    fun getItems(): List<String> {
        return items
    }

    override fun getFilter(): Filter {
        return filter
    }

    private inner class NoFilter : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val result = FilterResults()
            result.values = items
            result.count = items.size
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }

    }

    private inner class BasicFilter : Filter() {
        private val suggestions: MutableList<String> = mutableListOf()

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            if (!constraint.isNullOrBlank()) {
                val constraintString = constraint.toString().toLowerCase(Locale.ROOT)
                suggestions.clear()
                for (item in items) {
                    if (item.toLowerCase(Locale.ROOT).contains(constraintString)) {
                        suggestions.add(item)
                    }
                }
                suggestions.sortBy { (!it.toLowerCase(Locale.ROOT).startsWith(constraintString)).toString() + it }
                filterResults.values = suggestions
                filterResults.count = suggestions.size
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val filterList = results?.values as? ArrayList<*>
            if ((filterList?.size ?: 0) > 0) {
                clear()
                for (item in filterList!!) {
                    add(item.toString())
                    notifyDataSetChanged()
                }
            }
        }
    }
}