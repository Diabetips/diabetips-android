package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epitech.diabetips.R
import com.epitech.diabetips.services.DashboardItemsService
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.storages.DashboardItemObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.ChartHandler
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : NavigationFragment(FragmentType.HOME) {


    private lateinit var page: PaginationObject
    lateinit var dashboardItemsService: DashboardItemsService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardItemsService = DashboardItemsService(context=context!!){
                items, reset -> itemsUpdateTrigger(reset, items)
        }

        dashboardItemsService.page = PaginationObject(1000, resources.getInteger(R.integer.pagination_default))
        val cur = TimeHandler.instance.currentTimeSecond();
        dashboardItemsService.page.setInterval(cur - 24*60*60, cur)
        dashboardItemsService.getItems()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
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
/*        view.mealHomeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MealAdapter()
        }
        view.mealHomeList.addOnScrollListener(object : PaginationScrollListener(view.mealHomeList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return page.isLast()
            }

            override fun isLoading(): Boolean {
                return view.mealHomeSwipeRefresh.isRefreshing
            }

            override fun loadMoreItems() {
                getMeal(this@HomeFragment.view, false)
            }
        })
        view.mealHomeSwipeRefresh.setOnRefreshListener {
            getMeal()
        }
        getMeal(view) */
        //setSugarLineChartData(view)
//        (activity as NavigationActivity).nfcReader.
        ChartHandler.instance.updateChartData(listOf(), view?.sugarLineChart!!, context!!)
        return view
    }

    private fun itemsUpdateTrigger(reset: Boolean, items: Array<DashboardItemObject>) {
        val map: List<Pair<String, Int>> = items.filter{ it.type == DashboardItemObject.Type.SUGAR}
            .map {Pair(
                SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(it.time * 1000),
                (it.orignal as BloodSugarObject).value)}.sortedBy { it.first }
        ChartHandler.instance.updateChartData(map, view?.sugarLineChart!!, context!!)
    }
    private fun setSugarLineChartData(view: View? = this.view) {
//        ChartHandler.instance.updateChartData(hashMapOf("11h25" to 120, "11h30" to 128,"11h35" to 138,"11h45" to 142,"11h50" to 130,"11h55" to 120,"12h00" to 135),
//            view?.sugarLineChart!!, context!!)
//        view?.sugarLineChart?.data = ChartHandler.instance.handleLineDataCreation(context!!, entries)
//        view?.sugarLineChart?.invalidate()
    }

    override fun isLoading(): Boolean {
        return false;
//        return view?.mealHomeSwipeRefresh?.isRefreshing ?: false
    }
}
