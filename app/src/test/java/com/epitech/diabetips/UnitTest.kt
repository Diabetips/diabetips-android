package com.epitech.diabetips

import com.epitech.diabetips.storages.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Local unit test, which will execute on the development machine.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {

    private val paginationObject = PaginationObject(10, 1, 3, 2, 4, 5)
    private val foodObject = FoodObject(1, "Food", "g", 100f)
    private val ingredientObject = IngredientObject(10f, 10f, foodObject)
    private val recipeObject = RecipeObject(1, "Recipe", "", 10f, 1f, arrayOf(ingredientObject))
    private val mealRecipeObject = MealRecipeObject(25f, 1f, recipeObject, arrayOf(IngredientObject(25f, 25f, foodObject)))
    private val mealObject = MealObject(1, "", "", 35f, arrayOf(mealRecipeObject), arrayOf(ingredientObject))

    @Test
    fun paginationObjectNextPage() {
        val page = paginationObject
        page.nextPage()
        assertEquals("Wrong next page", page.current, paginationObject.next)
    }

    @Test
    fun paginationObjectPreviousPage() {
        val page = paginationObject
        page.previousPage()
        assertEquals("Wrong previous page", page.current, paginationObject.previous)
    }

    @Test
    fun paginationObjectReset() {
        val page = paginationObject
        page.reset()
        assertEquals("Wrong pagination", page, PaginationObject(paginationObject.size, paginationObject.default))
    }

    @Test
    fun paginationObjectUpdateFromHeader() {
        val page = paginationObject
        val expectedPrevious = 42
        val expectedNext = 69
        val expectedLast = 666
        page.updateFromHeader("previous: $expectedPrevious; next: $expectedNext; last: $expectedLast")
        assertEquals("Wrong previous page", page.previous, expectedPrevious)
        assertEquals("Wrong next page", page.next, expectedNext)
        assertEquals("Wrong last page", page.last, expectedLast)
    }

    @Test
    fun ingredientTotalSugar() {
        val ingredient = ingredientObject
        assertEquals("Wrong ingredient total sugar", ingredientObject.total_sugar, ingredient.calculateTotalSugar())
    }

    @Test
    fun recipeTotalSugar() {
        val recipe = recipeObject
        assertEquals("Wrong recipe total sugar", recipeObject.total_sugar, recipe.calculateTotalSugar())
    }

    @Test
    fun mealRecipeTotalSugar() {
        val mealRecipe = mealRecipeObject
        assertEquals("Wrong mealRecipe total sugar", mealRecipeObject.total_sugar, mealRecipe.calculateTotalSugar())
    }

    @Test
    fun mealTotalSugar() {
        val meal = mealObject
        assertEquals("Wrong meal total sugar", mealObject.total_sugar, meal.calculateTotalSugar())
    }
}