package enzzom.hexemoji.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import enzzom.hexemoji.data.entities.GameStatistic

@Dao
interface StatisticsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameStatistic(gameStatistic: GameStatistic)

    @Query("DELETE FROM game_statistics WHERE day < :day OR month < :month")
    suspend fun deleteStatisticsBeforeDate(day: Int, month: Int)

    @Query("SELECT * FROM game_statistics")
    suspend fun getAllGameStatistics(): List<GameStatistic>

    @Query("SELECT COUNT() FROM game_statistics")
    suspend fun countAllGameStatistics(): Int
}