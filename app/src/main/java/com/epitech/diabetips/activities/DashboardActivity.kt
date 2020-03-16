package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DashboardItemAdapter
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
    private lateinit var itemsManagers:  Array<Pair<PaginationObject,(PaginationObject) -> Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>>>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        itemsManagers = arrayOf(
            Pair(page.copy(), ::getMeals),
            Pair(page.copy(), ::getSugars),
            Pair(page.copy(), ::getComments),
            Pair(page.copy(), ::getInsulins)
        )
        setContentView(R.layout.activity_dashboard)
        itemsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DashboardItemAdapter {
/*                    item : DashboardItemObject ->
                startActivityForResult(
                    Intent(context, NewMealActivity::class.java)
                        .putExtra(getString(R.string.param_meal), item),
                    HomeFragment.RequestCode.UPDATE_MEAL.ordinal)*/
            }
        }
        closeDashboardButton.setOnClickListener {
            finish()
        }
        itemsList.addOnScrollListener(object : PaginationScrollListener(itemsList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return itemsManagers.map{it.first.isLast()}.all{it}
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
        getItems()
    }

    private fun getItems(resetPage: Boolean = true, index: Int = 0, items: Array<DashboardItemObject> = arrayOf()) {
        if (index == 0) {
            itemsSwipeRefresh?.isRefreshing = true
            if (resetPage)
                itemsManagers.forEach { it.first.reset() }
            else
                itemsManagers.forEach { it.first.nextPage() }
        }

        if (index >= itemsManagers.size)
            setItemsInDashBoardAdapter(resetPage, items)
        else
            itemsManagers[index].second(itemsManagers[index].first).doOnSuccess{
                itemsManagers[index].first.updateFromHeader(it.first.headers[getString(R.string.pagination_header)]?.get(0))
                if (it.third == null && it.second != null) {
                    getItems(resetPage, index + 1, items + it.second as Array<DashboardItemObject>)
                }
            }.subscribe();
    }

    private fun setItemsInDashBoardAdapter(resetPage: Boolean, items: Array<DashboardItemObject>) {
//        LocalDateTime.ofEpochSecond(0).
        var newItems = items.sortedByDescending{it.time}.toTypedArray()
        val lol = ArrayList(newItems.groupBy { getDateTime(it.time)}.toList())
        if (resetPage)
            (itemsList?.adapter as DashboardItemAdapter).setItems(lol)
        else
            (itemsList?.adapter as DashboardItemAdapter).addItems(lol)
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
    private fun getMeals(page: PaginationObject ): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>> {
        return MealService.instance.getAllUserMeals(page).map{Triple(it.first, it.second.component1()?.map{ item ->
            DashboardItemObject(item, this) }?.toTypedArray(), it.second.component2())};
    }

    private fun getComments(page: PaginationObject): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>{
        return MealService.instance.getAllUserMeals(page).map{Triple(it.first, it.second.component1()?.map{item ->
            DashboardItemObject(item, this) }?.toTypedArray(), it.second.component2())};
    }

    private fun getInsulins(page: PaginationObject): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>{
        return InsulinService.instance.getAllUserInsulin(page).map{Triple(it.first, it.second.component1()?.map{item ->
            DashboardItemObject(item, this) }?.toTypedArray(), it.second.component2())};
    }

    private fun getSugars(page: PaginationObject): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>{
        return MealService.instance.getAllUserMeals(page).map{Triple(it.first, it.second.component1()?.map{item ->
            DashboardItemObject(item, this) }?.toTypedArray(), it.second.component2())};
    }
}