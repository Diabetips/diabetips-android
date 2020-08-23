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
import com.epitech.diabetips.utils.TimeHandler
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_dashboard.view.*
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

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
        entriesManager.getPage()?.setInterval(
            LocalDate.now(ZoneOffset.UTC).atStartOfDay().minusDays(7).format(DateTimeFormatter.ISO_DATE_TIME),
            LocalDate.now(ZoneOffset.UTC).atStartOfDay().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME))
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
        val newItems = ArrayList(items.sortedByDescending{it.time}.toTypedArray().groupBy {
            TimeHandler.instance.changeTimeFormat(it.time, getString(R.string.format_time_api), getString(R.string.format_date_dashboard))
        }.toList())
        val adapter: DashboardGroupedItemsAdapter? = (view?.itemsList?.adapter as DashboardGroupedItemsAdapter?)
        if (resetPage) adapter?.setItems(newItems) else adapter?.addItems(newItems)
        if (!entriesManager.isLastPage()) {
            return entriesManager.getItems(false)
        }
        view?.itemsSwipeRefresh?.isRefreshing = false
    }

    override fun isLoading(): Boolean {
        return view?.itemsSwipeRefresh?.isRefreshing ?: true
    }
}