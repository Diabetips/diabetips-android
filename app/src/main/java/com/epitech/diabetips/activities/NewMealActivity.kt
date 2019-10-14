package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.adapters.MealRecipeAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.services.DiabetipsService
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.RecipeObject
import kotlinx.android.synthetic.main.activity_new_meal.*
import java.util.*

class NewMealActivity : AppCompatActivity() {

    enum class RequestCode {SEARCH_RECIPE}

    private var mealId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_meal)
        addRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, RecipeActivity::class.java), RequestCode.SEARCH_RECIPE.ordinal)
        }
        recipeList.apply {
            layoutManager = LinearLayoutManager(this@NewMealActivity)
            adapter = MealRecipeAdapter()
        }
        validateMealButton.setOnClickListener {
            val meal = MealObject(mealId, Date(), newMealDescription.text.toString(),
                (recipeList.adapter as MealRecipeAdapter).getRecipes().toTypedArray())
            if (meal.recipes.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.meal_empty), Toast.LENGTH_SHORT).show()
            } else {
                DiabetipsService.instance.createOrUpdateUserMeal(AccountManager.instance.getAccountUid(this), meal).doOnSuccess {
                    if (it.second.component2() == null) {
                        setResult(Activity.RESULT_OK, Intent().putExtra(
                            getString(R.string.param_meal), it.second.component1()))
                        finish()
                    }
                }.subscribe()
            }
        }
        getParams()
    }

    private fun getParams() {
        if (intent.hasExtra(getString(R.string.param_meal))) {
            val meal = (intent.getSerializableExtra(getString(R.string.param_meal)) as MealObject)
            mealId = meal.id
            newMealDescription.setText(meal.description)
            if (mealId > 0 && meal.recipes.isNotEmpty()) {
                (recipeList.adapter as MealRecipeAdapter).setRecipes(meal.recipes)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_RECIPE.ordinal) {
                (recipeList.adapter as MealRecipeAdapter).addRecipe(
                    data?.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
            }
        }
    }
}
