package com.epitech.diabetips.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.Adapters.RecipeAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.Storages.RecipeObject
import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : AppCompatActivity() {

    enum class RequestCode {NEW_RECIPE}

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        newRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, NewRecipeActivity::class.java), RequestCode.NEW_RECIPE.ordinal)
        }
        //TODO remove variable when using the API
        val recipes : ArrayList<RecipeObject> = arrayListOf(
            RecipeObject("", "Nuggets", "Top Tier"),
            RecipeObject("", "Ramen", ""),
            RecipeObject("", "Hamburger", "USA!")
        )
        recipeSearchList.apply {
            layoutManager = LinearLayoutManager(this@RecipeActivity)
            adapter = RecipeAdapter(recipes) { recipe : RecipeObject -> selectRecipe(recipe) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.NEW_RECIPE.ordinal) {
                    selectRecipe(data?.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
            }
        }
    }

    private fun selectRecipe(recipe: RecipeObject) {
        setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_recipe), recipe))
                finish()
    }
}
