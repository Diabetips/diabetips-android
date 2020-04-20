package com.epitech.diabetips.holders

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.DashboardItemObject
import kotlinx.android.synthetic.main.grouped_items_dashboard.view.*


class DashboardGroupedItemViewHolder (var inflater: LayoutInflater, var parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.grouped_items_dashboard, parent, false)) {

    private var date: String = "OUI";
    private var items: List<DashboardItemObject> = arrayListOf();
    var recyclerView : RecyclerView = itemView.findViewById(R.id.rv_dashboard_expanded_items)
    var topbar: LinearLayout = itemView.day_dashboard_recap
    var more: ImageView = itemView.imageView2
    lateinit var topbarItems: List<ItemInfo>

    fun bind(date: String, items: List<DashboardItemObject>) {
        this.date = date
        this.items = items

        itemView.dateGroupedItems.text = date

        topbarItems = listOf(
            ItemInfo(itemView.foodIcon, itemView.foodQuantity, R.color.colorPrimary, DashboardItemObject.Type.MEAL),
            ItemInfo(itemView.insulinIcon, itemView.insulinQuantity, R.color.colorAccent, DashboardItemObject.Type.INSULIN_FAST),
            ItemInfo(itemView.commentIcon, itemView.commentQuantity, R.color.searchBarSearchIconTintColor, DashboardItemObject.Type.COMMENT)
        )
        topbarItems.forEach() {
            setupIcon(it)
        }
    }

    private fun setupIcon(item: ItemInfo) {
        val count = items.count { it.type == item.type }
        item.quantity.text = count.toString()
        item.activated = (count > 0)
        setItemVisibility(item, item.activated)
        if (item.activated)
            setColorFilter(item.icon.drawable, item.color)
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        drawable.setColorFilter(ContextCompat.getColor(inflater.context, color), PorterDuff.Mode.SRC_ATOP)
        return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            drawable.setColorFilter(PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP))
        }
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
        topbarItems.forEach() {item ->
            setItemVisibility(item, !expand)
//            if (expand)
//                topbar.setBackgroundColor(inflater.context.resources.getColor(R.color.colorBackgroundCard))
//            else
//                topbar.setBackgroundColor(inflater.context.resources.getColor(R.color.colorBackground))
        }
    }

    data class ItemInfo (
        var icon: ImageView,
        var quantity: TextView,
        var color: Int,
        var type: DashboardItemObject.Type,
        var activated: Boolean = true)
}

