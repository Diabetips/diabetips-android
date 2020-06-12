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
import com.epitech.diabetips.utils.IRecipe
import com.epitech.diabetips.utils.ANavigationFragment
import kotlinx.android.synthetic.main.activity_recipe.view.*

class RecipeFragment : ANavigationFragment(FragmentType.RECIPES), IRecipe {

    override lateinit var activityMode: IRecipe.ActivityMode
    override lateinit var page: PaginationObject
    override lateinit var recipeActivity: Activity
    override lateinit var recipeContext: Context
    override lateinit var recipeSearchList: RecyclerView
    override lateinit var recipeSearchView: SearchView
    override lateinit var recipeSwipeRefresh: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = createFragmentView(R.layout.activity_recipe, inflater, container)
        initView(requireActivity(), requireContext(), view.recipeSearchView, view.recipeSearchList, view.recipeSwipeRefresh)
        view.newRecipeButton.setOnClickListener {
            startActivityForResult(Intent(requireContext(), NewRecipeActivity::class.java), IRecipe.RequestCode.NEW_RECIPE.ordinal)
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
