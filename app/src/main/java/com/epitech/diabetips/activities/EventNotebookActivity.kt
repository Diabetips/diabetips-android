package com.epitech.diabetips.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DashboardGroupedItemsAdapter
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.ObjectType
import com.epitech.diabetips.utils.PaginationScrollListener
import com.epitech.diabetips.utils.TimeHandler
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_event_notebook.*
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

class EventNotebookActivity : ADiabetipsActivity(R.layout.activity_event_notebook) {
    private lateinit var entriesManager: EntriesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEntriesManager()
        setupItemsList()
        getItems(true)
        closeEventNotebookButton.setOnClickListener { finish() }
    }

    private fun setupEntriesManager() {
        entriesManager = EntriesManager(context = this) { items, reset ->
                setItemsInDashBoardAdapter(reset, items)
            }
        entriesManager.deactivate(ObjectType.SUGAR)
        entriesManager.getPage()?.setInterval(
            LocalDate.now(ZoneOffset.UTC).atStartOfDay().minusDays(7).format(DateTimeFormatter.ISO_DATE_TIME),
            LocalDate.now(ZoneOffset.UTC).atStartOfDay().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME))
        entriesManager.updatePages()
    }

    private fun setupItemsList() {
        itemsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DashboardGroupedItemsAdapter(context, supportFragmentManager)
        }
        itemsList.addOnScrollListener(object : PaginationScrollListener(itemsList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return entriesManager.isLastPage()
            }

            override fun isLoading(): Boolean {
                return itemsSwipeRefresh.isRefreshing
            }

            override fun loadMoreItems() {
                getItems(false)
            }
        })
        itemsSwipeRefresh.setOnRefreshListener {
            getItems()
        }
    }

    private fun getItems(resetPage: Boolean = true) {
        itemsSwipeRefresh.isRefreshing = true
        entriesManager.getItems(resetPage)
    }

    private fun setItemsInDashBoardAdapter(resetPage: Boolean, items: Array<EntryObject>) {
        val newItems = ArrayList(items.sortedByDescending{it.time}.toTypedArray().groupBy {
            TimeHandler.instance.changeTimeFormat(it.time, getString(R.string.format_time_api), getString(R.string.format_date_dashboard))
        }.toList())
        val adapter: DashboardGroupedItemsAdapter = (itemsList.adapter as DashboardGroupedItemsAdapter)
        if (resetPage) adapter.setItems(newItems) else adapter.addItems(newItems)
        if (!entriesManager.isLastPage()) {
            return entriesManager.getItems(false)
        }
        itemsSwipeRefresh.isRefreshing = false
    }
}
