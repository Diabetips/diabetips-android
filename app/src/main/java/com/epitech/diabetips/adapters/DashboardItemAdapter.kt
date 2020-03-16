package com.epitech.diabetips.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.holders.DashboardItemViewHolder
import com.epitech.diabetips.storages.DashboardItemObject

class DashboardItemAdapter(private val items: ArrayList<DashboardItemObject> = arrayListOf(),
                           private val onItemClickListener : ((DashboardItemObject) -> Unit)? = null)
        : RecyclerView.Adapter<DashboardItemViewHolder>() {

        fun setItems(itemList: Array<DashboardItemObject>) {
                items.clear()
                items.addAll(itemList)
                notifyDataSetChanged()
        }

        fun addItem(item: DashboardItemObject) {
                items.add(item)
                notifyItemInserted(items.size)
        }

        fun addItems(itemList: Array<DashboardItemObject>) {
                items.addAll(itemList)
                notifyItemRangeInserted(items.size - itemList.size, itemList.size)
        }

        fun updateItem(item: DashboardItemObject) {
                items.forEachIndexed { index, DashboardItemObject ->
                        if (DashboardItemObject.id == item.id) {
                                items[index] = item
                                notifyItemChanged(index)
                                return
                        }
                }
        }

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return DashboardItemViewHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
                holder.bind(items[position], onItemClickListener)
        }

}
