package com.epitech.diabetips.holders

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.DashboardItemObject
import kotlinx.android.synthetic.main.grouped_items_dashboard.view.*

class DashboardGroupedItemViewHolder (inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.grouped_items_dashboard, parent, false)) {

    private var date: String = "OUI";
    private var items: List<DashboardItemObject> = arrayListOf();
    var recyclerView : RecyclerView = itemView.findViewById(R.id.rv_dashboard_expanded_items)
    var cardContainer : View = itemView.findViewById(R.id.card_container)
    lateinit var topbar: LinearLayout
    lateinit var more: ImageView

    fun bind(date: String, items: List<DashboardItemObject>, onItemClickListener : ((DashboardItemObject) -> Unit)? = null) {
        this.date = date
        this.topbar = itemView.day_dashboard_recap
        this.more = itemView.imageView2
        itemView.dateGroupedItems.text = date
        this.items = items
        var count = items.count { it.title == "Repas" }
        itemView.foodIcon.drawable.setColorFilter( ContextCompat.getColor(itemView.context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
        itemView.foodQuantity.text = count.toString()
        if (count == 0) {
            itemView.foodQuantity.visibility = View.GONE
            itemView.foodIcon.visibility = View.GONE
        }
        count = items.count { it.title.startsWith("Insulin") }
        itemView.insulinIcon.drawable.setColorFilter( ContextCompat.getColor(itemView.context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
        itemView.insulinQuantity.text = count.toString()
        if (count == 0) {
            itemView.insulinIcon.visibility = View.GONE
            itemView.insulinQuantity.visibility = View.GONE
        }

//        itemView.setOnClickListener {onItemClickListener?.invoke(ite)}
    }
}

