package com.epitech.diabetips.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.drawToBitmap
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.RecipeFoodAdapter
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.MealRecipeObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.textWatchers.InputWatcher
import com.epitech.diabetips.utils.DividerItemDecorator
import com.epitech.diabetips.utils.ImageHandler
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.dialog_change_picture.view.*
import kotlinx.android.synthetic.main.dialog_save_change.view.*
import kotlinx.android.synthetic.main.dialog_select_quantity.view.*
import java.io.InputStream

class NewRecipeActivity : AppCompatActivity() {

    enum class ActivityMode {RECIPE, MEAL_RECIPE}
    enum class RequestCode {SEARCH_FOOD, GET_IMAGE, GET_PHOTO}

    private var saved: Boolean? = null
    private var changedPicture: Boolean = false
    private var activityMode: ActivityMode = ActivityMode.RECIPE
    private var recipe = RecipeObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        newRecipeName.addTextChangedListener(InputWatcher(this, newRecipeNameLayout, true, R.string.recipe_name_empty))
        newRecipeName.addTextChangedListener {
            saved = false
        }
        newRecipeDescription.addTextChangedListener {
            saved = false
        }
        imagePhotoRecipe.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_change_picture, null)
            MaterialHandler.instance.handleTextInputLayoutSize(dialogView as ViewGroup)
            val dialog = AlertDialog.Builder(this).setView(dialogView).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogView.newPictureButton.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, RequestCode.GET_PHOTO.ordinal)
                dialog.dismiss()
            }
            dialogView.pictureGalleryButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, RequestCode.GET_IMAGE.ordinal)
                dialog.dismiss()
            }
            dialog.show()
        }
        addFoodButton.setOnClickListener {
            startActivityForResult(
                Intent(this, FoodActivity::class.java)
                    .putExtra(getString(R.string.param_food), (foodList.adapter as RecipeFoodAdapter).getFoods()),
                RequestCode.SEARCH_FOOD.ordinal)
        }
        foodList.apply {
            layoutManager = LinearLayoutManager(this@NewRecipeActivity)
            adapter = RecipeFoodAdapter {ingredientObject, textQuantity ->
                val view = layoutInflater.inflate(R.layout.dialog_select_quantity, null)
                MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
                val dialog = AlertDialog.Builder(this@NewRecipeActivity).setView(view).create()
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
            (adapter as RecipeFoodAdapter).setVisibilityElements(foodListEmptyLayout, foodList)
            addItemDecoration(DividerItemDecorator(getDrawable(R.drawable.list_divider)!!))
        }
        saveNewRecipeButton.setOnClickListener {
            saveRecipe()
        }
        closeNewRecipeButton.setOnClickListener {
            onBackPressed()
        }
        getParams()
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
                (foodList.adapter as RecipeFoodAdapter).setFoods(mealRecipe.getIngredients())
            } else {
                recipe = (intent.getSerializableExtra(getString(R.string.param_recipe)) as RecipeObject)
                if (recipe.ingredients.isNotEmpty()) {
                    (foodList.adapter as RecipeFoodAdapter).setFoods(recipe.ingredients)
                }
            }
            ImageHandler.instance.loadImage(imagePhotoRecipe as ImageView,this, RecipeService.instance.getRecipePictureUrl(recipe.id), R.drawable.ic_unknown, false)
            newRecipeName.setText(recipe.name)
            newRecipeDescription.setText(recipe.description)
            saved = null
        }
    }

    private fun saveRecipe(finishView: Boolean = false) {
        if (validateFields()) {
            if (activityMode == ActivityMode.RECIPE) {
                RecipeService.instance.createOrUpdateRecipe(getRecipe()).doOnSuccess {
                    if (it.second.component2() == null) {
                        saved = true
                        recipe = it.second.component1()!!
                        if (changedPicture) {
                            saveRecipePicture(finishView)
                        } else {
                            Toast.makeText(this, R.string.saved_change, Toast.LENGTH_SHORT).show()
                            if (finishView) {
                                endActivity()
                            }
                        }
                    }
                }.subscribe()
            } else {
                endActivity()
            }
        }
    }

    private fun getMealRecipe() : MealRecipeObject {
        val ingredients = (foodList.adapter as RecipeFoodAdapter).getFoods()
        recipe.ingredients.forEach { ingredient ->
            if (ingredients.find { it.food.id == ingredient.food.id } == null) {
                ingredients.add(IngredientObject(0f, 0f, ingredient.food))
            }
        }
        val mealRecipe = MealRecipeObject(0f, recipe, ingredients.toTypedArray())
        mealRecipe.calculateTotalSugar()
        return mealRecipe
    }

    private fun getRecipe(): RecipeObject {
        recipe.name = newRecipeName.text.toString()
        recipe.description = newRecipeDescription.text.toString()
        recipe.ingredients = (foodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray()
        recipe.calculateTotalSugar()
        return recipe
    }

    private fun validateFields() : Boolean {
        newRecipeName.text = newRecipeName.text
        var error = newRecipeNameLayout.error != null
        if ((foodList.adapter as RecipeFoodAdapter).getFoods().isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.recipe_empty), Toast.LENGTH_SHORT).show()
            error = true
        }
        return !error
    }

    private fun saveRecipePicture(finishView: Boolean = false) {
        val image: Bitmap = imagePhotoRecipe.drawToBitmap()
            RecipeService.instance.updateRecipePicture(image, recipe.id).doOnSuccess {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                saved = false
                (foodList.adapter as RecipeFoodAdapter).setFoods(data?.getSerializableExtra(getString(R.string.param_food)) as ArrayList<IngredientObject>)
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
            val view = layoutInflater.inflate(R.layout.dialog_save_change, null)
            MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
            val dialog = AlertDialog.Builder(this@NewRecipeActivity).setView(view).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            view.saveChangeNegativeButton.setOnClickListener {
                finish()
            }
            view.saveChangePositiveButton.setOnClickListener {
                if (saved == false || recipe.id <= 0) {
                    saveRecipe(true)
                } else {
                    saveRecipePicture(true)
                }
                dialog.dismiss()
            }
            dialog.show()
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
