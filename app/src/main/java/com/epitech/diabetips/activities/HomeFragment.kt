package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.MealAdapter
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.services.NfcReaderService
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.utils.ChartHandler
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import com.epitech.diabetips.utils.PaginationScrollListener
import com.github.mikephil.charting.data.Entry
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : NavigationFragment(FragmentType.HOME) {


    private lateinit var page: PaginationObject

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        setSugarLineChartData(view)
        return view
    }

    private fun getMeal(view: View? = this.view, resetPage: Boolean = true) {
/*        view?.mealHomeSwipeRefresh?.isRefreshing = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        MealService.instance.getAllUserMeals(page).doOnSuccess {
            if (it.second.component2() == null) {
                page.updateFromHeader(it.first.headers[getString(R.string.pagination_header)]?.get(0))
                if (resetPage)
                    (view?.mealHomeList?.adapter as MealAdapter).setMeals(it.second.component1()!!)
                else
                    (view?.mealHomeList?.adapter as MealAdapter).addMeals(it.second.component1()!!)
            }
            view?.mealHomeSwipeRefresh?.isRefreshing = false
        }.subscribe() */
    }

    private fun setSugarLineChartData(view: View? = this.view) {
        val entries: ArrayList<Entry> = ArrayList()
        entries.add(Entry(0f, 5f))
        entries.add(Entry(5f, 10f))
        entries.add(Entry(10f, 8f))
        entries.add(Entry(15f, 15f))
        view?.sugarLineChart?.data = ChartHandler.instance.handleLineDataCreation(context!!, entries)
        view?.sugarLineChart?.invalidate()
    }

    override fun isLoading(): Boolean {
        return false;
//        return view?.mealHomeSwipeRefresh?.isRefreshing ?: false
    }
}
