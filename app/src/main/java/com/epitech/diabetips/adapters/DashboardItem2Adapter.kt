package com.epitech.diabetips.adapters
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.NewMealActivity
import com.epitech.diabetips.holders.DashboardItemViewHolder
import com.epitech.diabetips.storages.DashboardItemObject
import com.epitech.diabetips.storages.MealObject

class DashboardItem2Adapter(val context: Context,
                            private val items: ArrayList<DashboardItemObject> = arrayListOf(),
                            private val onItemClickListener : ((DashboardItemObject) -> Unit)? = null)
        : RecyclerView.Adapter<DashboardItemViewHolder>() {

        var mitems = items;

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

                holder.itemView.setOnClickListener {
                        val intent: Intent
                        intent = Intent(context, NewMealActivity::class.java)
                                .putExtra(context.getString(R.string.param_meal), items[position].orignal as MealObject)
                        context.startActivity(intent)
                }
        }

}