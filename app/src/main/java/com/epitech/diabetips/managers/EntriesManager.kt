package com.epitech.diabetips.managers

import android.content.Context
import android.util.Log
import com.epitech.diabetips.R
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.services.NoteService
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.TimeHandler
import com.epitech.diabetips.utils.toInt
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import io.reactivex.Single
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

typealias DashboardItemsResponse = Single<Triple<Response, Array<EntryObject>?, FuelError?>>

class EntriesManager(
    private val context: Context,
    private var page: PaginationObject? = null,
    private var itemsManagers:  Array<EntryManager> = arrayOf(),
    private val ItemsUpdated: (Array<EntryObject>, Boolean) -> Unit){

    data class EntryManager(
        var page: PaginationObject,
        val getter: (PaginationObject) -> DashboardItemsResponse,
        val type: EntryObject.Type,
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
            EntryManager(page!!.copy(), ::getSugars, EntryObject.Type.SUGAR),
            EntryManager(page!!.copy(), ::getMeals, EntryObject.Type.MEAL),
            EntryManager(page!!.copy(), ::getInsulins, EntryObject.Type.INSULIN),
            EntryManager(page!!.copy(), ::getComments, EntryObject.Type.COMMENT)
        )
    }

    fun deactivate(type: EntryObject.Type) {
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
        if (!manager.needRefresh())
            return getItemsRec(resetPage, index + 1, items)

        manager.getter(manager.page).doOnSuccess{
            manager.page.updateFromHeader(it.first.headers[context.getString(R.string.pagination_header)]?.get(0))
            manager.page.nextPage()
            val newIndex = index + (!manager.needRefresh()).toInt()
            val newItems = items + if (it.third == null && it.second != null) it.second as Array<EntryObject> else arrayOf()
            getItemsRec(resetPage, newIndex, newItems)
        }.subscribe();
    }

    fun getPage(): PaginationObject? {
        return page;
    }

    private fun getMeals(page: PaginationObject ): DashboardItemsResponse {
        return MealService.instance.getAllUserMeals(page)
            .map{Triple(it.first, it.second.component1()
                ?.map{ item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())};
    }

    private fun getComments(page: PaginationObject): DashboardItemsResponse {
        return NoteService.instance.getAllUserNote(page)
            .map{Triple(it.first, it.second.component1()
                ?.map{ item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())};
    }

    private fun getInsulins(page: PaginationObject): DashboardItemsResponse {
        return InsulinService.instance.getAllUserInsulin(page)
            .map{Triple(it.first, it.second.component1()
                ?.map{ item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())};
    }

    private fun getSugars(page: PaginationObject): DashboardItemsResponse {
        return BloodSugarService.instance.getAllMeasures(page)
            .map{Triple(it.first, it.second.component1()
                ?.map{ item -> EntryObject(item, context) }
                ?.toTypedArray(), it.second.component2())};
    }
}
