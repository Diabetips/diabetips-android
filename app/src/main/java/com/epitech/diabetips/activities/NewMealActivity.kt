package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.MealRecipeAdapter
import com.epitech.diabetips.adapters.NutritionalAdapter
import com.epitech.diabetips.adapters.RecipeFoodAdapter
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.MealRecipeObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.textWatchers.NumberWatcher
import com.epitech.diabetips.textWatchers.TextChangedWatcher
import com.epitech.diabetips.utils.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_new_meal.*
import kotlinx.android.synthetic.main.dialog_hba1c.view.*
import kotlinx.android.synthetic.main.dialog_save_change.view.*
import kotlinx.android.synthetic.main.dialog_select_quantity.view.*

class NewMealActivity : ADiabetipsActivity(R.layout.activity_new_meal), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    enum class RequestCode {SEARCH_RECIPE, EDIT_RECIPE, SEARCH_FOOD}

    private var mealId: Int = 0
    private var saved: Boolean? = null
    private var mealTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mealTime = TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api))
        newMealTimeDate.setOnClickListener {
            TimeHandler.instance.getDatePickerDialog(this, this, mealTime).show(supportFragmentManager, "DatePickerDialog")
        }
        newMealTimeHour.setOnClickListener {
            TimeHandler.instance.getTimePickerDialog(this, this, mealTime).show(supportFragmentManager, "TimePickerDialog")
        }
        addRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, RecipeActivity::class.java)
                .putExtra(getString(R.string.param_mode), IRecipe.ActivityMode.SELECT),
                RequestCode.SEARCH_RECIPE.ordinal)
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
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@NewMealActivity, R.drawable.list_divider)!!))
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
                view.selectQuantityInputLayout.hint = "${view.selectQuantityInputLayout.hint} (${ingredientObject.food.unit})"
                view.selectQuantityInput.setText(ingredientObject.quantity.toBigDecimal().stripTrailingZeros().toPlainString())
                view.selectQuantityNutritionalList.apply {
                    layoutManager = LinearLayoutManager(this@NewMealActivity)
                    adapter = NutritionalAdapter(ingredientObject.getNutritionalValues(), ingredientObject.quantity)
                }
                view.selectQuantityInput.addTextChangedListener(NumberWatcher(this@NewMealActivity, view.selectQuantityInputLayout, R.string.quantity_null, 0f))
                view.selectQuantityInput.addTextChangedListener(TextChangedWatcher {
                    val quantity = it.toString().toFloatOrNull() ?: 0f
                    (view.selectQuantityNutritionalList.adapter as NutritionalAdapter).setNutritions(ingredientObject.getNutritionalValues(quantity), quantity)
                })
                view.selectQuantityNegativeButton.setOnClickListener {
                    dialog.dismiss()
                }
                view.selectQuantityPositiveButton.setOnClickListener {
                    view.selectQuantityInput.text = view.selectQuantityInput.text
                    if (view.selectQuantityInputLayout.error == null) {
                        saved = false
                        ingredientObject.quantity = view.selectQuantityInput.text.toString().toFloatOrNull() ?: 0f
                        textQuantity?.text = "${ingredientObject.quantity} ${ingredientObject.food.unit}"
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
            (adapter as RecipeFoodAdapter).setVisibilityElements(mealFoodListEmptyLayout, mealFoodList)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@NewMealActivity, R.drawable.list_divider)!!))
        }
        saveNewMealButton.setOnClickListener {
            saveMeal()
        }
        validateNewMealButton.setOnClickListener {
            saveMeal(true)
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
            mealTime = meal.time
            if (mealId > 0 && meal.recipes.isNotEmpty()) {
                (recipeList.adapter as MealRecipeAdapter).setRecipes(meal.recipes)
            }
            if (mealId > 0 && meal.foods.isNotEmpty())
                (mealFoodList.adapter as RecipeFoodAdapter).setFoods(meal.foods)
        }
        TimeHandler.instance.updateTimeDisplay(this, mealTime, newMealTimeDate, newMealTimeHour)
    }

    private fun saveMeal(finishView: Boolean = false) {
        val meal = getMeal()
        if (meal.recipes.isNullOrEmpty() && meal.foods.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.meal_empty), Toast.LENGTH_SHORT).show()
        } else {
            MealService.instance.createOrUpdate(meal, meal.id).doOnSuccess {
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
        val meal = MealObject(mealId, mealTime, "", 0f,
            (recipeList.adapter as MealRecipeAdapter).getRecipes().toTypedArray(),
            (mealFoodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray())
        meal.calculateTotalSugar()
        return meal
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_RECIPE.ordinal) {
                saved = false
                val recipe = data?.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject
                val mealRecipe = MealRecipeObject(0f, recipe.portions, recipe)
                (recipeList.adapter as MealRecipeAdapter).addRecipe(mealRecipe)
            } else if (requestCode == RequestCode.EDIT_RECIPE.ordinal) {
                saved = false
                (recipeList.adapter as MealRecipeAdapter).updateRecipe(data?.getSerializableExtra(getString(R.string.param_recipe)) as MealRecipeObject)
            } else if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                saved = false
                (mealFoodList.adapter as RecipeFoodAdapter).setFoods(ArrayList((data?.getSerializableExtra(getString(R.string.param_food)) as ArrayList<*>).filterIsInstance<IngredientObject>()))
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mealTime = TimeHandler.instance.changeFormatDate(mealTime, getString(R.string.format_time_api), year, monthOfYear, dayOfMonth)
        TimeHandler.instance.updateTimeDisplay(this, mealTime, newMealTimeDate, newMealTimeHour)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        mealTime = TimeHandler.instance.changeFormatTime(mealTime, getString(R.string.format_time_api), hourOfDay, minute)
        TimeHandler.instance.updateTimeDisplay(this, mealTime, newMealTimeDate, newMealTimeHour)
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
