package com.epitech.diabetips.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.ObjectType
import com.epitech.diabetips.utils.toInt
import kotlinx.android.synthetic.main.grouped_items_dashboard.view.*

class DashboardGroupedItemsViewHolder (var inflater: LayoutInflater, var parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.grouped_items_dashboard, parent, false)) {

    data class ItemInfo (
        var icon: ImageView,
        var quantity: TextView,
        var color: Int,
        var type: ObjectType,
        var activated: Boolean = false,
        var resetDisplay: Boolean = true)

    private var date: String = "OUI"
    private var items: List<EntryObject> = arrayListOf()
    var recyclerView : RecyclerView = itemView.findViewById(R.id.rv_dashboard_expanded_items)
    //var topbar: LinearLayout = itemView.day_dashboard_recap
    var more: ImageView = itemView.imageArrow
    lateinit var topbarItems: List<ItemInfo>

    fun bind(date: String, items: List<EntryObject>) {
        this.date = date
        this.items = items

        itemView.dateGroupedItems.text = date

        topbarItems = listOf(
            ItemInfo(itemView.foodIcon, itemView.foodQuantity, ContextCompat.getColor(inflater.context, R.color.colorPrimary), ObjectType.MEAL),
            ItemInfo(itemView.insulinIcon, itemView.insulinQuantity, ContextCompat.getColor(inflater.context, R.color.colorAccent), ObjectType.INSULIN_FAST),
            ItemInfo(itemView.insulinIcon, itemView.insulinQuantity, ContextCompat.getColor(inflater.context, R.color.colorAccent), ObjectType.INSULIN_SLOW, resetDisplay = false),
            ItemInfo(itemView.activityIcon, itemView.activityQuantity, ContextCompat.getColor(inflater.context, R.color.colorGreen), ObjectType.ACTIVITY),
            ItemInfo(itemView.commentIcon, itemView.commentQuantity, MaterialHandler.getColorFromAttribute(inflater.context, R.attr.colorComment), ObjectType.NOTE)
        )
        topbarItems.forEach {
            setupIcon(it)
        }
    }

    private fun setupIcon(item: ItemInfo) {
        var count = items.count { it.type == item.type }
        if (!item.resetDisplay) {
            count += (item.quantity.text.toString().toIntOrNull() ?: 0)
        }
        item.quantity.text = count.toString()
        item.activated = (count > 0)
        setItemVisibility(item, item.activated)
        if (item.activated)
            item.icon.drawable.setTint(item.color)
    }

    private fun setItemVisibility(item: ItemInfo, show: Boolean) {
        if (!item.activated || !show) {
            item.quantity.visibility = View.GONE
            item.icon.visibility = View.GONE
        } else {
            item.quantity.visibility = View.VISIBLE
            item.icon.visibility = View.VISIBLE
        }
    }

    fun changeState(expand: Boolean) {
        more.rotation = 180f * expand.toInt()
        topbarItems.forEach { item ->
            setItemVisibility(item, !expand)
        }
    }
}

