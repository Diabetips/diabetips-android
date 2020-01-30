package com.epitech.diabetips

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
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
class InstrumentedUnitTest {

    private lateinit var instrumentationContext: Context

    private val foodArray = arrayOf(FoodObject(1, "Food"), FoodObject(2), FoodObject(3))
    private val ingredientArray = arrayOf(IngredientObject(1, 1f, FoodObject(1, "Food")), IngredientObject(1, 2f, FoodObject(2, "Ingredient")), IngredientObject(1, 3f, FoodObject(3)))
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
    fun saveAndGetDarkMode() {
        val expectedMode =  AppCompatDelegate.MODE_NIGHT_YES
        ModeManager.instance.saveDarkMode(instrumentationContext, expectedMode)
        val actualMode =  ModeManager.instance.getDarkMode(instrumentationContext)
        assertEquals("Wrong dark mode.", expectedMode, actualMode)
    }

    @Test
    fun saveAndGetAccount() {
        val expectedAccount = AccountObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        AccountManager.instance.saveAccount(instrumentationContext, expectedAccount)
        val actualAccount =  AccountManager.instance.getAccount(instrumentationContext)
        assertEquals("Wrong account.", expectedAccount, actualAccount)
    }

    @Test
    fun removeAccount() {
        val expectedAccount = AccountObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        AccountManager.instance.saveAccount(instrumentationContext, expectedAccount)
        AccountManager.instance.removeAccount(instrumentationContext)
        val actualAccount =  AccountManager.instance.getAccount(instrumentationContext)
        assertNotEquals("Wrong account.", expectedAccount, actualAccount)
    }

    @Test
    fun foodAdapterSetFood() {
        val foodAdapter = FoodAdapter()
        foodAdapter.setFoods(foodArray)
        assertEquals(foodArray.size, foodAdapter.itemCount)
    }

    @Test
    fun foodAdapterAddFood() {
        val foodAdapter = FoodAdapter()
        foodAdapter.addFoods(foodArray)
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
    fun recipeAdapterAddRecipes() {
        val recipeAdapter = RecipeAdapter()
        recipeAdapter.addRecipes(recipeArray)
        assertEquals(recipeArray.size, recipeAdapter.itemCount)
    }

    @Test
    fun recipeFoodAdapterSetFoods() {
        val recipeFoodAdapter = RecipeFoodAdapter()
        recipeFoodAdapter.setFoods(ingredientArray)
        assertArrayEquals(ingredientArray, recipeFoodAdapter.getFoods().toTypedArray())
    }

    @Test
    fun recipeFoodAdapterAddFood() {
        val recipeFoodAdapter = RecipeFoodAdapter()
        recipeFoodAdapter.addFood(ingredientArray[0])
        assertEquals(ingredientArray[0], recipeFoodAdapter.getFoods()[0])
    }

    @Test
    fun mealAdapterSetMeals() {
        val mealAdapter = MealAdapter()
        mealAdapter.setMeals(mealArray)
        assertEquals(mealArray.size, mealAdapter.itemCount)
    }

    @Test
    fun mealAdapterAddMeal() {
        val mealAdapter = MealAdapter()
        mealAdapter.addMeal(mealArray[0])
        assertEquals(1, mealAdapter.itemCount)
    }

    @Test
    fun mealAdapterAddMeals() {
        val mealAdapter = MealAdapter()
        mealAdapter.addMeals(mealArray)
        assertEquals(mealArray.size, mealAdapter.itemCount)
    }

    @Test
    fun mealRecipeAdapterAddRecipes() {
        val mealRecipeAdapter = MealRecipeAdapter()
        mealRecipeAdapter.addRecipe(recipeArray[0])
        assertEquals(1, mealRecipeAdapter.itemCount)
    }

    @Test
    fun mealRecipeAdapterAddRecipe() {
        val mealRecipeAdapter = MealRecipeAdapter()
        mealRecipeAdapter.addRecipe(recipeArray[0])
        assertEquals(recipeArray[0], mealRecipeAdapter.getRecipes()[0])
    }
}
