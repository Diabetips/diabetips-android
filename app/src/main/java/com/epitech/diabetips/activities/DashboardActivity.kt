package com.epitech.diabetips.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DashboardGroupedItemsAdapter
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.epitech.diabetips.utils.TimeHandler
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

class DashboardActivity : AppCompatActivity() {
    private lateinit var entriesManager: EntriesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setupView()
        setupEntriesManager()
        setupItemsList()
        getItems()
        closeDashboardButton.setOnClickListener { finish() }

        super.onCreate(savedInstanceState)
    }

    fun setupEntriesManager() {
        entriesManager =
            EntriesManager(context = this) { items, reset ->
                setItemsInDashBoardAdapter(reset, items)
            }
        entriesManager.deactivate(EntryObject.Type.SUGAR)
        TimeHandler.instance.currentTimeSecond()
        val end = LocalDate.now(ZoneOffset.UTC).atStartOfDay().plusDays(2)
        val start = LocalDate.now(ZoneOffset.UTC).atStartOfDay().minusDays(7)
        entriesManager.getPage()?.setInterval(
            DateTimeUtils.toSqlTimestamp(start).time / 1000 - 1,
            DateTimeUtils.toSqlTimestamp(end).time / 1000)
        entriesManager.updatePages()
    }

    private fun setupView()
    {
        AppCompatDelegate.setDefaultNightMode(ModeManager.instance.getDarkMode(this))
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_dashboard)
    }

    private fun setupItemsList()
    {
        itemsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DashboardGroupedItemsAdapter(context)
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

    private fun getItems(resetPage: Boolean = true)
    {
        if (!itemsSwipeRefresh.isRefreshing) {
            itemsSwipeRefresh.isRefreshing = true;
            entriesManager.getItems(resetPage)
        }
    }

    private fun setItemsInDashBoardAdapter(resetPage: Boolean, items: Array<EntryObject>) {
        val newItems = ArrayList(items.sortedByDescending{it.time}.toTypedArray().groupBy { getDateTime(it.time)}.toList())
        val adapter: DashboardGroupedItemsAdapter = (itemsList?.adapter as DashboardGroupedItemsAdapter)
        if (resetPage) adapter.setItems(newItems) else adapter.addItems(newItems)
        if (!entriesManager.isLastPage()) {
            return entriesManager.getItems(false)
        }
        itemsSwipeRefresh.isRefreshing = false
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
}