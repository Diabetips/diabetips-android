package com.epitech.diabetips.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.NewRecipeActivity
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.*
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_recipe.view.*

class RecipeFragment : ANavigationFragment(FragmentType.RECIPES), IRecipe {

    override lateinit var activityMode: ActivityMode
    override lateinit var displayMode: DisplayMode
    override lateinit var page: PaginationObject
    override lateinit var recipeActivity: Activity
    override lateinit var recipeContext: Context
    override lateinit var recipeSearchList: RecyclerView
    override lateinit var recipeSearchView: SearchView
    override lateinit var recipeSwipeRefresh: SwipeRefreshLayout
    override lateinit var recipeToggleAll: MaterialButton
    override lateinit var recipeToggleFavorite: MaterialButton
    override lateinit var recipeTogglePersonal: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = createFragmentView(R.layout.activity_recipe, inflater, container)
        initView(requireActivity(), requireContext(), view.recipeSearchView, view.recipeSearchList, view.recipeSwipeRefresh, view.recipeToggleAll, view.recipeToggleFavorite, view.recipeTogglePersonal)
        view.newRecipeButton.setOnClickListener {
            startActivityForResult(Intent(requireContext(), NewRecipeActivity::class.java), RequestCode.NEW_RECIPE.ordinal)
        }
        view.recipeNotFoundButton.setOnClickListener {
            view.newRecipeButton.callOnClick()
        }
        view.navigationRecipeLayout.visibility = View.VISIBLE
        view.closeRecipeButton.visibility = View.GONE
        getRecipe()
        return view
    }

    override fun isLoading(): Boolean {
        return view?.recipeSwipeRefresh?.isRefreshing ?: true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onRecipeActivityResult(requestCode, resultCode, data)
    }

}
