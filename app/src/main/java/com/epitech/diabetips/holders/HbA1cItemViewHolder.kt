package com.epitech.diabetips.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.HbA1cObject
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.item_hba1c.view.*

class HbA1cItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_hba1c, parent, false)) {
    private var hbA1cText: TextView? = null
    private var hbA1cDate: TextView? = null
    private var hbA1cImage: ImageView? = null
    private var context: Context

    init {
        hbA1cText = itemView.hba1cText
        hbA1cDate = itemView.hba1cDate
        hbA1cImage = itemView.hba1cImage
        context = inflater.context
    }

    fun bind(hbA1c: HbA1cObject, onItemClickListener : ((HbA1cObject) -> Unit)? = null) {
        hbA1cText?.text = "${hbA1c.value}%"
        hbA1cDate?.text = TimeHandler.instance.changeTimeFormat(hbA1c.time, context.getString(R.string.format_time_api), context.getString(R.string.format_date_dashboard))
        when(hbA1c.value) {
            in Float.MIN_VALUE..6.5f -> hbA1cImage?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle).also { it?.setTint(ContextCompat.getColor(context, R.color.colorPrimary)) })
            in 7.5f..Float.MAX_VALUE -> hbA1cImage?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle).also { it?.setTint(ContextCompat.getColor(context, R.color.colorWarm)) })
            else -> hbA1cImage?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle).also { it?.setTint(ContextCompat.getColor(context, R.color.colorAccent)) })
        }
        itemView.setOnClickListener {onItemClickListener?.invoke(hbA1c)}
    }
}

