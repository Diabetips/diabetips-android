package com.epitech.diabetips

import com.epitech.diabetips.storages.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit test, which will execute on the development machine.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FunctionalTest {

    @Test
    fun paginationObject() {
        val page = PaginationObject(10, 1, 3, 2, 4, 5)
        val expectedPrevious = 42
        val expectedNext = 69
        val expectedLast = 666
        page.updateFromHeader("previous: $expectedPrevious; next: $expectedNext; last: $expectedLast")
        assertEquals("Wrong previous page", page.previous, expectedPrevious)
        assertEquals("Wrong next page", page.next, expectedNext)
        assertEquals("Wrong last page", page.last, expectedLast)
        page.nextPage()
        assertEquals("Wrong next page", page.current, expectedNext)
        page.previousPage()
        assertEquals("Wrong previous page", page.current, expectedPrevious)
        page.reset()
        assertEquals("Wrong pagination", page, PaginationObject(page.size, page.default))
    }

    @Test
    fun totalSugar() {
        val food = FoodObject(1, "Food", "g", carbohydrates_100g = 100f)
        val ingredient = IngredientObject(quantity = 10f, total_carbohydrates = 10f, food = food)
        val recipe = RecipeObject(1, "Recipe", portions = 1f, total_carbohydrates = 10f, ingredients = arrayOf(ingredient))
        val mealRecipe = MealRecipeObject(portions_eaten = 1f, total_carbohydrates = 25f, recipe = recipe, modifications = arrayOf(IngredientObject(25f, total_carbohydrates = 25f, food = food)))
        val meal = MealObject(1, total_carbohydrates = 35f, recipes = arrayOf(mealRecipe), foods = arrayOf(ingredient))
        var expectedSugar = ingredient.total_carbohydrates
        assertEquals("Wrong ingredient total sugar", expectedSugar, ingredient.calculateTotalSugars())
        expectedSugar = recipe.total_carbohydrates
        assertEquals("Wrong recipe total sugar", expectedSugar, recipe.calculateTotalSugars())
        expectedSugar = mealRecipe.total_carbohydrates
        assertEquals("Wrong mealRecipe total sugar", expectedSugar, mealRecipe.calculateTotalSugars())
        expectedSugar = meal.total_carbohydrates
        assertEquals("Wrong meal total sugar", expectedSugar, meal.calculateTotalSugars())
    }

    @Test
    fun notificationObject() {
        val notificationChat = NotificationChatObject(from = "2B2T42666-693607202B", msg_id = "2B72036069-666422B2T")
        notificationChat.id = "2B72036069-666422B2T"
        notificationChat.read = true
        notificationChat.type = NotificationObject.Type.chat_message.name
        notificationChat.data = """{"from": "2B2T42666-693607202B", "msg_id": "2B72036069-666422B2T"}"""
        val notificationInvite = NotificationInviteObject(from = "2B2T42666-693607202B")
        notificationInvite.id = "2B72036069-666422B2T"
        notificationInvite.read = true
        notificationInvite.type = NotificationObject.Type.user_invite.name
        notificationInvite.data = """{"from": "2B2T42666-693607202B"}"""
        val notificationTest = NotificationTestObject(foo = "aled", bar = "oskour")
        notificationTest.id = "2B72036069-666422B2T"
        notificationTest.read = true
        notificationTest.type = NotificationObject.Type.test.name
        notificationTest.data = """{"foo": "aled", "bar": "oskour"}"""
        assertEquals("Wrong Notification Chat", notificationChat, NotificationObject(id = notificationChat.id, read = notificationChat.read, type = notificationChat.type, data = notificationChat.data).getTypedNotification())
        assertEquals("Wrong Notification Invite", notificationInvite, NotificationObject(id = notificationInvite.id, read = notificationInvite.read, type = notificationInvite.type, data = notificationInvite.data).getTypedNotification())
        assertEquals("Wrong Notification Test", notificationTest, NotificationObject(id = notificationTest.id, read = notificationTest.read, type = notificationTest.type, data = notificationTest.data).getTypedNotification())
    }
}