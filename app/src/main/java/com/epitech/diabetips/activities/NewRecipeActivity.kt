package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.adapters.RecipeFoodAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.services.DiabetipsService
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.storages.RecipeObject
import kotlinx.android.synthetic.main.activity_new_meal.*
import kotlinx.android.synthetic.main.activity_new_recipe.*

class NewRecipeActivity : AppCompatActivity() {

    enum class RequestCode {SEARCH_FOOD}

    private var recipeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        addFoodButton.setOnClickListener {
            startActivityForResult(Intent(this, FoodActivity::class.java), RequestCode.SEARCH_FOOD.ordinal)
        }
        foodList.apply {
            layoutManager = LinearLayoutManager(this@NewRecipeActivity)
            adapter = RecipeFoodAdapter()
        }
        validateRecipeButton.setOnClickListener {
            val recipe = RecipeObject(recipeId, "Recette", "Ceci est une recette",
                (foodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray())
            if (recipe.name.isBlank()) {
                Toast.makeText(this, getString(R.string.recipe_name_empty), Toast.LENGTH_SHORT).show()
            } else if (recipe.ingredients.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.recipe_empty), Toast.LENGTH_SHORT).show()
            } else {
                DiabetipsService.instance.createOrUpdateRecipe(recipe).doOnSuccess {
                    if (it.second.component2() == null) {
                        setResult(Activity.RESULT_OK, Intent().putExtra(
                            getString(R.string.param_recipe), it.second.component1()))
                        finish()
                    }
                }.subscribe()
            }
        }
        getParams()
    }

    private fun getParams() {
        if (intent.hasExtra(getString(R.string.param_recipe))) {
            val recipe = (intent.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
            recipeId = recipe.id
            if (recipeId > 0) {
                (recipeList.adapter as RecipeFoodAdapter).setFoods(recipe.ingredients)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                (foodList.adapter as RecipeFoodAdapter).addFood(
                    data?.getSerializableExtra(getString(R.string.param_food)) as FoodObject)
            }
        }
    }

}
