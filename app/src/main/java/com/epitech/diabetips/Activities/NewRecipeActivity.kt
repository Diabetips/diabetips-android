package com.epitech.diabetips.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.Adapters.RecipeFoodAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.Storages.FoodObject
import com.epitech.diabetips.Storages.RecipeObject
import kotlinx.android.synthetic.main.activity_new_recipe.*

class NewRecipeActivity : AppCompatActivity() {

    enum class RequestCode {SEARCH_FOOD}

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        foodButton.setOnClickListener {
            startActivityForResult(Intent(this, FoodActivity::class.java), RequestCode.SEARCH_FOOD.ordinal)
        }
        foodList.apply {
            layoutManager = LinearLayoutManager(this@NewRecipeActivity)
            adapter = RecipeFoodAdapter()
        }
        validateRecipeButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_recipe),
                RecipeObject("", "Recette", "Ceci est une recette",
                    (foodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray())))
            finish()
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
