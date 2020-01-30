package com.epitech.diabetips

import com.epitech.diabetips.storages.PaginationObject
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
}