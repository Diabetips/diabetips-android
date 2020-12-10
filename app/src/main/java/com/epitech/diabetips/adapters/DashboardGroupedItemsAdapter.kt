package com.epitech.diabetips.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.holders.DashboardGroupedItemsViewHolder
import com.epitech.diabetips.storages.EntryObject
import java.util.*

//var animationPlaybackSpeed: Double = 0.8

class DashboardGroupedItemsAdapter(
        val context: Context,
        private val fragmentManager: FragmentManager,
        private var items: ArrayList<Pair<String?, List<EntryObject>>> = arrayListOf())
    : RecyclerView.Adapter<DashboardGroupedItemsViewHolder>() {


    private val viewPool = RecyclerView.RecycledViewPool()

    private lateinit var recyclerView: RecyclerView
    private var expandedModel: Pair<String?, List<EntryObject>>? = null

    fun setItems(itemList: ArrayList<Pair<String?, List<EntryObject>>>) {
        items.clear()
        items.addAll(itemList)
        notifyDataSetChanged()
    }

    fun addItem(item: Pair<String?, List<EntryObject>>) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun addItems(itemList: ArrayList<Pair<String?, List<EntryObject>>>) {
        items.addAll(itemList)
        notifyItemRangeInserted(items.size - itemList.size, itemList.size)
    }

    fun updateItem(item: Pair<String?, List<EntryObject>>) {
        items.forEachIndexed { index, DashboardItemObject ->
            if (DashboardItemObject.first == item.first) {
                items[index] = item
                notifyItemChanged(index)
                return
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardGroupedItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DashboardGroupedItemsViewHolder(inflater, parent)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: DashboardGroupedItemsViewHolder, position: Int) {
        val model = items[position]

        holder.bind(items[position].first as String, items[position].second)
        val parent = items[position]

        val childLayoutManager = LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
        val arrayItems = ArrayList(parent.second)
        childLayoutManager.initialPrefetchItemCount = arrayItems.size
        holder.recyclerView.apply {
            layoutManager = childLayoutManager
            adapter = DashboardItemsAdapter(context, fragmentManager, arrayItems, null)
            setRecycledViewPool(viewPool)
        }

        expandItem(holder, expand = false)

        holder.more.setOnClickListener {
            if (expandedModel == null) {
                // expand clicked view
                expandItem(holder, expand = true)
                expandedModel = model
            } else if (expandedModel == model) {
                // collapse clicked view
                expandItem(holder, expand = false)
                expandedModel = null
            } else {
                // collapse previously expanded view
                val expandedModelPosition = items.indexOf(expandedModel!!)
                val oldViewHolder = recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? DashboardGroupedItemsViewHolder
                if (oldViewHolder != null) {
                        expandItem(oldViewHolder, expand = false)
                }
                // expand clicked view
                expandItem(holder, expand = true)
                expandedModel = model
            }
        }

    }

    private fun expandItem(holder: DashboardGroupedItemsViewHolder, expand: Boolean) {
        holder.recyclerView.isVisible = expand
        holder.changeState(expand)
    }
}