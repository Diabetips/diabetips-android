package com.epitech.diabetips.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.RecipeFoodAdapter
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.textWatchers.InputWatcher
import com.epitech.diabetips.utils.DividerItemDecorator
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.dialog_save_change.view.*
import kotlinx.android.synthetic.main.dialog_select_quantity.view.*

class NewRecipeActivity : AppCompatActivity() {

    enum class RequestCode {SEARCH_FOOD}

    private var recipeId: Int = 0
    private var saved: Boolean? = null

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
            if (saved == null) {
                finish()
            } else if (saved!!) {
                setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_recipe), getRecipe()))
            } else {
                val view = layoutInflater.inflate(R.layout.dialog_save_change, null)
                MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
                val dialog = AlertDialog.Builder(this@NewRecipeActivity).setView(view).create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                view.saveChangeNegativeButton.setOnClickListener {
                    finish()
                }
                view.saveChangePositiveButton.setOnClickListener {
                    saveRecipe(true)
                    dialog.dismiss()
                }
                dialog.show()
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

    private fun saveRecipe(finishView: Boolean = false) {
        if (validateFields()) {
            val recipe = getRecipe()
            RecipeService.instance.createOrUpdateRecipe(recipe).doOnSuccess {
                if (it.second.component2() == null) {
                    saved = true
                    recipeId = it.second.component1()?.id!!
                    if (finishView) {
                        setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_recipe), it.second.component1()))
                        finish()
                    }
                }
            }.subscribe()
        }
    }

    private fun getRecipe(): RecipeObject {
        return RecipeObject(
            recipeId,
            newRecipeName.text.toString(),
            newRecipeDescription.text.toString(),
            0f,
            (foodList.adapter as RecipeFoodAdapter).getFoods().toTypedArray()
        )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.SEARCH_FOOD.ordinal) {
                saved = false
                (foodList.adapter as RecipeFoodAdapter).setFoods(data?.getSerializableExtra(getString(R.string.param_food)) as ArrayList<IngredientObject>)
            }
        }
    }

}
