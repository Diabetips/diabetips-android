package com.epitech.diabetips

import com.epitech.diabetips.storages.BiometricObject
import com.epitech.diabetips.storages.PaginationObject
import org.junit.Test

import org.junit.Assert.*

/**
 * Local unit test, which will execute on the development machine.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {

    private val paginationObject = PaginationObject(10, 1, 3, 2, 4, 5)
    private val biometricObject = BiometricObject(80, 175, "", "man")

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
        page.updateFromHeader("previous: " + expectedPrevious + "; next: " + expectedNext + "; last: " + expectedLast)
        assertEquals("Wrong previous page", page.previous, expectedPrevious)
        assertEquals("Wrong next page", page.next, expectedNext)
        assertEquals("Wrong last page", page.last, expectedLast)
    }
}