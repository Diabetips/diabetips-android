package com.epitech.diabetips

import com.epitech.diabetips.storages.*
import org.junit.Test

import org.junit.Assert.*

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
        page.updateFromHeader("previous: " + expectedPrevious + "; next: " + expectedNext + "; last: " + expectedLast)
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
        val food = FoodObject(1, "Food", "g", 100f)
        val ingredient = IngredientObject(10f, 10f, food)
        val recipe = RecipeObject(1, "Recipe", "", 25f, arrayOf(ingredient), arrayOf(IngredientObject(25f, 25f, food)))
        val meal = MealObject(1, 0, "", 35f, arrayOf(recipe), arrayOf(ingredient))
        var expectedSugar = ingredient.total_sugar
        assertEquals("Wrong ingredient total sugar", ingredient.total_sugar, ingredient.calculateTotalSugar())
        expectedSugar = recipe.total_sugar
        assertEquals("Wrong ingredient total sugar", recipe.total_sugar, recipe.calculateTotalSugar())
        expectedSugar = meal.total_sugar
        assertEquals("Wrong ingredient total sugar", meal.total_sugar, meal.calculateTotalSugar())

    }
}