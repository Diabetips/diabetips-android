package com.epitech.diabetips.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Pagination class to add more items to the list when reach the last item.
 */
abstract class PaginationInvertedScrollListener
/**
 * Supporting only LinearLayoutManager for now.
 *
 * @param layoutManager
 */
    (var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (isLoading() || isLastPage())
            return
        if (layoutManager.findFirstVisibleItemPosition() == 0) {
            loadMoreItems()
        }
    }

    abstract fun loadMoreItems()
}