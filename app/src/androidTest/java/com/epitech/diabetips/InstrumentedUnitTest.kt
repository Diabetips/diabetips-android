package com.epitech.diabetips

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.epitech.diabetips.adapters.*
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.storages.AccountObject
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.RecipeObject
import org.junit.Assert.assertArrayEquals
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
class InstrumentedUnitTest {

    private lateinit var instrumentationContext: Context

    private val foodArray = arrayOf(FoodObject(1, "Food"), FoodObject(2), FoodObject(3))
    private val recipeArray = arrayOf(RecipeObject(1, "Recipe"), RecipeObject(2), RecipeObject(3))
    private val mealArray = arrayOf(MealObject(1, Date(), "Meal"), MealObject(2), MealObject(3))

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun saveAndGetAccessToken() {
        val expectedToken = "49fad390491a5b547d0f782309b6a5b33f7ac087"
        AuthManager.instance.saveAccessToken(instrumentationContext, expectedToken)
        val actualToken = AuthManager.instance.getAccessToken(instrumentationContext)
        assertEquals("Wrong access token.", expectedToken, actualToken)
    }

    @Test
    fun saveAndGetRefreshToken() {
        val expectedToken = "8xLOxBtZp8"
        AuthManager.instance.saveRefreshToken(instrumentationContext, expectedToken)
        val actualToken = AuthManager.instance.getRefreshToken(instrumentationContext)
        assertEquals("Wrong refresh token.", expectedToken, actualToken)
    }

    @Test
    fun saveAndGetAccount() {
        val expectedAccount = AccountObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        AccountManager.instance.saveObject(instrumentationContext, expectedAccount)
        val actualAccount =  AccountManager.instance.getAccount(instrumentationContext)
        assertEquals("Wrong account.", expectedAccount, actualAccount)
    }

    @Test
    fun foodAdapterSetFood() {
        val foodAdapter = FoodAdapter()
        foodAdapter.setFoods(foodArray)
        assertEquals(foodArray.size, foodAdapter.itemCount)
    }

    @Test
    fun recipeAdapterSetRecipes() {
        val recipeAdapter = RecipeAdapter()
        recipeAdapter.setRecipes(recipeArray)
        assertEquals(recipeArray.size, recipeAdapter.itemCount)
    }

    @Test
    fun recipeAdapterAddRecipe() {
        val recipeAdapter = RecipeAdapter()
        recipeAdapter.addRecipe(recipeArray[0])
        assertEquals(1, recipeAdapter.itemCount)
    }

    @Test
    fun recipeFoodAdapterSetFoods() {
        val recipeFoodAdapter = RecipeFoodAdapter()
        recipeFoodAdapter.setFoods(foodArray)
        assertArrayEquals(foodArray, recipeFoodAdapter.getFoods().toTypedArray())
    }

    @Test
    fun recipeFoodAdapterAddFood() {
        val recipeFoodAdapter = RecipeFoodAdapter()
        recipeFoodAdapter.addFood(foodArray[0])
        assertEquals(foodArray[0], recipeFoodAdapter.getFoods()[0])
    }

    @Test
    fun mealAdapterSetRecipe() {
        val mealAdapter = MealAdapter()
        mealAdapter.setMeals(mealArray)
        assertEquals(mealArray.size, mealAdapter.itemCount)
    }

    @Test
    fun mealAdapterAddRecipe() {
        val mealRecipeAdapter = MealRecipeAdapter()
        mealRecipeAdapter.setRecipes(recipeArray)
        assertArrayEquals(recipeArray, mealRecipeAdapter.getRecipes().toTypedArray())
    }

    @Test
    fun mealRecipeAdapterAddRecipe() {
        val mealRecipeAdapter = MealRecipeAdapter()
        mealRecipeAdapter.addRecipe(recipeArray[0])
        assertEquals(recipeArray[0], mealRecipeAdapter.getRecipes()[0])
    }
}
