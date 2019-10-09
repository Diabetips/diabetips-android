package com.epitech.diabetips.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.Adapters.MealRecipeAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.Storages.RecipeObject
import kotlinx.android.synthetic.main.activity_new_meal.*

class NewMealActivity : AppCompatActivity() {

    enum class RequestCode {SEARCH_RECIPE}

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_meal)
        recipeButton.setOnClickListener {
            startActivityForResult(Intent(this, RecipeActivity::class.java), RequestCode.SEARCH_RECIPE.ordinal)
        }
        recipeList.apply {
            layoutManager = LinearLayoutManager(this@NewMealActivity)
            adapter = MealRecipeAdapter()
        }
        validateMealButton.setOnClickListener {
            //TODO Call API
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
