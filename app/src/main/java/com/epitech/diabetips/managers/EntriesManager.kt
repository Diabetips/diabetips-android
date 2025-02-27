package com.epitech.diabetips.managers

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.services.*
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.utils.ObjectType
import com.epitech.diabetips.utils.toInt
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import io.reactivex.Single

typealias DashboardItemsResponse = Single<Triple<Response, Array<EntryObject>?, FuelError?>>

class EntriesManager(
    private val context: Context,
    private var page: PaginationObject? = null,
    private var itemsManagers:  Array<EntryManager> = arrayOf(),
    private val ItemsUpdated: (Array<EntryObject>, Boolean) -> Unit){

    data class EntryManager(
        var page: PaginationObject,
        val getter: (PaginationObject) -> DashboardItemsResponse,
        val type: ObjectType,
        var activated: Boolean = true) {
         fun needRefresh(): Boolean {
             return activated && !page.isLast()
         }
    }

    init {
        if (page == null)
            page = PaginationObject(
                context.resources.getInteger(R.integer.pagination_size),
                context.resources.getInteger(R.integer.pagination_default))

        itemsManagers = arrayOf(
            EntryManager(page!!.copy(), ::getSugars, ObjectType.SUGAR),
            EntryManager(page!!.copy(), ::getMeals, ObjectType.MEAL),
            EntryManager(page!!.copy(), ::getInsulins, ObjectType.INSULIN),
            EntryManager(page!!.copy(), ::getActivities, ObjectType.ACTIVITY),
            EntryManager(page!!.copy(), ::getComments, ObjectType.NOTE)
        )
    }

    fun deactivate(type: ObjectType) {
        itemsManagers.find { it.type === type }?.activated = false
    }

    fun updatePages() {
        page?.let { setPages(it) }
    }

    fun setPages(newPage: PaginationObject) {
        this.itemsManagers.forEach {
            it.page = newPage.copy()
        }
    }

    fun isLastPage(): Boolean {
        return itemsManagers.map{!it.needRefresh()}.all{it}
    }

    fun getItems(resetPage: Boolean = true, index: Int = 0, items: Array<EntryObject> = arrayOf()) {
        itemsManagers.forEach{ if (resetPage) it.page.reset() else it.page.nextPage() }
        return getItemsRec(resetPage, index, items)
    }

    private fun getItemsRec(resetPage: Boolean = true, index: Int = 0, items: Array<EntryObject> = arrayOf()) {
        if (index >= itemsManagers.size)
            return ItemsUpdated.invoke(items, resetPage)

        val manager = itemsManagers[index]
        if (!manager.activated)
            return getItemsRec(resetPage, index + 1, items)

        manager.getter(manager.page).doOnSuccess{
            manager.page.updateFromHeader(it.first.headers[context.getString(R.string.pagination_header)]?.get(0))
            val newIndex = index + (!manager.needRefresh()).toInt()
            manager.page.nextPage()
            val newItems = items + if (it.third == null && it.second != null) it.second as Array<EntryObject> else arrayOf()
            getItemsRec(resetPage, newIndex, newItems)
        }.subscribe()
    }

    fun getPage(): PaginationObject? {
        return page
    }

    private fun getMeals(page: PaginationObject ): DashboardItemsResponse {
        return MealService.instance.getAll<MealObject>(page).map{
            Triple(it.first, it.second.component1()
                ?.map { item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())
        }
    }

    private fun getComments(page: PaginationObject): DashboardItemsResponse {
        return NoteService.instance.getAll<NoteObject>(page).map {
            Triple(it.first, it.second.component1()
                ?.map { item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())
        }
    }

    private fun getInsulins(page: PaginationObject): DashboardItemsResponse {
        return InsulinService.instance.getAll<InsulinObject>(page).map {
            Triple(it.first, it.second.component1()
                ?.map { item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())
        }
    }

    private fun getActivities(page: PaginationObject): DashboardItemsResponse {
        return ActivityService.instance.getAll<ActivityObject>(page).map {
            Triple(it.first, it.second.component1()
                ?.map { item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())
        }
    }

    private fun getSugars(page: PaginationObject): DashboardItemsResponse {
        return BloodSugarService.instance.getAllMeasures(page).map {
            Triple(it.first, it.second.component1()
                ?.map { item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())
        }
    }
}
