package com.epitech.diabetips.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DashboardGroupedItemsAdapter
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.utils.ANavigationFragment
import com.epitech.diabetips.utils.PaginationScrollListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_dashboard.view.*
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

class DashboardFragment : ANavigationFragment(FragmentType.DASHBOARD) {
    private lateinit var entriesManager: EntriesManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = createFragmentView(R.layout.activity_dashboard, inflater, container)
        setupEntriesManager()
        setupItemsList(view)
        getItems(true, view)
        view.closeDashboardButton.setOnClickListener { requireActivity().finish() }
        return view
    }

    private fun setupEntriesManager() {
        entriesManager =
            EntriesManager(context = requireContext()) { items, reset ->
                setItemsInDashBoardAdapter(reset, items)
            }
        entriesManager.deactivate(EntryObject.Type.SUGAR)
        //TimeHandler.instance.currentTimeSecond()
        val end = LocalDate.now(ZoneOffset.UTC).atStartOfDay().plusDays(2)
        val start = LocalDate.now(ZoneOffset.UTC).atStartOfDay().minusDays(7)
        entriesManager.getPage()?.setInterval(
            DateTimeUtils.toSqlTimestamp(start).time / 1000 - 1,
            DateTimeUtils.toSqlTimestamp(end).time / 1000)
        entriesManager.updatePages()
    }

    private fun setupItemsList(view: View) {
        view.itemsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DashboardGroupedItemsAdapter(context, requireActivity().supportFragmentManager)
        }
        view.itemsList.addOnScrollListener(object : PaginationScrollListener(view.itemsList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return entriesManager.isLastPage()
            }

            override fun isLoading(): Boolean {
                return view.itemsSwipeRefresh.isRefreshing
            }

            override fun loadMoreItems() {
                getItems(false)
            }
        })
        view.itemsSwipeRefresh.setOnRefreshListener {
            getItems()
        }
    }

    private fun getItems(resetPage: Boolean = true, view: View? = this.view) {
            view?.itemsSwipeRefresh?.isRefreshing = true
            entriesManager.getItems(resetPage)
    }

    private fun setItemsInDashBoardAdapter(resetPage: Boolean, items: Array<EntryObject>) {
        val newItems = ArrayList(items.sortedByDescending{it.time}.toTypedArray().groupBy { getDateTime(it.time)}.toList())
        val adapter: DashboardGroupedItemsAdapter? = (view?.itemsList?.adapter as DashboardGroupedItemsAdapter?)
        if (resetPage) adapter?.setItems(newItems) else adapter?.addItems(newItems)
        if (!entriesManager.isLastPage()) {
            return entriesManager.getItems(false)
        }
        view?.itemsSwipeRefresh?.isRefreshing = false
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat(resources.getString(R.string.format_date_dashboard))
            val netDate = Date(timestamp * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
    override fun isLoading(): Boolean {
        return view?.itemsSwipeRefresh?.isRefreshing ?: true
    }
}