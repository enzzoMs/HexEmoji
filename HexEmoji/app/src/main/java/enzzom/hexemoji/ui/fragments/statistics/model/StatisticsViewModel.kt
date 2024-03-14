package enzzom.hexemoji.ui.fragments.statistics.model

import android.os.CountDownTimer
import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.entities.GameStatistic
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.data.repositories.StatisticsRepository
import enzzom.hexemoji.models.EmojiCategory
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
    private val statisticsRepository: StatisticsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val emojiRepository: EmojiRepository
) : ViewModel() {

    private val _statisticsLoading = MutableLiveData(true)
    val statisticsLoading: LiveData<Boolean> = _statisticsLoading

    private var gameStatisticsInCurrentWeek: List<GameStatistic>? = null

    val dailyEmojiReward: String?
    private var dailyEmojiUnlocked: Boolean

    val currentWeekDate: String

    init {
        val previousEmojiDay = preferencesRepository.getInt(
            PreferencesRepository.PREFERENCE_KEY_PREVIOUS_DAILY_EMOJI_DAY, -1
        )

        dailyEmojiUnlocked = previousEmojiDay != -1 && previousEmojiDay == LocalDateTime.now().dayOfMonth

        preferencesRepository.getString(
            PreferencesRepository.PREFERENCE_KEY_NEXT_DAILY_EMOJI, "\\uD83D\\uDC1D"
        ).let {
            dailyEmojiReward = if (it.isNullOrBlank()) null else it
        }

        currentWeekDate = getWeekDate()

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

        val weekStartDate = LocalDateTime.now().with(
            TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
        )

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

    fun unlockDailyEmoji() {
        viewModelScope.launch {
            dailyEmojiReward?.let {
                dailyEmojiUnlocked = true

                emojiRepository.unlockEmoji(it)

                val nextEmoji = emojiRepository.getRandomUnlockedEmojis(
                    EmojiCategory.entries, 1
                ).getOrElse(0) { "" }

                preferencesRepository.putString(
                    PreferencesRepository.PREFERENCE_KEY_NEXT_DAILY_EMOJI, nextEmoji
                )

                preferencesRepository.putInt(
                    PreferencesRepository.PREFERENCE_KEY_PREVIOUS_DAILY_EMOJI_DAY,
                    LocalDateTime.now().dayOfMonth
                )
            }
        }
    }

    fun dailyEmojiUnlocked(): Boolean = dailyEmojiUnlocked

    fun getVictoriesCurrentInWeek(): Int? = gameStatisticsInCurrentWeek?.count { it.victory }

    fun getVictoriesInWeekDay(weekDay: WeekDay): Int? = gameStatisticsInCurrentWeek?.count {
        it.weekDay == weekDay && it.victory
    }

    private fun getWeekDate(): String {
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

        return "$formattedWeekStartDate - $formattedWeekEndDate"
    }
}