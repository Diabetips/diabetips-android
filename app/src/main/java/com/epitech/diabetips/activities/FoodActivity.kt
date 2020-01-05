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
import com.epitech.diabetips.utils.MaterialHandler
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_food.*

class FoodActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        foodSearchBar.setOnSearchActionListener(this)
        foodSearchList.apply {
            layoutManager = LinearLayoutManager(this@FoodActivity)
            adapter = FoodAdapter { food : FoodObject ->
                setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_food), food))
                finish()
            }
        }
        foodSwipeRefresh.setOnRefreshListener {
            getFood()
        }
        getFood()
    }

    private fun getFood() {
        foodSwipeRefresh.isRefreshing = true
        FoodService.instance.getAllFood(foodSearchBar.text.toString()).doOnSuccess {
            if (it.second.component2() == null) {
                (foodSearchList.adapter as FoodAdapter).setFoods(it.second.component1()!!)
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
