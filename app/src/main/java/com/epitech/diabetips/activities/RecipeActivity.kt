package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.adapters.RecipeAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : AppCompatActivity() {

    enum class ActivityMode {SELECT, UPDATE}
    enum class RequestCode {NEW_RECIPE, UPDATE_RECIPE}

    private lateinit var page: PaginationObject

    private var activityMode: ActivityMode = ActivityMode.SELECT

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        closeRecipeButton.setOnClickListener {
            finish()
        }
        recipeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(s: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(s: String): Boolean {
                getRecipe()
                return true
            }
        })
        newRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, NewRecipeActivity::class.java), RequestCode.NEW_RECIPE.ordinal)
        }
        recipeNotFoundButton.setOnClickListener {
            newRecipeButton.callOnClick()
        }
        recipeSearchList.apply {
            layoutManager = LinearLayoutManager(this@RecipeActivity)
            adapter = RecipeAdapter { recipe : RecipeObject ->
                if (activityMode == ActivityMode.UPDATE) {
                    startActivityForResult(Intent(this@RecipeActivity, NewRecipeActivity::class.java)
                        .putExtra(getString(R.string.param_recipe), recipe),
                        RequestCode.UPDATE_RECIPE.ordinal)
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
        recipeSwipeRefresh.setOnRefreshListener {
            getRecipe()
        }
        getRecipe()
    }

    private fun getRecipe(resetPage: Boolean = true) {
        recipeSwipeRefresh.isRefreshing = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        RecipeService.instance.getAll<RecipeObject>(page, recipeSearchView.query.toString()).doOnSuccess {
            if (it.second.component2() == null) {
                page.updateFromHeader(it.first.headers[getString(R.string.pagination_header)]?.get(0))
                if (resetPage)
                    (recipeSearchList.adapter as RecipeAdapter).setRecipes(it.second.component1()!!)
                else
                    (recipeSearchList.adapter as RecipeAdapter).addRecipes(it.second.component1()!!)
            }
            recipeSwipeRefresh.isRefreshing = false
        }.subscribe()
        getParams()
    }

    private fun getParams() {
        if (intent.hasExtra(getString(R.string.param_mode))) {
            activityMode = (intent.getSerializableExtra(getString(R.string.param_mode)) as ActivityMode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.NEW_RECIPE.ordinal) {
                    selectRecipe(data?.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
            } else if (requestCode == RequestCode.UPDATE_RECIPE.ordinal) {
                (recipeSearchList.adapter as RecipeAdapter).updateRecipe(
                    data?.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
            }
        }
    }

    private fun selectRecipe(recipe: RecipeObject) {
        setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_recipe), recipe))
        finish()
    }

}
