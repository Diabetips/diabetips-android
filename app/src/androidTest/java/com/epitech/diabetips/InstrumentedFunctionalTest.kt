package com.epitech.diabetips

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.epitech.diabetips.adapters.*
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.storages.AccountObject
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.RecipeObject
import org.junit.Assert
import org.junit.Assert.assertEquals
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
        //Save values with managers
        AuthManager.instance.saveAccessToken(instrumentationContext, expectedAccessToken)
        AuthManager.instance.saveRefreshToken(instrumentationContext, expectedRefreshToken)
        AccountManager.instance.saveObject(instrumentationContext, expectedAccount)
        //Get values with managers
        val actualAccessToken = AuthManager.instance.getAccessToken(instrumentationContext)
        val actualRefreshToken = AuthManager.instance.getRefreshToken(instrumentationContext)
        val actualAccount =  AccountManager.instance.getAccount(instrumentationContext)
        //Asserts
        assertEquals("Wrong access token.", expectedAccessToken, actualAccessToken)
        assertEquals("Wrong refresh token.", expectedRefreshToken, actualRefreshToken)
        assertEquals("Wrong account.", expectedAccount, actualAccount)
    }

    @Test
    fun foodAdapters() {
        //Values
        val foodArray = arrayOf(FoodObject(1, "Food"), FoodObject(2), FoodObject(3))
        val foodAdapter = FoodAdapter()
        val recipeFoodAdapter = RecipeFoodAdapter()
        //Operations
        foodAdapter.setFoods(foodArray)
        recipeFoodAdapter.setFoods(foodArray)
        recipeFoodAdapter.addFood(foodArray[0])
        //Asserts
        assertEquals(foodArray.size, foodAdapter.itemCount)
        assertEquals(foodArray.size + 1, recipeFoodAdapter.itemCount)
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
        mealRecipeAdapter.setRecipes(recipeArray)
        mealRecipeAdapter.addRecipe(recipeArray[0])
        //Asserts
        assertEquals(recipeArray.size + 1, recipeAdapter.itemCount)
        assertEquals(recipeArray.size + 1, mealRecipeAdapter.itemCount)
    }
}
