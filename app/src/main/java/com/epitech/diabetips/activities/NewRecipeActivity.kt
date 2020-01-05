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
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.activity_new_recipe.*

class NewRecipeActivity : AppCompatActivity() {

    enum class RequestCode {SEARCH_FOOD}

    private var recipeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        addFoodButton.setOnClickListener {
            startActivityForResult(Intent(this, FoodActivity::class.java), RequestCode.SEARCH_FOOD.ordinal)
        }
        foodList.apply {
            layoutManager = LinearLayoutManager(this@NewRecipeActivity)
            adapter = RecipeFoodAdapter()
        }
        validateRecipeButton.setOnClickListener {
            val recipe = RecipeObject(recipeId, newRecipeName.text.toString(), newRecipeDescription.text.toString(),
                (foodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray())
            if (recipe.name.isBlank()) {
                Toast.makeText(this, getString(R.string.recipe_name_empty), Toast.LENGTH_SHORT).show()
            } else if (recipe.ingredients.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.recipe_empty), Toast.LENGTH_SHORT).show()
            } else {
                RecipeService.instance.createOrUpdateRecipe(recipe).doOnSuccess {
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
            newRecipeName.setText(recipe.name)
            newRecipeDescription.setText(recipe.description)
            if (recipeId > 0 && recipe.ingredients.isNotEmpty()) {
                (foodList.adapter as RecipeFoodAdapter).setFoods(recipe.ingredients)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                val ingredientObject = IngredientObject()
                ingredientObject.food = data?.getSerializableExtra(getString(R.string.param_food)) as FoodObject
                ingredientObject.id = ingredientObject.food.id
                ingredientObject.quantity = 1f
                (foodList.adapter as RecipeFoodAdapter).addFood(ingredientObject)
            }
        }
    }

}
