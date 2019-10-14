package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.adapters.MealAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.services.DiabetipsService
import com.epitech.diabetips.storages.MealObject
import kotlinx.android.synthetic.main.activity_meal.*
import java.util.*
import kotlin.collections.ArrayList

class MealActivity : AppCompatActivity() {

    enum class RequestCode {NEW_MEAL, UPDATE_MEAL}

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)
        newMealButton.setOnClickListener {
            startActivityForResult(Intent(this, NewMealActivity::class.java), RequestCode.NEW_MEAL.ordinal)
        }
        mealSearchList.apply {
            layoutManager = LinearLayoutManager(this@MealActivity)
            adapter = MealAdapter { meal : MealObject ->
                startActivityForResult(
                    Intent(this@MealActivity, NewMealActivity::class.java)
                        .putExtra(getString(R.string.param_meal), meal),
                    RequestCode.UPDATE_MEAL.ordinal)
            }
        }
        DiabetipsService.instance.getAllUserMeals(AccountManager.instance.getAccountUid(this)).doOnSuccess {
            if (it.second.component2() == null) {
                (mealSearchList.adapter as MealAdapter).setMeals(it.second.component1()!!)
            }
        }.subscribe()
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
