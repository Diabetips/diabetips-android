package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.adapters.FoodAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.services.FoodService
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.PaginationScrollListener
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_food.*

class FoodActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {

    private lateinit var page: PaginationObject

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        foodSearchBar.setOnSearchActionListener(this)
        foodSearchList.apply {
            layoutManager = LinearLayoutManager(this@FoodActivity)
            adapter = FoodAdapter { food : FoodObject ->
                setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_food), food))
                finish()
            }
        }
        foodSearchList.addOnScrollListener(object : PaginationScrollListener(foodSearchList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return page.isLast()
            }

            override fun isLoading(): Boolean {
                return foodSwipeRefresh.isRefreshing
            }

            override fun loadMoreItems() {
                getFood(false)
            }
        })
        foodSwipeRefresh.setOnRefreshListener {
            getFood()
        }
        getFood()
    }

    private fun getFood(resetPage: Boolean = true) {
        foodSwipeRefresh.isRefreshing = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        FoodService.instance.getAllFood(page, foodSearchBar.text.toString()).doOnSuccess {
            if (it.second.component2() == null) {
                page.updateFromHeader(it.first.headers[getString(R.string.pagination_header)]?.get(0))
                if (resetPage)
                    (foodSearchList.adapter as FoodAdapter).setFoods(it.second.component1()!!)
                else
                    (foodSearchList.adapter as FoodAdapter).addFoods(it.second.component1()!!)
            }
            foodSwipeRefresh.isRefreshing = false
        }.subscribe()
    }

    override fun onButtonClicked(buttonCode: Int) {}
    override fun onSearchStateChanged(enabled: Boolean) {}
    override fun onSearchConfirmed(text: CharSequence?) {
        getFood()
        foodSearchBar.clearFocus()
    }

}
