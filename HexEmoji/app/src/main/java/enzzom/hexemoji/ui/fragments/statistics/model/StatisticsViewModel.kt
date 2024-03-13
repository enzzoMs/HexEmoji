package enzzom.hexemoji.ui.fragments.statistics.model

import android.os.CountDownTimer
import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.entities.GameStatistic
import enzzom.hexemoji.data.repositories.StatisticsRepository
import enzzom.hexemoji.models.WeekDay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

private const val MIN_LOADING_TIME_MS = 800L

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val _statisticsLoading = MutableLiveData(true)
    val statisticsLoading: LiveData<Boolean> = _statisticsLoading

    private var gameStatisticsInCurrentWeek: List<GameStatistic>? = null

    val currentWeekDate: String

    init {
        val currentDate = LocalDateTime.now()

        val weekStartDate = currentDate.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
        )

        val weekEndDate = currentDate.with(
            TemporalAdjusters.next(DayOfWeek.SUNDAY)
        )

        val dateFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(), "ddMM")

        val formattedWeekStartDate = dateFormat.replace(
            "dd", weekStartDate.dayOfMonth.toString().padStart(2, '0')
        ).replace(
            "MM", weekStartDate.monthValue.toString().padStart(2, '0')
        )

        val formattedWeekEndDate = dateFormat.replace(
            "dd", weekEndDate.dayOfMonth.toString().padStart(2, '0')
        ).replace(
            "MM", weekEndDate.monthValue.toString().padStart(2, '0')
        )

        currentWeekDate = "$formattedWeekStartDate - $formattedWeekEndDate"

        var minLoadingTimeFinished = false
        var dataLoadingFinished = false

        object : CountDownTimer(MIN_LOADING_TIME_MS, MIN_LOADING_TIME_MS) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                minLoadingTimeFinished = true

                if (dataLoadingFinished) {
                    _statisticsLoading.value = false
                }
            }
        }.start()

        viewModelScope.launch {
            // Delete old records from the database
            statisticsRepository.deleteStatisticsBeforeDate(
                weekStartDate.dayOfMonth, weekStartDate.monthValue
            )

            gameStatisticsInCurrentWeek = statisticsRepository.getAllGameStatistics()
            dataLoadingFinished = true

            if (minLoadingTimeFinished) {
                _statisticsLoading.value = false
            }
        }
    }


    fun getVictoriesCurrentInWeek(): Int? = gameStatisticsInCurrentWeek?.count { it.victory }

    fun getVictoriesInWeekDay(weekDay: WeekDay): Int? = gameStatisticsInCurrentWeek?.count {
        it.weekDay == weekDay && it.victory
    }
}