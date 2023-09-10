package enzzom.hexemoji.utils.recyclerview

import androidx.recyclerview.widget.GridLayoutManager

/**
 * A custom [GridLayoutManager.SpanSizeLookup] class for creating a hexagonal grid layout.
 *
 * This class is designed for use with a [GridLayoutManager] to determine the span size
 * (number of columns) that an item at a specific position should occupy in the grid.
 *
 * This class doesn't do much by itself. It creates a pattern where every item at the beginning of
 * odd rows occupies 2 spans instead of one.
 *
 * For example, with a 'spanCount' of 4, the pattern would be :
 * 3 items -> 4 items -> 3 items -> 4 items -> ...
 *
 * To actually implement the hexagonal pattern, the RecyclerView adapter should use the helper method
 * [HexagonalLayout.setHexagonMargins] to position the items correctly within the grid.
 *
 * @see HexagonalLayout
 */
class HexagonalSpanSizeLookup(
    private val spanCount: Int
) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        val distanceBetweenOddRows = spanCount + (spanCount - 1)
        val isFirstInRow = position % distanceBetweenOddRows == 0

        return if (isFirstInRow) 2 else 1
    }
}