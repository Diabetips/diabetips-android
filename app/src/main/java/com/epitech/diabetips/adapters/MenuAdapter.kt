package com.epitech.diabetips.adapters

import android.content.Context
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.iterator
import com.epitech.diabetips.holders.MenuItemViewHolder

class MenuAdapter(menuId: Int,
                  context: Context,
                  private val onItemClickListener : ((MenuItem) -> Unit)? = null)
    : AVisibilityAdapter<MenuItemViewHolder>() {

    private val menu: Menu

    init {
        val popupMenu = PopupMenu(context, null)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)
        menu = popupMenu.menu

        val menuIterator = menu.iterator()
        menuIterator.forEach { item ->
            if (!item.isVisible) {
                menu.removeItem(item.itemId)
                menuIterator.remove()
            }
        }
        popupMenu.dismiss()
    }

    override fun getItemCount(): Int = menu.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MenuItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(menu.getItem(position), onItemClickListener)
    }

}

