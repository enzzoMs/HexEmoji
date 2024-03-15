package enzzom.hexemoji.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.WeekDay

@Entity(tableName = "game_statistics")
data class GameStatistic(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "num_of_pairs_found") val numOfPairsFound: Int,
    @ColumnInfo(name = "game_mode") val gameMode: GameMode,
    @ColumnInfo(name = "board_size") val boardSize: BoardSize,
    @ColumnInfo(name = "week_day") val weekDay: WeekDay,
    @ColumnInfo(name = "day") val day: Int,
    @ColumnInfo(name = "month") val month: Int,
    val victory: Boolean
)