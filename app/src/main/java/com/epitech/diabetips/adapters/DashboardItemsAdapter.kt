package com.epitech.diabetips.adapters
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.NewMealActivity
import com.epitech.diabetips.holders.DashboardItemViewHolder
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.storages.EntryObject.Type.*
import com.epitech.diabetips.storages.MealObject

class DashboardItemsAdapter(val context: Context,
                            private val items: ArrayList<EntryObject> = arrayListOf(),
                            private val onItemClickListener : ((EntryObject) -> Unit)? = null)
        : RecyclerView.Adapter<DashboardItemViewHolder>() {

        var mitems = items;

        fun setItems(itemList: Array<EntryObject>) {
                items.clear()
                items.addAll(itemList)
                notifyDataSetChanged()
        }

        fun addItem(item: EntryObject) {
                items.add(item)
                notifyItemInserted(items.size)
        }

        fun addItems(itemList: Array<EntryObject>) {
                items.addAll(itemList)
                notifyItemRangeInserted(items.size - itemList.size, itemList.size)
        }

        fun updateItem(item: EntryObject) {
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
                        when(items[position].type) {
                                MEAL -> launchMeal(items[position])
                                else -> doNothing()
                        }
                }
        }

        private fun launchMeal(item: EntryObject) {
                val intent = Intent(context, NewMealActivity::class.java)
                        .putExtra(
                                context.getString(R.string.param_meal),
                                item.orignal as MealObject
                        )
                context.startActivity(intent)
        }

        private fun doNothing() {

        }

}