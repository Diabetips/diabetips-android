package com.epitech.diabetips.services

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.DashboardItemObject
import com.epitech.diabetips.storages.PaginationObject
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

class DashboardItemsService (var context: Context, val ItemsUpdated : (Array<DashboardItemObject>, Boolean) -> Unit){
    public lateinit var itemsManagers:  Array<Pair<PaginationObject,(PaginationObject) -> Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>>>;
    lateinit var page: PaginationObject

    init {
        page = PaginationObject(context.resources.getInteger(R.integer.pagination_size), context.resources.getInteger(R.integer.pagination_default))
        itemsManagers = arrayOf(
            Pair(page.copy(), ::getMeals),
            Pair(page.copy(), ::getInsulins),
            Pair(page.copy(), ::getSugars)
//            Pair(page.copy(), ::getComments),
        )
    }

    var listener: (()->Unit)? = null

    private fun getDateTime(s: Long): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    public fun ItemsUpdated(items: Array<DashboardItemObject>, resetPage: Boolean) {

    }

    public fun getItems(resetPage: Boolean = true, index: Int = 0, items: Array<DashboardItemObject> = arrayOf()) {
        if (index == 0) {
            if (resetPage)
                itemsManagers.forEach { it.first.reset() }
            else
                itemsManagers.forEach { it.first.nextPage() }
        }

        if (index >= itemsManagers.size)
            ItemsUpdated.invoke(items, resetPage)
        else
            itemsManagers[index].second(itemsManagers[index].first).doOnSuccess{
                itemsManagers[index].first.updateFromHeader(it.first.headers[context.getString(R.string.pagination_header)]?.get(0))
                if (it.third == null && it.second != null) {
                    getItems(resetPage, index + 1, items + it.second as Array<DashboardItemObject>)
                }
            }.subscribe();
    }

    private fun getMeals(page: PaginationObject ): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>> {
        return MealService.instance.getAllUserMeals(page).map{
            Triple(it.first, it.second.component1()?.map{
                    item -> DashboardItemObject(item, context)
            }?.toTypedArray(), it.second.component2())
        };
    }

    private fun getComments(page: PaginationObject): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>{
        return MealService.instance.getAllUserMeals(page).map{Triple(it.first, it.second.component1()?.map{item ->
            DashboardItemObject(item, context) }?.toTypedArray(), it.second.component2())};
    }

    private fun getInsulins(page: PaginationObject): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>{
        return InsulinService.instance.getAllUserInsulin(page).map{Triple(it.first, it.second.component1()?.map{item ->
            DashboardItemObject(item, context) }?.toTypedArray(), it.second.component2())};
    }

    private fun getSugars(page: PaginationObject): Single<Triple<Response, Array<DashboardItemObject>?, FuelError?>>{
        return BloodSugarService.instance.getAllMeasures(page).map{Triple(it.first, it.second.component1()?.map{item ->
            DashboardItemObject(item, context) }?.toTypedArray(), it.second.component2())};
    }
}
