package com.epitech.diabetips.adapters
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.holders.DashboardGroupedItemViewHolder
import com.epitech.diabetips.storages.DashboardItemObject
import java.util.*

var animationPlaybackSpeed: Double = 0.8

class DashboardItemAdapter(val context: Context,
                           private var items: ArrayList<Pair<String?, List<DashboardItemObject>>> = arrayListOf(),
                           private val onItemClickListener : ((DashboardItemObject) -> Unit)? = null)
        : RecyclerView.Adapter<DashboardGroupedItemViewHolder>() {


        private val viewPool = RecyclerView.RecycledViewPool()

        private lateinit var recyclerView: RecyclerView
        private var expandedModel: Pair<String?, List<DashboardItemObject>>? = null

        fun setItems(itemList: ArrayList<Pair<String?, List<DashboardItemObject>>>) {
                items.clear()
                items.addAll(itemList)
                notifyDataSetChanged()
        }

        fun addItem(item: Pair<String?, List<DashboardItemObject>>) {
                items. add(item)
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

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                this.recyclerView = recyclerView
        }

        override fun onBindViewHolder(holder: DashboardGroupedItemViewHolder, position: Int) {
                val model = items[position]

                holder.bind(items[position].first as String, items[position].second)
                val parent = items[position]

                val childLayoutManager = LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
                val arrayItems = ArrayList(parent.second)
                childLayoutManager.initialPrefetchItemCount = arrayItems.size
                holder.recyclerView.apply {
                        layoutManager = childLayoutManager
                        adapter = DashboardItem2Adapter(context, arrayItems, null)
                        setRecycledViewPool(viewPool)
                }

                expandItem(holder, model == expandedModel, animate = false)

                holder.more.setOnClickListener {
                        if (expandedModel == null) {

                                // expand clicked view
                                expandItem(holder, expand = true, animate = true)
                                expandedModel = model
                        } else if (expandedModel == model) {

                                // collapse clicked view
                                expandItem(holder, expand = false, animate = true)
                                expandedModel = null
                        } else {

                                // collapse previously expanded view
                                val expandedModelPosition = items.indexOf(expandedModel!!)
                                val oldViewHolder =
                                        recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? DashboardGroupedItemViewHolder
                                if (oldViewHolder != null) expandItem(oldViewHolder, expand = false, animate = true)

                                // expand clicked view
                                expandItem(holder, expand = true, animate = true)
                                expandedModel = model
                        }
                }

        }

        private fun expandItem(holder: DashboardGroupedItemViewHolder, expand: Boolean, animate: Boolean) {
                holder.recyclerView.isVisible = expand
                holder.more.rotation *= 180
                holder.changeState(expand)
        }
}