package com.epitech.diabetips.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.item_dashboard.view.*
import java.text.SimpleDateFormat

class DashboardItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_dashboard, parent, false)) {
    private var icon: ImageView? = null
    private var title: TextView? = null
    private var description: TextView? = null
    private var time: TextView? = null

    init {
        icon = itemView.dashboardItemIcon
        title = itemView.dashboardItemTitle
        description = itemView.dashboardItemDescription
        time = itemView.dashboardItemTime
    }

    fun bind(context: Context, item: EntryObject, onItemClickListener : ((EntryObject) -> Unit)? = null) {
        icon?.setImageDrawable(item.icon)
        title?.text = item.title
        description?.text = item.description
        TimeHandler.instance.updateTimeDisplay(context, item.time, null, time)
        itemView.setOnClickListener {onItemClickListener?.invoke(item)}
    }
}

