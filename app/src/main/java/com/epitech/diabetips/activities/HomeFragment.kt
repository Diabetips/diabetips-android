package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.ChartHandler
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : NavigationFragment(FragmentType.HOME) {

    lateinit var entriesManager: EntriesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val page = PaginationObject(100, resources.getInteger(R.integer.pagination_default))
        val cur = TimeHandler.instance.currentTimeSecond();
        page.setInterval(cur - 24*60*60, cur)
        Log.d("PAGE INTERVAL : ",page.start.toString() + ":" + page.end.toString())
        entriesManager =
        EntriesManager(context = context!!, page=page) { items, reset ->
                itemsUpdateTrigger(reset, items)
        }
        entriesManager.getItems()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        ChartHandler.instance.handleLineChartStyle(view.sugarLineChart)
        view.newEntryButton.setOnClickListener {
            startActivity(Intent(context, NewEntryActivity::class.java))
        }
        view.viewRecipeButton.setOnClickListener {
            startActivity(Intent(context, RecipeActivity::class.java)
                .putExtra(getString(R.string.param_mode), RecipeActivity.ActivityMode.UPDATE))
        }
        view.openDashboardButton.setOnClickListener {
            startActivity(Intent(context, DashboardActivity::class.java))
        }
        val interval: Pair<Long, Long> = Pair(entriesManager.getPage()!!.start, entriesManager.getPage()!!.end)
        ChartHandler.instance.updateChartData(listOf(), interval, view.sugarLineChart, context!!)
        return view
    }

    private fun itemsUpdateTrigger(reset: Boolean, items: Array<EntryObject>) {
        val map: List<BloodSugarObject> = items.filter{ it.type == EntryObject.Type.SUGAR}
            .map{it.orignal as (BloodSugarObject)}.sortedBy{ it.timestamp }
        if (context == null)
            Log.d("Context", "NULL")
        val interval: Pair<Long, Long> = Pair(entriesManager.getPage()!!.start, entriesManager.getPage()!!.end)
        view?.sugarLineChart?.let { ChartHandler.instance.updateChartData(map, interval, it,  context!!) }
    }
    private fun setSugarLineChartData(view: View? = this.view) {
//        ChartHandler.instance.updateChartData(hashMapOf("11h25" to 120, "11h30" to 128,"11h35" to 138,"11h45" to 142,"11h50" to 130,"11h55" to 120,"12h00" to 135),
//            view?.sugarLineChart!!, context!!)
//        view?.sugarLineChart?.data = ChartHandler.instance.handleLineDataCreation(context!!, entries)
//        view?.sugarLineChart?.invalidate()
    }

    override fun isLoading(): Boolean {
        return false
//        return view?.mealHomeSwipeRefresh?.isRefreshing ?: false
    }
}
