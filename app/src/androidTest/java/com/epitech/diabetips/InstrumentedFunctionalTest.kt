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
class InstrumentedFunctionalTest {

    private lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun saveAndGetAllManager() {
        //Expected values
        val expectedAccessToken = "49fad390491a5b547d0f782309b6a5b33f7ac087"
        val expectedRefreshToken = "8xLOxBtZp8"
        val expectedAccount = UserObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        val expectedBiometric = BiometricObject(80, 175, 60, 180, "2020-10-05", "male")
        val expectedMode =  AppCompatDelegate.MODE_NIGHT_YES
        //Save values with managers
        AuthManager.instance.saveAccessToken(instrumentationContext, expectedAccessToken)
        AuthManager.instance.saveRefreshToken(instrumentationContext, expectedRefreshToken)
        UserManager.instance.saveUser(instrumentationContext, expectedAccount)
        UserManager.instance.saveBiometric(instrumentationContext, expectedBiometric)
        ModeManager.instance.saveDarkMode(instrumentationContext, expectedMode)
        //Get values with managers
        val actualAccessToken = AuthManager.instance.getAccessToken(instrumentationContext)
        val actualRefreshToken = AuthManager.instance.getRefreshToken(instrumentationContext)
        val actualAccount =  UserManager.instance.getUser(instrumentationContext)
        val actualBiometric =  UserManager.instance.getBiometric(instrumentationContext)
        val actualMode =  ModeManager.instance.getDarkMode(instrumentationContext)
        //Asserts
        assertEquals("Wrong access token", expectedAccessToken, actualAccessToken)
        assertEquals("Wrong refresh token", expectedRefreshToken, actualRefreshToken)
        assertEquals("Wrong account", expectedAccount, actualAccount)
        assertEquals("Wrong biometric", expectedBiometric, actualBiometric)
        assertEquals("Wrong dark mode", expectedMode, actualMode)
    }

    @Test
    fun removeAllManager() {
        //Expected values
        val expectedAccessToken = "49fad390491a5b547d0f782309b6a5b33f7ac087"
        val expectedRefreshToken = "8xLOxBtZp8"
        val expectedAccount = UserObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        val expectedBiometric = BiometricObject(80, 175, 60, 180, "2020-10-05", "male")
        val expectedMode =  AppCompatDelegate.MODE_NIGHT_YES
        //Save values with managers
        AuthManager.instance.saveAccessToken(instrumentationContext, expectedAccessToken)
        AuthManager.instance.saveRefreshToken(instrumentationContext, expectedRefreshToken)
        UserManager.instance.saveUser(instrumentationContext, expectedAccount)
        UserManager.instance.saveBiometric(instrumentationContext, expectedBiometric)
        ModeManager.instance.saveDarkMode(instrumentationContext, expectedMode)
        //Remove values with managers
        AuthManager.instance.removePreferences(instrumentationContext)
        UserManager.instance.removePreferences(instrumentationContext)
        ModeManager.instance.removePreferences(instrumentationContext)
        //Get values with managers
        val actualAccessToken = AuthManager.instance.removePreferences(instrumentationContext)
        val actualRefreshToken = AuthManager.instance.removePreferences(instrumentationContext)
        val actualAccount =  UserManager.instance.removePreferences(instrumentationContext)
        val actualBiometric =  UserManager.instance.getBiometric(instrumentationContext)
        val actualMode =  ModeManager.instance.removePreferences(instrumentationContext)
        //Asserts
        assertNotEquals("Wrong access token", expectedAccessToken, actualAccessToken)
        assertNotEquals("Wrong refresh token", expectedRefreshToken, actualRefreshToken)
        assertNotEquals("Wrong account", expectedAccount, actualAccount)
        assertNotEquals("Wrong biometric", expectedBiometric, actualBiometric)
        assertNotEquals("Wrong dark mode", expectedMode, actualMode)
    }

    @Test
    fun foodAdapters() {
        //Values
        val foodArray = arrayOf(FoodObject(1, "Food"), FoodObject(2), FoodObject(3))
        val ingredientArray = arrayOf(IngredientObject(10f, food = FoodObject(1, "Food")), IngredientObject(15f, food = FoodObject(2, "Ingredient")), IngredientObject(7f, food = FoodObject(3)))
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
        val mealRecipeArray = arrayOf(MealRecipeObject(0f, recipe = RecipeObject(1, "Recipe")), MealRecipeObject(0f, recipe = RecipeObject(2)), MealRecipeObject(0f, recipe = RecipeObject(3)))
        val recipeAdapter = RecipeAdapter()
        val mealRecipeAdapter = MealRecipeAdapter()
        //Operations
        recipeAdapter.setRecipes(recipeArray)
        recipeAdapter.addRecipe(recipeArray[0])
        recipeAdapter.addRecipes(recipeArray)
        mealRecipeAdapter.setRecipes(mealRecipeArray)
        mealRecipeAdapter.addRecipe(mealRecipeArray[0])
        //Asserts
        assertEquals("Wrong recipe adapter", recipeArray.size * 2 + 1, recipeAdapter.itemCount)
        assertEquals("Wrong meal recipe adapter", mealRecipeArray.size + 1, mealRecipeAdapter.itemCount)
    }

