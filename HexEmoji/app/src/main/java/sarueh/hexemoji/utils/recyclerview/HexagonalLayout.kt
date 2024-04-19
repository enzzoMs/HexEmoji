package sarueh.hexemoji.utils.recyclerview

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import kotlin.math.tan

private const val DEFAULT_HEXAGON_MARGIN_PX = 30

/**
 * A utility class for creating hexagonal grid layouts within a RecyclerView
 * using a [GridLayoutManager].
 */

object HexagonalLayout {

    /**
     * A method to assist in creating a hexagonal grid pattern by manipulating a view's margins.
     *
     * To guarantee the desired behavior this method should be used within a RecyclerView
     * using [GridLayoutManager] and [HexagonalSpanSizeLookup]. For better results the RecyclerView's
     * width should be set to 'wrap_content'.
     *
     * It is typically invoked within the 'onBindViewHolder()' method of the recyclerView's adapter
     *
     * @param view The view whose margins and position will be adjusted.
     * @param viewSizePx The size of each grid item in pixels. All items are assumed to be
     * squares and have the same size.
     */
    fun setHexagonMargins(
        view: View, viewPositionInGrid: Int, spanCount: Int, viewSizePx: Int,
        marginPx: Int = DEFAULT_HEXAGON_MARGIN_PX
    ) {
        val layoutParams = LinearLayout.LayoutParams(viewSizePx, viewSizePx).also {
            // Reset existing margins
            it.setMargins(0, 0, 0, 0)
        }

        // In the hexagonal pattern created by 'HexagonalSpanSizeLookup' even rows have a length of
        // 'spanCount' items while odd rows have a length of 'spanSize - 1'
        val distanceBetweenOddRows = spanCount + (spanCount - 1)

        if (viewPositionInGrid % distanceBetweenOddRows == 0) {
            // This condition means that the view is at the beginning of an odd row.
            // In the 'HexagonalSpanSizeLookup' pattern views at this position have a span size
            // of 2, resulting in the following layout:
            // x       *   *
            // *   *   *   *
            // In this case, our goal is to horizontally align this item (x) with the middle of
            // the two bottom items (viewSizePx/2):
            //   x     *   *
            // *   *   *   *
            // (We use 'marginPx/4' to account for the margin that will be applied to the items below.)

            layoutParams.marginStart = viewSizePx/2 + marginPx/4

        } else if (viewPositionInGrid % distanceBetweenOddRows < spanCount - 1) {
            // When a particular view 'O' is on an odd row but not the first item in that row,
            // the following scenario is encountered:
            //   x     O   O
            // *   *   *   *
            // To maintain the hexagonal pattern it is necessary to move the positions of
            // this views to the left:
            //   x   O   O
            // *   *   *   *

            layoutParams.marginStart = -(viewSizePx/2 + marginPx/4)

        } else if ((viewPositionInGrid + 1) % spanCount != 0) {
            // Apply margin to items on even rows (except the last one)
            layoutParams.marginEnd = marginPx/2
        }

        if (viewPositionInGrid >= spanCount - 1) {
            // To complete the hexagonal pattern it's necessary to rise the tip of items that are not on
            // the first row.
            // This transforms the pattern in the following way:
            //   *     *            *     *
            // *   * *   *    ->  *   * *   *
            //   *     *            *  x  *
            //      x                x   x
            //    x   x                x
            //      x
            // To determine the amount that needs to be raised we use trigonometry:
            // \     /      \     / |
            //  \   /        \   /  | x
            //   \ /   ->     \ /___|
            //                  |  a
            //                  └─────────────> angle of 30 degrees
            // In this context, 'a' is half of 'viewSizePx' (the radius of the hexagon), and
            // 'x' is the vertical displacement we need to find.
            // Therefore, since tan(30 degrees) = x / a, we have that x = a * tan(30 degrees)

            val topDisplacement = ((viewSizePx / 2) * tan(Math.toRadians(30.0))).toInt()

            layoutParams.topMargin = -topDisplacement + marginPx
        }

        view.layoutParams = layoutParams
    }
}