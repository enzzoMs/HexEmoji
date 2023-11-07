package enzzom.hexemoji.models

import enzzom.hexemoji.utils.recyclerview.HexagonalSpanSizeLookup
import kotlin.math.ceil

enum class BoardSize(val numOfColumns: Int, private val numOfRows: Int) {
    BOARD_2_BY_4(2, 4),
    BOARD_3_BY_4(3, 4),
    BOARD_4_BY_4(4, 4),
    BOARD_4_BY_7(4, 7),
    BOARD_4_BY_8(4, 8),
    BOARD_5_BY_8(5, 8),
    BOARD_7_BY_9(7, 9),
    BOARD_8_BY_7(8, 7),
    BOARD_8_BY_8(8, 8),
    BOARD_6_BY_8(6, 8),
    BOARD_9_BY_8(9, 8),
    BOARD_9_BY_9(9, 9);

    fun getLabel(): String = "${numOfColumns}x${numOfRows}"

    /**
     * Returns the total number of cards for this board size when used in a hexagonal layout.
     *
     * Usually, the total would be the result of (numOfColumns * numOfRows), but in a hexagonal
     * layout we have the following pattern:
     *
     * numOfColumns - 1 -> numOfColumns -> numOfColumns - 1 -> numOfColumns -> ...
     *
     * for the numOfRows, this function adjusts the total to account for this pattern.
     *
     * @see HexagonalSpanSizeLookup
     */
    fun getSizeInHexagonalLayout(): Int = (numOfColumns * numOfRows) - ceil(numOfRows / 2f).toInt()
}