package enzzom.hexemoji.data.repositories

import enzzom.hexemoji.data.entities.GameStatistic
import enzzom.hexemoji.data.source.StatisticsDAO
import javax.inject.Inject

class StatisticsRepository @Inject constructor(
    private val statisticsDAO: StatisticsDAO
) {

    suspend fun insertGameStatistic(gameStatistic: GameStatistic) {
        statisticsDAO.insertGameStatistic(gameStatistic)
    }

    suspend fun getAllGameStatistics(): List<GameStatistic> {
        return statisticsDAO.getAllGameStatistics()
    }
}