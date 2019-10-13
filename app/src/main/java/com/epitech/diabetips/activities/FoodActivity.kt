package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.adapters.FoodAdapter
import com.epitech.diabetips.R
import com.epitech.diabetips.services.DiabetipsService
import com.epitech.diabetips.storages.FoodObject
import kotlinx.android.synthetic.main.activity_food.*

class FoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        //TODO remove variable when using the API
        val foods : ArrayList<FoodObject> = arrayListOf(FoodObject(0, "Poulet", "Kg"),
            FoodObject(0, "Patate", "U"), FoodObject(0, "Riz", "g"),
            FoodObject(0, "Eau", "L")
        )
        foodSearchList.apply {
            layoutManager = LinearLayoutManager(this@FoodActivity)
            adapter = FoodAdapter(foods) { food : FoodObject ->
                setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_food), food))
                finish()
            }
        }
        DiabetipsService.instance.getAllFood().doOnSuccess {
            if (it.second.component2() == null) {
                (foodSearchList.adapter as FoodAdapter).setFoods(it.second.component1()!!)
            }
        }.subscribe()
    }

}
