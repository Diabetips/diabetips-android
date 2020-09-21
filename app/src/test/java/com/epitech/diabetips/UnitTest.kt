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
    private val foodObject = FoodObject(1, "Food", "g", carbohydrates_100g = 100f)
    private val ingredientObject = IngredientObject(quantity = 10f, total_carbohydrates = 10f, food = foodObject)
    private val recipeObject = RecipeObject(1, "Recipe", portions = 1f, total_carbohydrates = 10f, ingredients = arrayOf(ingredientObject))
    private val mealRecipeObject = MealRecipeObject(portions_eaten = 1f, total_carbohydrates = 25f, recipe = recipeObject, modifications = arrayOf(IngredientObject(25f, total_carbohydrates = 25f, food = foodObject)))
    private val mealObject = MealObject(1, total_carbohydrates = 35f, recipes = arrayOf(mealRecipeObject), foods = arrayOf(ingredientObject))

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
        assertEquals("Wrong ingredient total sugar", ingredientObject.total_carbohydrates, ingredient.calculateTotalSugars())
    }

    @Test
    fun recipeTotalSugar() {
        val recipe = recipeObject
        assertEquals("Wrong recipe total sugar", recipeObject.total_carbohydrates, recipe.calculateTotalSugars())
    }

    @Test
    fun mealRecipeTotalSugar() {
        val mealRecipe = mealRecipeObject
        assertEquals("Wrong mealRecipe total sugar", mealRecipeObject.total_carbohydrates, mealRecipe.calculateTotalSugars())
    }

    @Test
    fun mealTotalSugar() {
        val meal = mealObject
        assertEquals("Wrong meal total sugar", mealObject.total_carbohydrates, meal.calculateTotalSugars())
    }
}