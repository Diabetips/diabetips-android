package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.MealAdapter
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var page: PaginationObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        setContentView(R.layout.activity_dashboard)
        mealsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MealAdapter { meal : MealObject ->
                startActivityForResult(
                    Intent(context, NewMealActivity::class.java)
                        .putExtra(getString(R.string.param_meal), meal),
                    HomeFragment.RequestCode.UPDATE_MEAL.ordinal)
            }
        }
        closeDashboardButton.setOnClickListener {
            finish()
        }
        mealsList.addOnScrollListener(object : PaginationScrollListener(mealsList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return page.isLast()
            }

            override fun isLoading(): Boolean {
                return mealsSwipeRefresh.isRefreshing
            }

            override fun loadMoreItems() {
                getMeal(false)
            }
        })
        mealsSwipeRefresh.setOnRefreshListener {
            getMeal()
        }
        getMeal()
    }

    private fun getMeal(resetPage: Boolean = true) {
        mealsSwipeRefresh?.isRefreshing = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        MealService.instance.getAllUserMeals(page).doOnSuccess {
            if (it.second.component2() == null) {
                page.updateFromHeader(it.first.headers[getString(R.string.pagination_header)]?.get(0))
                if (resetPage)
                    (mealsList?.adapter as MealAdapter).setMeals(it.second.component1()!!)
                else
                    (mealsList?.adapter as MealAdapter).addMeals(it.second.component1()!!)
            }
            mealsSwipeRefresh.isRefreshing = false
        }.subscribe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.NEW_MEAL.ordinal) {
                (view?.mealHomeList?.adapter as MealAdapter).addMeal(data?.getSerializableExtra(getString(R.string.param_meal)) as MealObject)
            } else if (requestCode == RequestCode.UPDATE_MEAL.ordinal) {
                (view?.mealHomeList?.adapter as MealAdapter).updateMeal(data?.getSerializableExtra(getString(R.string.param_meal)) as MealObject)
            }
        }*/
    }
}
