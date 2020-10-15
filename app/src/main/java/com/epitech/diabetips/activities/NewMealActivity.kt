package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.MealRecipeAdapter
import com.epitech.diabetips.adapters.NutritionalAdapter
import com.epitech.diabetips.adapters.RecipeFoodAdapter
import com.epitech.diabetips.services.MealService
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.utils.*
import kotlinx.android.synthetic.main.activity_new_meal.*

class NewMealActivity : ADiabetipsActivity(R.layout.activity_new_meal) {

    private var mealId: Int = 0
    private var saved: Boolean? = null
    private var mealTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mealTime = TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api))
        newMealTimeDate.setOnClickListener {
            DialogHandler.datePickerDialog(this, supportFragmentManager, mealTime, newMealTimeDate) { time ->
                mealTime = time
            }
        }
        newMealTimeHour.setOnClickListener {
            DialogHandler.timePickerDialog(this, supportFragmentManager, mealTime, newMealTimeHour) { time ->
                mealTime = time
            }
        }
        addRecipeButton.setOnClickListener {
            startActivityForResult(Intent(this, RecipeActivity::class.java)
                .putExtra(getString(R.string.param_mode), ActivityMode.SELECT),
                RequestCode.SEARCH_RECIPE.ordinal)
        }
        newMealToggleContent.setOnClickListener {
            changeDisplayMode(DisplayMode.CONTENT)
        }
        newMealToggleNutrition.setOnClickListener {
            changeDisplayMode(DisplayMode.NUTRITION)
        }
        recipeList.apply {
            layoutManager = LinearLayoutManager(this@NewMealActivity)
            adapter = MealRecipeAdapter (onItemClickListener = { mealRecipeObject ->
                val intent = Intent(this@NewMealActivity, NewRecipeActivity::class.java)
                intent.putExtra(getString(R.string.param_mode), ActivityMode.MEAL_RECIPE)
                intent.putExtra(getString(R.string.param_recipe), mealRecipeObject)
                startActivityForResult(intent, RequestCode.UPDATE_RECIPE.ordinal)
            }, onItemRemovedListener =  { updateNutritionalValueDisplay() })
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
            adapter = RecipeFoodAdapter (onItemClickListener = { ingredientObject, textQuantity ->
                DialogHandler.dialogSelectQuantity(this@NewMealActivity, layoutInflater, ingredientObject) {
                    saved = false
                    textQuantity?.text = "${ingredientObject.quantity} ${ingredientObject.food.unit}"
                    updateNutritionalValueDisplay()
                }
            }, onItemRemovedListener =  { updateNutritionalValueDisplay() })
            (adapter as RecipeFoodAdapter).setVisibilityElements(mealFoodListEmptyLayout, mealFoodList)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@NewMealActivity, R.drawable.list_divider)!!))
        }
        newMealNutritionalList.apply {
            layoutManager = LinearLayoutManager(this@NewMealActivity)
            adapter = NutritionalAdapter()
        }
        saveNewMealButton.setOnClickListener {
            saveMeal()
        }
        validateNewMealButton.setOnClickListener {
            saveMeal(true)
        }
        deleteNewMealButton.setOnClickListener {
            DialogHandler.dialogConfirm(this, layoutInflater, R.string.meal_delete) {
                MealService.instance.remove<MealObject>(mealId).doOnSuccess {
                    if (it.second.component2() == null) {
                        Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK, Intent())
                        finish()
                    } else {
                        Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.subscribe()
            }
        }
        closeNewMealButton.setOnClickListener {
            onBackPressed()
        }
        getParams()
        changeDisplayMode(DisplayMode.CONTENT)
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
        deleteNewMealButton.visibility = if (mealId > 0) View.VISIBLE else View.GONE
        TimeHandler.instance.updateTimeDisplay(this, mealTime, newMealTimeDate, newMealTimeHour)
        updateNutritionalValueDisplay()
    }

    private fun updateNutritionalValueDisplay() {
        val meal = getMeal()
        (newMealNutritionalList.adapter as NutritionalAdapter).setNutritions(meal.getNutritionalValues(), meal.getQuantity())
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
        val meal = MealObject(mealId, mealTime, "",
            recipes = (recipeList.adapter as MealRecipeAdapter).getRecipes().toTypedArray(),
            foods = (mealFoodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray())
        meal.calculateTotalSugars()
        return meal
    }

    private fun changeDisplayMode(displayMode: DisplayMode = DisplayMode.CONTENT) {
        newMealToggleContent.isChecked = (displayMode == DisplayMode.CONTENT)
        newMealToggleNutrition.isChecked = (displayMode == DisplayMode.NUTRITION)
        newMealContentLayout.visibility = if (displayMode == DisplayMode.CONTENT) View.VISIBLE else View.GONE
        newMealNutritionLayout.visibility = if (displayMode == DisplayMode.NUTRITION) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_RECIPE.ordinal) {
                saved = false
                val recipe = data?.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject
                val mealRecipe = MealRecipeObject(portions_eaten = recipe.portions, recipe = recipe)
                (recipeList.adapter as MealRecipeAdapter).addRecipe(mealRecipe)
                updateNutritionalValueDisplay()
            } else if (requestCode == RequestCode.UPDATE_RECIPE.ordinal) {
                saved = false
                (recipeList.adapter as MealRecipeAdapter).updateRecipe(data?.getSerializableExtra(getString(R.string.param_recipe)) as MealRecipeObject)
                updateNutritionalValueDisplay()
            } else if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                saved = false
                (mealFoodList.adapter as RecipeFoodAdapter).setFoods(ArrayList((data?.getSerializableExtra(getString(R.string.param_food)) as ArrayList<*>).filterIsInstance<IngredientObject>()))
                updateNutritionalValueDisplay()
            }
        }
    }

    override fun onBackPressed() {
        if (saved == null) {
            finish()
        } else if (saved!!) {
            setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_meal), getMeal()))
            finish()
        } else {
            DialogHandler.dialogSaveChange(this, layoutInflater, { saveMeal(true) }, { finish() })
        }
    }
}
