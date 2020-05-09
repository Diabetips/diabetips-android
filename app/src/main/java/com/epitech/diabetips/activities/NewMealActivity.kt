package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.MealRecipeAdapter
import com.epitech.diabetips.adapters.RecipeFoodAdapter
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.MealRecipeObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.utils.DividerItemDecorator
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.TimeHandler
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_new_meal.*
import kotlinx.android.synthetic.main.dialog_save_change.view.*
import kotlinx.android.synthetic.main.dialog_select_quantity.view.*

class NewMealActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    enum class RequestCode {SEARCH_RECIPE, EDIT_RECIPE, SEARCH_FOOD}

    private var mealId: Int = 0
    private var saved: Boolean? = null
    private var mealTimestamp: Long = TimeHandler.instance.currentTimeSecond()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_meal)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        newMealTimeDate.setOnClickListener {
            TimeHandler.instance.getDatePickerDialog(this, this, mealTimestamp).show(supportFragmentManager, "DatePickerDialog")
        }
        newMealTimeHour.setOnClickListener {
            TimeHandler.instance.getTimePickerDialog(this, this, mealTimestamp).show(supportFragmentManager, "TimePickerDialog")
        }
        addRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, RecipeActivity::class.java), RequestCode.SEARCH_RECIPE.ordinal)
        }
        recipeList.apply {
            layoutManager = LinearLayoutManager(this@NewMealActivity)
            adapter = MealRecipeAdapter { mealRecipeObject ->
                val intent = Intent(this@NewMealActivity, NewRecipeActivity::class.java)
                intent.putExtra(getString(R.string.param_mode), NewRecipeActivity.ActivityMode.MEAL_RECIPE)
                intent.putExtra(getString(R.string.param_recipe), mealRecipeObject)
                startActivityForResult(intent, RequestCode.EDIT_RECIPE.ordinal)
            }
            (adapter as MealRecipeAdapter).setVisibilityElements(recipeListEmptyLayout, recipeList)
            addItemDecoration(DividerItemDecorator(getDrawable(R.drawable.list_divider)!!))
        }
        addMealFoodButton.setOnClickListener {
            startActivityForResult(
                Intent(this, FoodActivity::class.java)
                    .putExtra(getString(R.string.param_food), (mealFoodList.adapter as RecipeFoodAdapter).getFoods()),
                RequestCode.SEARCH_FOOD.ordinal)
        }
        mealFoodList.apply {
            layoutManager = LinearLayoutManager(this@NewMealActivity)
            adapter = RecipeFoodAdapter {ingredientObject, textQuantity ->
                val view = layoutInflater.inflate(R.layout.dialog_select_quantity, null)
                MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
                val dialog = AlertDialog.Builder(this@NewMealActivity).setView(view).create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                view.selectQuantityInputLayout.hint = view.selectQuantityInputLayout.hint.toString() + " (" + ingredientObject.food.unit + ")"
                view.selectQuantityInput.setText(ingredientObject.quantity.toString())
                view.selectQuantityNegativeButton.setOnClickListener {
                    dialog.dismiss()
                }
                view.selectQuantityPositiveButton.setOnClickListener {
                    val quantity: Float? = view.selectQuantityInput.text.toString().toFloatOrNull()
                    if (quantity == null || quantity <= 0) {
                        view.selectQuantityInputLayout.error = getString(R.string.quantity_null)
                    } else {
                        saved = false
                        ingredientObject.quantity = quantity
                        textQuantity?.text = ingredientObject.quantity.toString() + " " + ingredientObject.food.unit
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
            (adapter as RecipeFoodAdapter).setVisibilityElements(mealFoodListEmptyLayout, mealFoodList)
            addItemDecoration(DividerItemDecorator(getDrawable(R.drawable.list_divider)!!))
        }
        saveNewMealButton.setOnClickListener {
            saveMeal()
        }
        closeNewMealButton.setOnClickListener {
            onBackPressed()
        }
        getParams()
    }

    private fun getParams() {
        if (intent.hasExtra(getString(R.string.param_meal))) {
            val meal = (intent.getSerializableExtra(getString(R.string.param_meal)) as MealObject)
            mealId = meal.id
            mealTimestamp = meal.timestamp
            if (mealId > 0 && meal.recipes.isNotEmpty()) {
                (recipeList.adapter as MealRecipeAdapter).setRecipes(meal.recipes)
            }
            if (mealId > 0 && meal.foods.isNotEmpty())
                (mealFoodList.adapter as RecipeFoodAdapter).setFoods(meal.foods)
        }
        TimeHandler.instance.updateTimeDisplay(this, mealTimestamp, newMealTimeDate, newMealTimeHour)
    }

    private fun saveMeal(finishView: Boolean = false) {
        val meal = getMeal()
        if (meal.recipes.isNullOrEmpty() && meal.foods.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.meal_empty), Toast.LENGTH_SHORT).show()
        } else {
            MealService.instance.createOrUpdateUserMeal(meal).doOnSuccess {
                if (it.second.component2() == null) {
                    saved = true
                    mealId = it.second.component1()?.id!!
                    Toast.makeText(this, R.string.saved_change, Toast.LENGTH_SHORT).show()
                    if (finishView) {
                        setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_meal), it.second.component1()))
                        finish()
                    }
                } else {
                    Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
            }.subscribe()
        }
    }

    private fun getMeal() : MealObject {
        val meal = MealObject(mealId, mealTimestamp, "", 0f,
            (recipeList.adapter as MealRecipeAdapter).getRecipes().toTypedArray(),
            (mealFoodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray())
        meal.calculateTotalSugar()
        return meal;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_RECIPE.ordinal) {
                saved = false
                val mealRecipe = MealRecipeObject(0f, data?.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
                (recipeList.adapter as MealRecipeAdapter).addRecipe(mealRecipe)
            } else if (requestCode == RequestCode.EDIT_RECIPE.ordinal) {
                saved = false
                (recipeList.adapter as MealRecipeAdapter).updateRecipe(data?.getSerializableExtra(getString(R.string.param_recipe)) as MealRecipeObject)
            } else if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                saved = false
                (mealFoodList.adapter as RecipeFoodAdapter).setFoods(data?.getSerializableExtra(getString(R.string.param_food)) as ArrayList<IngredientObject>)
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mealTimestamp = TimeHandler.instance.changeTimestampDate(mealTimestamp, year, monthOfYear, dayOfMonth)
        TimeHandler.instance.updateTimeDisplay(this, mealTimestamp, newMealTimeDate, newMealTimeHour)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        mealTimestamp = TimeHandler.instance.changeTimestampTime(mealTimestamp, hourOfDay, minute)
        TimeHandler.instance.updateTimeDisplay(this, mealTimestamp, newMealTimeDate, newMealTimeHour)
    }

    override fun onBackPressed() {
        if (saved == null) {
            finish()
        } else if (saved!!) {
            setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_meal), getMeal()))
            finish()
        } else {
            val view = layoutInflater.inflate(R.layout.dialog_save_change, null)
            MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
            val dialog = AlertDialog.Builder(this@NewMealActivity).setView(view).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            view.saveChangeNegativeButton.setOnClickListener {
                finish()
            }
            view.saveChangePositiveButton.setOnClickListener {
                saveMeal(true)
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}
