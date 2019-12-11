package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.adapters.RecipeAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.RecipeObject
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {

    enum class ActivityMode {SELECT, UPDATE}
    enum class RequestCode {NEW_RECIPE, UPDATE_RECIPE}

    private var activityMode: ActivityMode = ActivityMode.SELECT

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        recipeSearchBar.setOnSearchActionListener(this)
        newRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, NewRecipeActivity::class.java), RequestCode.NEW_RECIPE.ordinal)
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
        }
        recipeSwipeRefresh.setOnRefreshListener {
            getRecipe()
        }
        getRecipe()
    }
    private fun getRecipe() {
        recipeSwipeRefresh.isRefreshing = true
        RecipeService.instance.getAllRecipes(recipeSearchBar.text.toString()).doOnSuccess {
            if (it.second.component2() == null) {
                (recipeSearchList.adapter as RecipeAdapter).setRecipes(it.second.component1()!!)
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

    override fun onButtonClicked(buttonCode: Int) {}
    override fun onSearchStateChanged(enabled: Boolean) {}
    override fun onSearchConfirmed(text: CharSequence?) {
        getRecipe()
        recipeSearchBar.clearFocus()
    }
}
