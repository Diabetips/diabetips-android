package com.epitech.diabetips.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epitech.diabetips.adapters.RecipeAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.NewRecipeActivity
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.storages.RecipeObject
import kotlinx.android.synthetic.main.activity_recipe.view.*

interface IRecipe {

    enum class ActivityMode {SELECT, UPDATE}
    enum class RequestCode {NEW_RECIPE, UPDATE_RECIPE}

    var activityMode: ActivityMode
    var page: PaginationObject
    var recipeActivity: Activity
    var recipeContext: Context
    var recipeSearchView: SearchView
    var recipeSearchList: RecyclerView
    var recipeSwipeRefresh: SwipeRefreshLayout


    fun initView(activity: Activity, context: Context, searchView: SearchView, recyclerView: RecyclerView, swipeRefreshLayout: SwipeRefreshLayout) {
        this.recipeContext = context
        this.recipeActivity = activity
        this.recipeSearchView = searchView
        this.recipeSearchList = recyclerView
        this.recipeSwipeRefresh = swipeRefreshLayout
        page = PaginationObject(recipeContext.resources.getInteger(R.integer.pagination_size), recipeContext.resources.getInteger(R.integer.pagination_default))
        getParams()
        initSearchList()
        recipeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(s: String): Boolean {
                return true
            }
            override fun onQueryTextSubmit(s: String): Boolean {
                getRecipe()
                return true
            }
        })
        recipeSwipeRefresh.setOnRefreshListener {
            getRecipe()
        }
    }

    private fun getParams() {
        if (recipeActivity.intent.hasExtra(recipeContext.getString(R.string.param_mode))) {
            activityMode = (recipeActivity.intent.getSerializableExtra(recipeContext.getString(R.string.param_mode)) as ActivityMode)
        } else {
            activityMode =
                ActivityMode.UPDATE
        }
    }

    private fun initSearchList() {
        recipeSearchList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RecipeAdapter { recipe : RecipeObject ->
                if (activityMode == ActivityMode.UPDATE) {
                    startActivityForResult(recipeActivity, Intent(context, NewRecipeActivity::class.java)
                        .putExtra(context.getString(R.string.param_recipe), recipe),
                        RequestCode.UPDATE_RECIPE.ordinal, Bundle.EMPTY)
                } else {
                    selectRecipe(recipe)
                }
            }
            (adapter as RecipeAdapter).setVisibilityElements(recipeNotFoundLayout, recipeSwipeRefresh, false)
        }
        recipeSearchList.addOnScrollListener(object : PaginationScrollListener(recipeSearchList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return page.isLast()
            }
            override fun isLoading(): Boolean {
                return recipeSwipeRefresh.isRefreshing
            }
            override fun loadMoreItems() {
                getRecipe(false)
            }
        })
    }

    fun getRecipe(resetPage: Boolean = true) {
        recipeSwipeRefresh.isRefreshing = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        RecipeService.instance.getAll<RecipeObject>(page, recipeSearchView.query.toString()).doOnSuccess {
            if (it.second.component2() == null) {
                page.updateFromHeader(it.first.headers[recipeContext.getString(R.string.pagination_header)]?.get(0))
                if (resetPage)
                    (recipeSearchList.adapter as RecipeAdapter).setRecipes(it.second.component1()!!)
                else
                    (recipeSearchList.adapter as RecipeAdapter).addRecipes(it.second.component1()!!)
            }
            recipeSwipeRefresh.isRefreshing = false
        }.subscribe()
        getParams()
    }

    fun onRecipeActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.NEW_RECIPE.ordinal) {
                selectRecipe(data?.getSerializableExtra(recipeContext.getString(R.string.param_recipe)) as RecipeObject)
            } else if (requestCode == RequestCode.UPDATE_RECIPE.ordinal) {
                (recipeSearchList.adapter as RecipeAdapter).updateRecipe(
                    data?.getSerializableExtra(recipeContext.getString(R.string.param_recipe)) as RecipeObject)
            }
        }
    }

    fun selectRecipe(recipe: RecipeObject) {
        recipeActivity.setResult(Activity.RESULT_OK, Intent().putExtra(recipeContext.getString(R.string.param_recipe), recipe))
        recipeActivity.finish()
    }

}
