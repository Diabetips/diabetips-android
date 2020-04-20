package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DashboardItem2Adapter
import com.epitech.diabetips.adapters.DashboardItemAdapter
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.services.DashboardItemsService
import com.epitech.diabetips.services.FuelResponse
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.storages.DashboardItemObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.PaginationScrollListener
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class DashboardActivity : AppCompatActivity() {
    private lateinit var dashboardItemService: DashboardItemsService

    override fun onCreate(savedInstanceState: Bundle?) {
        dashboardItemService = DashboardItemsService(context=this){
                items, reset -> setItemsInDashBoardAdapter(reset, items)
        }
        AppCompatDelegate.setDefaultNightMode(ModeManager.instance.getDarkMode(this))
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        itemsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DashboardItemAdapter(context)
        }
        closeDashboardButton.setOnClickListener {
            finish()
        }
        itemsList.addOnScrollListener(object : PaginationScrollListener(itemsList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return dashboardItemService.itemsManagers.map{it.first.isLast()}.all{it}
            }

            override fun isLoading(): Boolean {
                return itemsSwipeRefresh.isRefreshing
            }

            override fun loadMoreItems() {
                itemsSwipeRefresh.isRefreshing = true;
                dashboardItemService.getItems(false)
            }
        })

        itemsSwipeRefresh.setOnRefreshListener {
            itemsSwipeRefresh.isRefreshing = true;
            dashboardItemService.getItems()
        }
        itemsSwipeRefresh.isRefreshing = true;
        dashboardItemService.getItems()
    }

    private fun setItemsInDashBoardAdapter(resetPage: Boolean, items: Array<DashboardItemObject>) {
//        LocalDateTime.ofEpochSecond(0).
        var newItems = items.sortedByDescending{it.time}.toTypedArray()
        val lol = ArrayList(newItems.groupBy { getDateTime(it.time)}.toList())

        if (resetPage) {
//            (itemsList2?.adapter as DashboardItem2Adapter).setItems(newItems)
            (itemsList?.adapter as DashboardItemAdapter).setItems(lol)
        } else {
//            (itemsList2?.adapter as DashboardItem2Adapter).addItems(newItems)
            (itemsList?.adapter as DashboardItemAdapter).addItems(lol)
        }
        itemsSwipeRefresh.isRefreshing = false
    }

    private fun getDateTime(s: Long): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}