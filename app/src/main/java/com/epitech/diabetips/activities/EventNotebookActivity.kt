package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DashboardGroupedItemsAdapter
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.utils.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_event_notebook.*
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

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
                setItemsInDashBoardAdapter(items, reset)
            }
        entriesManager.deactivate(ObjectType.SUGAR)
        val now = TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api))
        entriesManager.getPage()?.setInterval(
            this,
            TimeHandler.instance.addTimeToFormat(now, getString(R.string.format_time_api), -7, Calendar.DAY_OF_YEAR),
            TimeHandler.instance.addTimeToFormat(now, getString(R.string.format_time_api), 2, Calendar.DAY_OF_YEAR))
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

    private fun setItemsInDashBoardAdapter(items: Array<EntryObject>, resetPage: Boolean) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.UPDATE_MEAL.ordinal) {
            getItems()
        }
    }
}
