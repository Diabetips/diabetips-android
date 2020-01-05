package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.MealAdapter
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.activity_meal.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    enum class RequestCode {NEW_MEAL, UPDATE_MEAL}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        view.newMealButton.setOnClickListener {
            startActivityForResult(Intent(context, NewMealActivity::class.java), RequestCode.NEW_MEAL.ordinal)
        }
        view.viewRecipeButton.setOnClickListener {
            startActivity(Intent(context, RecipeActivity::class.java)
                .putExtra(getString(R.string.param_mode), RecipeActivity.ActivityMode.UPDATE))
        }
        view.mealHomeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MealAdapter { meal : MealObject ->
                startActivityForResult(
                    Intent(context, NewMealActivity::class.java)
                        .putExtra(getString(R.string.param_meal), meal),
                    RequestCode.UPDATE_MEAL.ordinal)
            }
        }
        MealService.instance.getAllUserMeals().doOnSuccess {
            if (it.second.component2() == null) {
                (mealSearchList.adapter as MealAdapter).setMeals(it.second.component1()!!)
            }
        }.subscribe()
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.NEW_MEAL.ordinal) {
                (mealSearchList.adapter as MealAdapter).addMeal(data?.getSerializableExtra(getString(R.string.param_meal)) as MealObject)
            } else if (requestCode == RequestCode.UPDATE_MEAL.ordinal) {
                (mealSearchList.adapter as MealAdapter).updateMeal(data?.getSerializableExtra(getString(R.string.param_meal)) as MealObject)
            }
        }
    }
}
