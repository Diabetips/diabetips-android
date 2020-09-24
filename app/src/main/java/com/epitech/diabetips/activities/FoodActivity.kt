package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.FoodAdapter
import com.epitech.diabetips.services.FoodService
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.DialogHandler
import com.epitech.diabetips.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_food.*

class FoodActivity : ADiabetipsActivity(R.layout.activity_food) {

    private var saved: Boolean? = null
    private lateinit var page: PaginationObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        closeFoodButton.setOnClickListener {
            onBackPressed()
        }
        foodSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(s: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(s: String): Boolean {
                getFood()
                return true
            }
        })
        foodSearchList.apply {
            layoutManager = LinearLayoutManager(this@FoodActivity)
            adapter = FoodAdapter { foodObject, checkBox ->
                if (checkBox != null && checkBox.isChecked) {
                    val ingredientObject: IngredientObject = (adapter as FoodAdapter).getSelectedIngredientOrNew(foodObject)
                    checkBox.isChecked = (ingredientObject.quantity > 0)
                    DialogHandler.dialogSelectQuantity(this@FoodActivity, layoutInflater, ingredientObject) {
                        (adapter as FoodAdapter).addSelectedIngredient(ingredientObject)
                        checkBox.isChecked = true
                        saved = false
                    }
                } else {
                    saved = false
                }
            }
            (adapter as FoodAdapter).setVisibilityElements(foodNotFoundLayout, foodSwipeRefresh, false)
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
        foodValidateButton.setOnClickListener {
            returnFoods()
        }
        getParams()
        getFood()
    }

    private fun getParams() {
        if (intent.hasExtra(getString(R.string.param_food))) {
            (foodSearchList.adapter as FoodAdapter).setSelectedIngredients(ArrayList((intent.getSerializableExtra(getString(R.string.param_food)) as ArrayList<*>).filterIsInstance<IngredientObject>()))
        }
    }

    private fun getFood(resetPage: Boolean = true) {
        foodSwipeRefresh.isRefreshing = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        FoodService.instance.getAll<FoodObject>(page, foodSearchView.query.toString()).doOnSuccess {
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

    private fun returnFoods() {
        setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_food), (foodSearchList.adapter as FoodAdapter).getSelectedIngredients()))
        finish()
    }

    override fun onBackPressed() {
        if (saved == null) {
            finish()
        } else if (saved!!) {
            returnFoods()
        } else {
            DialogHandler.dialogSaveChange(this, layoutInflater, { returnFoods() }, { finish() })
        }
    }

}
