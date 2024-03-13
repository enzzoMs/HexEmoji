package enzzom.hexemoji.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.WeekDay

@Entity(tableName = "game_statistics")
data class GameStatistic(
    @PrimaryKey(autoGenerate = true) val victory: Boolean,
    @ColumnInfo(name = "week_day") val weekDay: WeekDay,
    @ColumnInfo(name = "day") val day: Int,
    @ColumnInfo(name = "month") val month: Int,
    @ColumnInfo(name = "game_mode") val gameMode: GameMode
)