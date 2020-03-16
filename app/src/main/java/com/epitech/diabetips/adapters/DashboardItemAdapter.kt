package com.epitech.diabetips.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.holders.DashboardGroupedItemViewHolder
import com.epitech.diabetips.storages.DashboardItemObject
import java.util.*

class DashboardItemAdapter(private var items: ArrayList<Pair<String?, List<DashboardItemObject>>> = arrayListOf(),
                           private val onItemClickListener : ((DashboardItemObject) -> Unit)? = null)
        : RecyclerView.Adapter<DashboardGroupedItemViewHolder>() {

        fun setItems(itemList: ArrayList<Pair<String?, List<DashboardItemObject>>>) {
                items.clear()
                items.addAll(itemList)
                notifyDataSetChanged()
        }

        fun addItem(item: DashboardItemObject) {
//                items. add(item)
                notifyItemInserted(items.size)
        }

        fun addItems(itemList: ArrayList<Pair<String?, List<DashboardItemObject>>>) {
                items.addAll(itemList)
                notifyItemRangeInserted(items.size - itemList.size, itemList.size)
        }

        fun updateItem(item: Pair<String?, List<DashboardItemObject>>) {
                items.forEachIndexed { index, DashboardItemObject ->
                        if (DashboardItemObject.first == item.first) {
                                items[index] = item
                                notifyItemChanged(index)
                                return
                        }
                }
        }

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardGroupedItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return DashboardGroupedItemViewHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: DashboardGroupedItemViewHolder, position: Int) {
                holder.bind(items[position].first as String, items[position].second, onItemClickListener)
        }

}
