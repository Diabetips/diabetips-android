package com.epitech.diabetips

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.epitech.diabetips.adapters.*
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.storages.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedFunctionalTest {

    private lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun saveAndGetAllManager() {
        //Expected values
        val expectedAccessToken = "49fad390491a5b547d0f782309b6a5b33f7ac087"
        val expectedRefreshToken = "8xLOxBtZp8"
        val expectedAccount = AccountObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        val expectedMode =  AppCompatDelegate.MODE_NIGHT_YES
        //Save values with managers
        AuthManager.instance.saveAccessToken(instrumentationContext, expectedAccessToken)
        AuthManager.instance.saveRefreshToken(instrumentationContext, expectedRefreshToken)
        AccountManager.instance.saveAccount(instrumentationContext, expectedAccount)
        ModeManager.instance.saveDarkMode(instrumentationContext, expectedMode)
        //Get values with managers
        val actualAccessToken = AuthManager.instance.getAccessToken(instrumentationContext)
        val actualRefreshToken = AuthManager.instance.getRefreshToken(instrumentationContext)
        val actualAccount =  AccountManager.instance.getAccount(instrumentationContext)
        val actualMode =  ModeManager.instance.getDarkMode(instrumentationContext)
        //Asserts
        assertEquals("Wrong access token.", expectedAccessToken, actualAccessToken)
        assertEquals("Wrong refresh token.", expectedRefreshToken, actualRefreshToken)
        assertEquals("Wrong account.", expectedAccount, actualAccount)
        assertEquals("Wrong dark mode.", expectedMode, actualMode)
    }

    @Test fun removeAllManager() {
        //Expected values
        val expectedAccessToken = "49fad390491a5b547d0f782309b6a5b33f7ac087"
        val expectedRefreshToken = "8xLOxBtZp8"
        val expectedAccount = AccountObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        val expectedMode =  AppCompatDelegate.MODE_NIGHT_YES
        //Save values with managers
        AuthManager.instance.saveAccessToken(instrumentationContext, expectedAccessToken)
        AuthManager.instance.saveRefreshToken(instrumentationContext, expectedRefreshToken)
        AccountManager.instance.saveAccount(instrumentationContext, expectedAccount)
        ModeManager.instance.saveDarkMode(instrumentationContext, expectedMode)
        //Remove values with managers
        AuthManager.instance.removePreferences(instrumentationContext)
        AccountManager.instance.removePreferences(instrumentationContext)
        ModeManager.instance.removePreferences(instrumentationContext)
        //Get values with managers
        val actualAccessToken = AuthManager.instance.removePreferences(instrumentationContext)
        val actualRefreshToken = AuthManager.instance.removePreferences(instrumentationContext)
        val actualAccount =  AccountManager.instance.removePreferences(instrumentationContext)
        val actualMode =  ModeManager.instance.removePreferences(instrumentationContext)
        //Asserts
        assertNotEquals("Wrong access token.", expectedAccessToken, actualAccessToken)
        assertNotEquals("Wrong refresh token.", expectedRefreshToken, actualRefreshToken)
        assertNotEquals("Wrong account.", expectedAccount, actualAccount)
        assertNotEquals("Wrong dark mode.", expectedMode, actualMode)
    }

    @Test
    fun foodAdapters() {
        //Values
        val foodArray = arrayOf(FoodObject(1, "Food"), FoodObject(2), FoodObject(3))
        val ingredientArray = arrayOf(IngredientObject(10f, 1f, FoodObject(1, "Food")), IngredientObject(15f, 2f, FoodObject(2, "Ingredient")), IngredientObject(7f, 3f, FoodObject(3)))
        val foodAdapter = FoodAdapter()
        val recipeFoodAdapter = RecipeFoodAdapter()
        //Operations
        foodAdapter.setFoods(foodArray)
        foodAdapter.addFoods(foodArray)
        recipeFoodAdapter.setFoods(ingredientArray)
        recipeFoodAdapter.addFood(ingredientArray[0])
        //Asserts
        assertEquals("Wrong food adapter", foodArray.size * 2, foodAdapter.itemCount)
        assertEquals("Wrong recipe food adapter", ingredientArray.size + 1, recipeFoodAdapter.itemCount)
    }

    @Test
    fun recipeAdapters() {
        //Values
        val recipeArray = arrayOf(RecipeObject(1, "Recipe"), RecipeObject(2), RecipeObject(3))
        val recipeAdapter = RecipeAdapter()
        val mealRecipeAdapter = MealRecipeAdapter()
        //Operations
        recipeAdapter.setRecipes(recipeArray)
        recipeAdapter.addRecipe(recipeArray[0])
        recipeAdapter.addRecipes(recipeArray)
        mealRecipeAdapter.setRecipes(recipeArray)
        mealRecipeAdapter.addRecipe(recipeArray[0])
        //Asserts
        assertEquals("Wrong recipe adapter", recipeArray.size * 2 + 1, recipeAdapter.itemCount)
        assertEquals("Wrong meal recipe adapter", recipeArray.size + 1, mealRecipeAdapter.itemCount)
    }

    @Test
    fun mealAdapters() {
        //Values
        val mealArray = arrayOf(MealObject(1, 0, "Meal"), MealObject(2), MealObject(3))
        val mealAdapter = MealAdapter()
        //Operations
        mealAdapter.setMeals(mealArray)
        mealAdapter.addMeal(mealArray[0])
        mealAdapter.addMeals(mealArray)
        //Asserts
        assertEquals("Wrong meal adapter", mealArray.size * 2 + 1, mealAdapter.itemCount)
    }
}
