package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.FoodAdapter
import com.epitech.diabetips.services.FoodService
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_food.*
import kotlinx.android.synthetic.main.dialog_select_quantity.view.*


class FoodActivity : AppCompatActivity() {

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
        closeFoodButton.setOnClickListener {
            finish()
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
            adapter = FoodAdapter {foodObject, checkBox ->
                val view = layoutInflater.inflate(R.layout.dialog_select_quantity, null)
                MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
                val dialog = AlertDialog.Builder(this@FoodActivity).setView(view).create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val ingredientObject: IngredientObject = (adapter as FoodAdapter).getSelectedIngredientOrNew(foodObject)
                view.selectQuantityInputLayout.hint = view.selectQuantityInputLayout.hint.toString() + " (" + foodObject.unit + ")"
                if (ingredientObject.quantity > 0) {
                    view.selectQuantityInput.setText(ingredientObject.quantity.toString())
                }
                view.selectQuantityNegativeButton.setOnClickListener {
                    dialog.dismiss()
                }
                view.selectQuantityPositiveButton.setOnClickListener {
                    val quantity: Float? = view.selectQuantityInput.text.toString().toFloatOrNull()
                    if (quantity == null || quantity <= 0) {
                        view.selectQuantityInputLayout.error = getString(R.string.quantity_null)
                    } else {
                        ingredientObject.quantity = quantity
                        (adapter as FoodAdapter).addSelectedIngredient(ingredientObject)
                        checkBox?.isChecked = true
                        dialog.dismiss()
                    }
                }
                dialog.show()
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
            setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_food), (foodSearchList.adapter as FoodAdapter).getSelectedIngredients()))
            finish()
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
        FoodService.instance.getAllFood(page, foodSearchView.query.toString()).doOnSuccess {
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

}
