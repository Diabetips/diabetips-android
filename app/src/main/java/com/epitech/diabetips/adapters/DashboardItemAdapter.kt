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
//      private val listItemExpandDuration: Long get() = (300L / animationPlaybackSpeed).toLong()
//      private val inflater: LayoutInflater = LayoutInflater.from(context)

        private lateinit var recyclerView: RecyclerView
        private var expandedModel: Pair<String?, List<DashboardItemObject>>? = null
        private var isScaledDown = false
        private var originalHeight = -1 // will be calculated dynamically
        private var expandedHeight = -1 // will be calculated dynamically

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

                holder.bind(items[position].first as String, items[position].second, onItemClickListener)
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
                scaleDownItem(holder, position, isScaledDown)

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
//                if (animate) {
//                        val animator = getValueAnimator(
//                                expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
//                        ) { progress -> setExpandProgress(holder, progress) }
//
//                        if (expand) animator.doOnStart { holder.recyclerView.isVisible = true }
//                        else animator.doOnEnd { holder.recyclerView.isVisible = false }
//
//                        animator.start()
//                } else {
//
//                        // show expandView only if we have expandedHeight (onViewAttached)
//                        holder.recyclerView.isVisible = expand && expandedHeight >= 0
//                        setExpandProgress(holder, if (expand) 1f else 0f)
//                }
        }

        override fun onViewAttachedToWindow(holder: DashboardGroupedItemViewHolder) {
                super.onViewAttachedToWindow(holder)

//                // get originalHeight & expandedHeight if not gotten before
//                if (expandedHeight < 0) {
//                        expandedHeight = 0 // so that this block is only called once
//
//                        holder.cardContainer.doOnLayout { view ->
//                                originalHeight = view.height
//                                holder.recyclerView.isVisible = true
//                                Log.i("INIT HEIGHT :", originalHeight.toString())
//                                view.doOnPreDraw {
//                                        expandedHeight = 145
//                                        Log.i("INIT PREDRAW :", originalHeight.toString())
//                                        holder.recyclerView.isVisible = false
//                                }
//                        }
//                }
        }

        private fun scaleDownItem(holder: DashboardGroupedItemViewHolder, position: Int, isScaleDown: Boolean) {
//                setScaleDownProgress(holder, position, if (isScaleDown) 1f else 0f)
        }

        ///////////////////////////////////////////////////////////////////////////
        // Scale Down Animation
        ///////////////////////////////////////////////////////////////////////////

        private fun setExpandProgress(holder: DashboardGroupedItemViewHolder, progress: Float) {
                if (expandedHeight > 0 && originalHeight > 0) {
                        holder.cardContainer.layoutParams.height =
                                (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
                        Log.i("HEIGHT 2:", holder.cardContainer.layoutParams.height.toString())
                }

                holder.cardContainer.requestLayout()

                holder.more.rotation = 180 * progress
        }

        private fun setScaleDownProgress(holder: DashboardGroupedItemViewHolder, position: Int, progress: Float) {
                val itemExpanded = position >= 0 && items[position] == expandedModel
                holder.cardContainer.layoutParams.apply {
                        height = ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()
                        Log.i("HEIGHT :", height.toString())
                }
                holder.cardContainer.requestLayout()
        }

//        private fun expandItem(holder: DashboardGroupedItemViewHolder, expand: Boolean, animate: Boolean) {
//                if (expand) {
//                        holder.recyclerView.layoutParams.height = 200
//                        holder.recyclerView.requestLayout()
//                } else {
//                        holder.recyclerView.layoutParams.height = 0
//                        holder.recyclerView.requestLayout()
//                }
//        }


}

inline fun getValueAnimator(
        forward: Boolean = true,
        duration: Long,
        interpolator: TimeInterpolator,
        crossinline updateListener: (progress: Float) -> Unit
): ValueAnimator {
        val a =
                if (forward) ValueAnimator.ofFloat(0f, 1f)
                else ValueAnimator.ofFloat(1f, 0f)
        a.addUpdateListener { updateListener(it.animatedValue as Float) }
        a.duration = duration
        a.interpolator = interpolator
        return a
}