package enzzom.hexemoji.models

enum class BoardSize(private val numOfColumns: Int, private val numOfRows: Int) {
    BOARD_2_BY_4(2, 4),
    BOARD_2_BY_6(2, 6),
    BOARD_4_BY_4(4, 4),
    BOARD_4_BY_6(4, 6),
    BOARD_4_BY_8(4, 8),
    BOARD_5_BY_6(5, 6),
    BOARD_5_BY_8(5, 8),
    BOARD_8_BY_6(8, 6),
    BOARD_8_BY_7(8, 7),
    BOARD_8_BY_8(8, 8),
    BOARD_9_BY_8(9, 8);
    fun getLabel(): String = "${numOfColumns}x${numOfRows}"
}