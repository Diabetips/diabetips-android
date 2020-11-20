package com.epitech.diabetips

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.epitech.diabetips.adapters.*
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.utils.TimeHandler
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedUnitTest {

    private lateinit var instrumentationContext: Context

    private var timeFormat: String = ""
    private var currentTime: String = ""
    private var currentTimestamp: Long = 0
    private val foodArray = arrayOf(FoodObject(1, "Food"), FoodObject(2), FoodObject(3))
    private val ingredientArray = arrayOf(IngredientObject(5f, food = FoodObject(1, "Food")), IngredientObject(1f, food = FoodObject(2, "Ingredient")), IngredientObject(8f, food = FoodObject(3)))
    private val recipeArray = arrayOf(RecipeObject(1, "Recipe"), RecipeObject(2), RecipeObject(3))
    private val mealRecipeArray = arrayOf(MealRecipeObject(0f, recipe = RecipeObject(1, "Recipe")), MealRecipeObject(0f, recipe = RecipeObject(2)), MealRecipeObject(0f, recipe = RecipeObject(3)))
    private val mealArray = arrayOf(MealObject(1, "", "Meal"), MealObject(2), MealObject(3))
    private val hba1cArray = arrayOf(HbA1cObject(1, 5.5f), HbA1cObject(2, 10f), HbA1cObject(3, 7f))

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        timeFormat = instrumentationContext.getString(R.string.format_time_api)
        currentTimestamp = TimeHandler.instance.currentTime()
        currentTime = TimeHandler.instance.formatTimestamp(currentTimestamp, timeFormat)
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
        val expectedAccount = UserObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        UserManager.instance.saveUser(instrumentationContext, expectedAccount)
        val actualAccount =  UserManager.instance.getUser(instrumentationContext)
        assertEquals("Wrong account.", expectedAccount, actualAccount)
    }

    @Test
    fun removeAccount() {
        val expectedAccount = UserObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        UserManager.instance.saveUser(instrumentationContext, expectedAccount)
        UserManager.instance.removeUser(instrumentationContext)
        val actualAccount =  UserManager.instance.getUser(instrumentationContext)
        assertNotEquals("Wrong account.", expectedAccount, actualAccount)
    }

    @Test
    fun saveAndGetBiometric() {
        val expectedBiometric = BiometricObject(80, 175, 60, 180, "2020-10-05", "male")
        UserManager.instance.saveBiometric(instrumentationContext, expectedBiometric)
        val actualBiometric =  UserManager.instance.getBiometric(instrumentationContext)
        assertEquals("Wrong account.", expectedBiometric, actualBiometric)
    }

    @Test
    fun removeBiometric() {
        val expectedBiometric = BiometricObject(80, 175, 60, 180, "2020-10-05", "male")
        UserManager.instance.saveBiometric(instrumentationContext, expectedBiometric)
        UserManager.instance.removeBiometric(instrumentationContext)
        val actualBiometric =  UserManager.instance.getBiometric(instrumentationContext)
        assertNotEquals("Wrong account.", expectedBiometric, actualBiometric)
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
    fun mealRecipeAdapterAddRecipes() {
        val mealRecipeAdapter = MealRecipeAdapter()
        mealRecipeAdapter.addRecipe(mealRecipeArray[0])
        assertEquals(1, mealRecipeAdapter.itemCount)
    }

    @Test
    fun mealRecipeAdapterAddRecipe() {
        val mealRecipeAdapter = MealRecipeAdapter()
        mealRecipeAdapter.addRecipe(mealRecipeArray[0])
        assertEquals(mealRecipeArray[0], mealRecipeAdapter.getRecipes()[0])
    }

    @Test
    fun hba1cAdapterSetHbA1c() {
        val hbA1cAdapter = HbA1cAdapter()
        hbA1cAdapter.setHbA1c(hba1cArray)
        assertEquals(hba1cArray.size, hbA1cAdapter.itemCount)
    }

    @Test
    fun hba1cAdapterAddHbA1c() {
        val hbA1cAdapter = HbA1cAdapter()
        hbA1cAdapter.addHbA1c(hba1cArray)
        assertEquals(hba1cArray.size, hbA1cAdapter.itemCount)
    }

    @Test
    fun biometricObjectSex() {
        val biometric = BiometricObject(80, 175, 60, 180, "2020-10-05")
        biometric.setSex(instrumentationContext, instrumentationContext.resources.getStringArray(R.array.sex)[0])
        assertEquals("Wrong sex", instrumentationContext.resources.getStringArray(R.array.sex)[0], biometric.getSex(instrumentationContext))
    }

    @Test
    fun biometricObjectDiabetesType() {
        val biometric = BiometricObject(80, 175, 60, 180, "2020-10-05")
        biometric.setDiabetesType(instrumentationContext, instrumentationContext.resources.getStringArray(R.array.diabetes_type)[0])
        assertEquals("Wrong diabetes type", instrumentationContext.resources.getStringArray(R.array.diabetes_type)[0], biometric.getDiabetesType(instrumentationContext))
    }

    @Test
    fun activityStart() {
        val activity = ActivityObject(start = currentTime, end = currentTime)
        activity.setStart(instrumentationContext, currentTime)
        assertEquals("Wrong activity start", activity.start, currentTime)
    }

    @Test
    fun activityDuration() {
        val activity = ActivityObject(start = currentTime, end = currentTime)
        activity.setDuration(instrumentationContext, TimeHandler.instance.formatTimestamp(TimeHandler.instance.addTimeToTimestamp(0, 90), timeFormat, true))
        assertEquals("Wrong activity end", activity.end, TimeHandler.instance.addTimeToFormat(currentTime, timeFormat, 90))
    }

    @Test
    fun activityDurationSecond() {
        val activity = ActivityObject(start = currentTime, end = currentTime)
        assertEquals("Wrong activity duration", activity.getDurationSecond(instrumentationContext), 0)
    }

    @Test
    fun timeHandlerTimestampToTime() {
        assertEquals("Wrong Timestamp to Time", currentTime, TimeHandler.instance.formatTimestamp(currentTimestamp, timeFormat))
    }

    @Test
    fun timeHandlerTimeToTimestamp() {
        assertEquals("Wrong Time to Timestamp", currentTimestamp, TimeHandler.instance.getTimestampFromFormat(currentTime, timeFormat))
    }

    @Test
    fun timeHandlerAddTime() {
        assertEquals("Wrong Add Time",
            TimeHandler.instance.addTimeToTimestamp(currentTimestamp, 90),
            TimeHandler.instance.getTimestampFromFormat(TimeHandler.instance.addTimeToFormat(currentTime, timeFormat, 90), timeFormat))
    }

    @Test
    fun timeHandlerChangeDate() {
        assertEquals("Wrong Change Date",
            TimeHandler.instance.changeTimestampDate(currentTimestamp, 2000, 9, 12),
            TimeHandler.instance.getTimestampFromFormat(TimeHandler.instance.changeFormatDate(currentTime, timeFormat, 2000, 9, 12), timeFormat))
    }

    @Test
    fun timeHandlerChangeTime() {
        assertEquals("Wrong Change Time",
            TimeHandler.instance.changeTimestampTime(currentTimestamp, 12, 30),
            TimeHandler.instance.getTimestampFromFormat(TimeHandler.instance.changeFormatTime(currentTime, timeFormat, 12, 30), timeFormat))
    }

    @Test
    fun timeHandlerSecondDiff() {
        assertEquals("Wrong Second Diff",
            TimeHandler.instance.getSecondDiffFormat(
                TimeHandler.instance.changeFormatTime(currentTime, timeFormat, 12, 0),
                TimeHandler.instance.changeFormatTime(currentTime, timeFormat, 14, 30), timeFormat),
            TimeHandler.instance.getSecondDiff(
                TimeHandler.instance.changeTimestampTime(currentTimestamp, 12, 0),
                TimeHandler.instance.changeTimestampTime(currentTimestamp, 14, 30)))
    }
}
