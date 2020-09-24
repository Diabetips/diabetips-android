package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.*
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_recipe.recipeSearchList as searchList
import kotlinx.android.synthetic.main.activity_recipe.recipeSearchView as searchView
import kotlinx.android.synthetic.main.activity_recipe.recipeSwipeRefresh as swipeRefresh
import kotlinx.android.synthetic.main.activity_recipe.recipeToggleAll as toggleAll
import kotlinx.android.synthetic.main.activity_recipe.recipeToggleFavorite as toggleFavorite
import kotlinx.android.synthetic.main.activity_recipe.recipeTogglePersonal as togglePersonal

class RecipeActivity : ADiabetipsActivity(R.layout.activity_recipe), IRecipe {

    override lateinit var activityMode: ActivityMode
    override lateinit var displayMode: DisplayMode
    override lateinit var page: PaginationObject
    override lateinit var recipeActivity: Activity
    override lateinit var recipeContext: Context
    override lateinit var recipeSearchView: SearchView
    override lateinit var recipeSearchList: RecyclerView
    override lateinit var recipeSwipeRefresh: SwipeRefreshLayout
    override lateinit var recipeToggleAll: MaterialButton
    override lateinit var recipeToggleFavorite: MaterialButton
    override lateinit var recipeTogglePersonal: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(this, this, searchView, searchList, swipeRefresh, toggleAll, toggleFavorite, togglePersonal)
        closeRecipeButton.setOnClickListener {
            finish()
        }
        newRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, NewRecipeActivity::class.java), RequestCode.NEW_RECIPE.ordinal)
        }
        recipeNotFoundButton.setOnClickListener {
            newRecipeButton.callOnClick()
        }
        getRecipe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onRecipeActivityResult(requestCode, resultCode, data)
    }

}
