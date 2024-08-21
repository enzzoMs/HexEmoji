package ems.hexemoji.data.repositories

import ems.hexemoji.data.entities.GameStatistic
import ems.hexemoji.data.source.StatisticsDAO
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

    suspend fun countAllGameStatistics(): Int {
        return statisticsDAO.countAllGameStatistics()
    }
}