    @Test
    fun hba1cAdapters() {
        //Values
        val hba1cArray = arrayOf(HbA1cObject(1, 5.5f), HbA1cObject(2, 10f), HbA1cObject(3, 7f))
        val hbA1cAdapter = HbA1cAdapter()
        //Operations
        hbA1cAdapter.setHbA1c(hba1cArray)
        hbA1cAdapter.addHbA1c(hba1cArray)
        //Asserts
        assertEquals("Wrong HbA1c adapter", hba1cArray.size * 2, hbA1cAdapter.itemCount)
    }

    @Test
    fun chatAdapters() {
        //Values
        val chatArray = arrayOf(ChatObject("1", content = "Message 1"), ChatObject("2", content = "Message 2"), ChatObject("3", content = "Message 3"))
        val chatAdapter = ChatAdapter()
        //Operations
        chatAdapter.setMessages(chatArray)
        chatAdapter.addMessage(chatArray[0])
        chatAdapter.addMessages(chatArray)
        //Asserts
        assertEquals("Wrong Chat adapter", chatArray.size * 2 + 1, chatAdapter.itemCount)
    }

    @Test
    fun biometricObjects() {
        //Values
        val biometric = BiometricObject(80, 175, 60, 180, "2020-10-05")
        //Operations
        biometric.setSex(instrumentationContext, instrumentationContext.resources.getStringArray(R.array.sex)[0])
        biometric.setDiabetesType(instrumentationContext, instrumentationContext.resources.getStringArray(R.array.diabetes_type)[0])
        //Asserts
        assertEquals("Wrong sex", instrumentationContext.resources.getStringArray(R.array.sex)[0], biometric.getSex(instrumentationContext))
        assertEquals("Wrong diabetes type", instrumentationContext.resources.getStringArray(R.array.diabetes_type)[0], biometric.getDiabetesType(instrumentationContext))
    }

    @Test
    fun activityObject() {
        //Values
        val timeFormat = instrumentationContext.getString(R.string.format_time_api)
        val currentTimestamp = TimeHandler.instance.currentTime()
        val currentTime = TimeHandler.instance.formatTimestamp(currentTimestamp, timeFormat)
        val activity = ActivityObject(start = currentTime, end = currentTime)
        //Operations
        activity.setDuration(instrumentationContext, TimeHandler.instance.formatTimestamp(TimeHandler.instance.addTimeToTimestamp(0, 90), timeFormat, true))
        activity.setStart(instrumentationContext, currentTime)
        //Asserts
        assertEquals("Wrong activity start", activity.start, currentTime)
        assertEquals("Wrong activity end", activity.end, TimeHandler.instance.addTimeToFormat(currentTime, timeFormat, 90))
        assertEquals("Wrong activity duration", activity.getDurationSecond(instrumentationContext), 90 * 60)
    }

    @Test
    fun timeHandlers() {
        //Values
        val timeFormat = instrumentationContext.getString(R.string.format_time_api)
        val currentTimestamp = TimeHandler.instance.currentTime()
        val currentTime = TimeHandler.instance.formatTimestamp(currentTimestamp, timeFormat)
        //Asserts
        assertEquals("Wrong Timestamp to Time", currentTime, TimeHandler.instance.formatTimestamp(currentTimestamp, timeFormat))
        assertEquals("Wrong Time to Timestamp", currentTimestamp, TimeHandler.instance.getTimestampFromFormat(currentTime, timeFormat))
        assertEquals("Wrong Add Time",
            TimeHandler.instance.addTimeToTimestamp(currentTimestamp, 90),
            TimeHandler.instance.getTimestampFromFormat(TimeHandler.instance.addTimeToFormat(currentTime, timeFormat, 90), timeFormat))
        assertEquals("Wrong Change Date",
            TimeHandler.instance.changeTimestampDate(currentTimestamp, 2000, 9, 12),
            TimeHandler.instance.getTimestampFromFormat(TimeHandler.instance.changeFormatDate(currentTime, timeFormat, 2000, 9, 12), timeFormat))
        assertEquals("Wrong Change Time",
            TimeHandler.instance.changeTimestampTime(currentTimestamp, 12, 30),
            TimeHandler.instance.getTimestampFromFormat(TimeHandler.instance.changeFormatTime(currentTime, timeFormat, 12, 30), timeFormat))
        assertEquals("Wrong Second Diff",
            TimeHandler.instance.getSecondDiffFormat(TimeHandler.instance.changeFormatTime(currentTime, timeFormat, 12, 0),
                TimeHandler.instance.changeFormatTime(currentTime, timeFormat, 14, 30), timeFormat),
            TimeHandler.instance.getSecondDiff(TimeHandler.instance.changeTimestampTime(currentTimestamp, 12, 0),
                TimeHandler.instance.changeTimestampTime(currentTimestamp, 14, 30)))
    }
}
