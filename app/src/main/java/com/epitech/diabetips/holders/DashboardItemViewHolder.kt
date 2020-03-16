package com.epitech.diabetips.holders

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.DashboardItemObject
import com.epitech.diabetips.storages.MealObject
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

    fun bind(item: DashboardItemObject, onItemClickListener : ((DashboardItemObject) -> Unit)? = null) {
        icon?.setImageDrawable(item.icon)
        title?.text = item.title
        description?.text = item.description
        time?.text = SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(item.time * 1000)
//        mealDescription?.text = meal.description
//        mealDate?.text = SimpleDateFormat("HH:mm dd/MM/yyyy", java.util.Locale.getDefault()).format(meal.timestamp)
//        mealUnit?.text = "0u"
        itemView.setOnClickListener {onItemClickListener?.invoke(item)}
    }
}

