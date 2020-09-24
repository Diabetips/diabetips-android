package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.NutritionalAdapter
import com.epitech.diabetips.adapters.RecipeFoodAdapter
import com.epitech.diabetips.managers.FavoriteManager
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.MealRecipeObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.textWatchers.InputWatcher
import com.epitech.diabetips.textWatchers.NumberWatcher
import com.epitech.diabetips.utils.*
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.dialog_change_picture.view.*
import java.io.InputStream

class NewRecipeActivity : ADiabetipsActivity(R.layout.activity_new_recipe) {

    private var saved: Boolean? = null
    private var changedPicture: Boolean = false
    private var activityMode: ActivityMode = ActivityMode.RECIPE
    private var recipe = RecipeObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newRecipeName.addTextChangedListener(InputWatcher(this, newRecipeNameLayout, true, R.string.recipe_name_empty))
        newRecipePortion.addTextChangedListener(NumberWatcher(this, newRecipePortionLayout, R.string.recipe_portion_empty, 0.0001f))
        newRecipeName.addTextChangedListener {
            saved = false
        }
        newRecipeDescription.addTextChangedListener {
            saved = false
        }
        newRecipePortion.addTextChangedListener {
            saved = false
            newRecipeNutritionPortion.setText(it.toString())
        }
        newRecipeNutritionPortion.addTextChangedListener {
            updateNutritionalValueDisplay()
        }
        imagePhotoRecipe.setOnClickListener {
            DialogHandler.dialogChangePicture(this, layoutInflater, this) {
                if (recipe.id > 0) {
                    RecipeService.instance.removePicture<RecipeObject>(recipe.id).doOnSuccess {
                        if (it.second.component2() == null) {
                            changedPicture = false
                            loadImage()
                            Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }.subscribe()
                } else if (changedPicture && recipe.id == 0) {
                    changedPicture = false
                    loadImage()
                }
            }
        }
        addFoodButton.setOnClickListener {
            startActivityForResult(
                Intent(this, FoodActivity::class.java)
                    .putExtra(getString(R.string.param_food), (foodList.adapter as RecipeFoodAdapter).getFoods()),
                RequestCode.SEARCH_FOOD.ordinal)
        }
        newRecipeToggleContent.setOnClickListener {
            changeDisplayMode(DisplayMode.CONTENT)
        }
        newRecipeToggleNutrition.setOnClickListener {
            changeDisplayMode(DisplayMode.NUTRITION)
        }
        foodList.apply {
            layoutManager = LinearLayoutManager(this@NewRecipeActivity)
            adapter = RecipeFoodAdapter {ingredientObject, textQuantity ->
                DialogHandler.dialogSelectQuantity(this@NewRecipeActivity, layoutInflater, ingredientObject) {
                    saved = false
                    textQuantity?.text = "${ingredientObject.quantity} ${ingredientObject.food.unit}"
                    updateNutritionalValueDisplay()
                }
            }
            (adapter as RecipeFoodAdapter).setVisibilityElements(foodListEmptyLayout, foodList)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@NewRecipeActivity, R.drawable.list_divider)!!))
        }
        newRecipeNutritionalList.apply {
            layoutManager = LinearLayoutManager(this@NewRecipeActivity)
            adapter = NutritionalAdapter()
        }
        saveNewRecipeButton.setOnClickListener {
            saveRecipe()
        }
        validateNewRecipeButton.setOnClickListener {
            saveRecipe(true)
        }
        closeNewRecipeButton.setOnClickListener {
            onBackPressed()
        }
        getParams()
        changeDisplayMode(DisplayMode.CONTENT)
    }

    private fun getParams() {
        if (intent.hasExtra(getString(R.string.param_mode))) {
            activityMode = (intent.getSerializableExtra(getString(R.string.param_mode)) as ActivityMode)
            if (activityMode == ActivityMode.MEAL_RECIPE) {
                newRecipeName.isEnabled = false
                newRecipeDescription.isEnabled = false
            }
        }
        if (intent.hasExtra(getString(R.string.param_recipe))) {
            if (activityMode == ActivityMode.MEAL_RECIPE) {
                val mealRecipe = (intent.getSerializableExtra(getString(R.string.param_recipe)) as MealRecipeObject)
                recipe = mealRecipe.recipe
                newRecipeName.setText(recipe.name)
                newRecipeDescription.setText(recipe.description)
                (foodList.adapter as RecipeFoodAdapter).setFoods(mealRecipe.getIngredients())
                newRecipePortion.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                newRecipePortionLayout.suffixText = "/ ${recipe.portions.toInt()}"
                newRecipePortion.setText(mealRecipe.portions_eaten.toString())
            } else {
                recipe = (intent.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
                newRecipeName.setText(recipe.name)
                newRecipeDescription.setText(recipe.description)
                if (recipe.ingredients.isNotEmpty()) {
                    (foodList.adapter as RecipeFoodAdapter).setFoods(recipe.ingredients)
                }
                newRecipePortion.setText(recipe.portions.toInt().toString())
            }
            loadImage()
            saved = null
        } else {
            newRecipePortion.setText("1")
        }
        handleRecipeFavoriteButton()
    }

    private fun updateNutritionalValueDisplay() {
        val portions = newRecipeNutritionPortion.text.toString().toFloatOrNull() ?: 0f
        if (activityMode == ActivityMode.RECIPE) {
            val recipe = getRecipe()
            (newRecipeNutritionalList.adapter as NutritionalAdapter).setNutritions(recipe.getNutritionalValues(portions), recipe.getQuantity(portions))
        } else {
            val mealRecipe = getMealRecipe()
            (newRecipeNutritionalList.adapter as NutritionalAdapter).setNutritions(mealRecipe.getNutritionalValues(portions), mealRecipe.getQuantity(portions))
        }
    }

    private fun saveRecipe(finishView: Boolean = false) {
        if (validateFields()) {
            if (activityMode == ActivityMode.RECIPE) {
                RecipeService.instance.createOrUpdate(getRecipe(), recipe.id).doOnSuccess {
                    if (it.second.component2() == null) {
                        saved = true
                        recipe = it.second.component1()!!
                        handleRecipeFavoriteButton()
                        if (changedPicture) {
                            saveRecipePicture(finishView)
                        } else {
                            Toast.makeText(this, R.string.saved_change, Toast.LENGTH_SHORT).show()
                            if (finishView) {
                                endActivity()
                            }
                        }
                    } else {
                        Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.subscribe()
            } else if (changedPicture) {
                saveRecipePicture(true)
            } else {
                endActivity()
            }
        }
    }

    private fun getMealRecipe() : MealRecipeObject {
        val ingredients = (foodList.adapter as RecipeFoodAdapter).getFoods()
        recipe.ingredients.forEach { ingredient ->
            if (ingredients.find { it.food.id == ingredient.food.id } == null) {
                ingredients.add(IngredientObject(quantity = 0f, food = ingredient.food))
            }
        }
        val mealRecipe = MealRecipeObject(
            portions_eaten = newRecipePortion.text.toString().toFloatOrNull() ?: recipe.portions,
            recipe = recipe,
            modifications = ingredients.toTypedArray())
        mealRecipe.calculateTotalSugars()
        return mealRecipe
    }

    private fun getRecipe(): RecipeObject {
        recipe.name = newRecipeName.text.toString()
        recipe.description = newRecipeDescription.text.toString()
        recipe.portions = newRecipePortion.text.toString().toFloatOrNull() ?: 1f
        recipe.ingredients = (foodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray()
        recipe.calculateTotalSugars()
        return recipe
    }

    private fun validateFields() : Boolean {
        newRecipeName.text = newRecipeName.text
        newRecipePortion.text = newRecipePortion.text
        var error = newRecipeNameLayout.error != null || newRecipePortionLayout.error != null
        if ((foodList.adapter as RecipeFoodAdapter).getFoods().isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.recipe_empty), Toast.LENGTH_SHORT).show()
            error = true
        }
        return !error
    }

    private fun handleRecipeFavoriteButton() {
        if (recipe.id > 0) {
            newRecipeFavoriteButton.visibility = View.VISIBLE
            FavoriteManager.instance.handleImageButton(recipe.id, newRecipeFavoriteButton)
        } else {
            newRecipeFavoriteButton.visibility = View.GONE
        }
    }

    private fun changeDisplayMode(displayMode: DisplayMode = DisplayMode.CONTENT) {
        newRecipeToggleContent.isChecked = (displayMode == DisplayMode.CONTENT)
        newRecipeToggleNutrition.isChecked = (displayMode == DisplayMode.NUTRITION)
        newRecipeContentLayout.visibility = if (displayMode == DisplayMode.CONTENT) View.VISIBLE else View.GONE
        newRecipeNutritionLayout.visibility = if (displayMode == DisplayMode.NUTRITION) View.VISIBLE else View.GONE
    }

    private fun saveRecipePicture(finishView: Boolean = false) {
        val image: Bitmap = imagePhotoRecipe.drawToBitmap()
        RecipeService.instance.updatePicture<RecipeObject>(image, recipe.id).doOnSuccess {
            if (it.second.component2() == null) {
                changedPicture = false
                Toast.makeText(this, R.string.saved_change, Toast.LENGTH_SHORT).show()
                if (finishView) {
                    endActivity()
                }
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    private fun setRecipePicture(image: Bitmap) {
        imagePhotoRecipe.setImageBitmap(image)
        changedPicture = true
    }

    private fun loadImage() {
        ImageHandler.instance.loadImage(imagePhotoRecipe as ImageView,this, RecipeService.instance.getPictureUrl(recipe.id), R.drawable.ic_unknown, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                saved = false
                (foodList.adapter as RecipeFoodAdapter).setFoods(ArrayList((data?.getSerializableExtra(getString(R.string.param_food)) as ArrayList<*>).filterIsInstance<IngredientObject>()))
                updateNutritionalValueDisplay()
            } else if (requestCode == RequestCode.GET_IMAGE.ordinal) {
                val imageStream: InputStream? = contentResolver.openInputStream(data?.data!!)
                setRecipePicture(BitmapFactory.decodeStream(imageStream))
            } else if (requestCode == RequestCode.GET_PHOTO.ordinal) {
                setRecipePicture(data?.extras?.get("data") as Bitmap)
            }
        }
    }

    override fun onBackPressed() {
        if (saved == null && !changedPicture) {
            finish()
        } else if (saved == true && !changedPicture) {
            endActivity()
        } else {
            DialogHandler.dialogSaveChange(this, layoutInflater, {
                if (saved == false || recipe.id <= 0) {
                    saveRecipe(true)
                } else {
                    saveRecipePicture(true)
                }
            }, { finish() })
        }
    }

    private fun endActivity() {
        if (activityMode == ActivityMode.RECIPE) {
            setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_recipe), recipe))
        } else {
            setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_recipe), getMealRecipe()))
        }
        finish()
    }

}
