package com.epitech.diabetips.holders

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_menu, parent, false)) {
    private var menuText: TextView? = null
    private var menuIcon: ImageView? = null

    init {
        menuText = itemView.menuText
        menuIcon = itemView.menuIcon
    }

    fun bind(menuItem: MenuItem, onItemClickListener : ((MenuItem) -> Unit)? = null) {
        menuText?.text = menuItem.title
        menuIcon?.setImageDrawable(menuItem.icon)
        itemView.setOnClickListener {
            onItemClickListener?.invoke(menuItem)
        }
    }
}

