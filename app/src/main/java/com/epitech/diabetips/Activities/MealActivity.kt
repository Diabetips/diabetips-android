package com.epitech.diabetips.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R
import kotlinx.android.synthetic.main.activity_meal.*

class MealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)
        recipeButton.setOnClickListener {
            startActivity(Intent(this, RecipeActivity::class.java))
        }
    }
}
