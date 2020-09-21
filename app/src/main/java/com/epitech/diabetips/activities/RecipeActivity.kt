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
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.IRecipe
import kotlinx.android.synthetic.main.activity_recipe.*
import me.ibrahimsn.lib.SmoothBottomBar
import kotlinx.android.synthetic.main.activity_recipe.recipeSearchList as searchList
import kotlinx.android.synthetic.main.activity_recipe.recipeSearchView as searchView
import kotlinx.android.synthetic.main.activity_recipe.recipeSwipeRefresh as swipeRefresh
import kotlinx.android.synthetic.main.activity_recipe.recipeSelectionBar as selectionBar

class RecipeActivity : ADiabetipsActivity(R.layout.activity_recipe), IRecipe {

    override lateinit var activityMode: IRecipe.ActivityMode
    override lateinit var searchMode: IRecipe.SearchMode
    override lateinit var page: PaginationObject
    override lateinit var recipeActivity: Activity
    override lateinit var recipeContext: Context
    override lateinit var recipeSearchView: SearchView
    override lateinit var recipeSearchList: RecyclerView
    override lateinit var recipeSwipeRefresh: SwipeRefreshLayout
    override lateinit var recipeSelectionBar: SmoothBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(this, this, searchView, searchList, swipeRefresh, selectionBar)
        closeRecipeButton.setOnClickListener {
            finish()
        }
        newRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, NewRecipeActivity::class.java), IRecipe.RequestCode.NEW_RECIPE.ordinal)
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
