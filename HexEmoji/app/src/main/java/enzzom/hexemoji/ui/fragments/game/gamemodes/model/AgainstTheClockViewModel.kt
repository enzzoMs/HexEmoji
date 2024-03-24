package enzzom.hexemoji.ui.fragments.game.gamemodes.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.entities.TimedChallenge
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.data.repositories.StatisticsRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.BoardSize.BOARD_2_BY_4
import enzzom.hexemoji.models.BoardSize.BOARD_3_BY_4
import enzzom.hexemoji.models.BoardSize.BOARD_4_BY_4
import enzzom.hexemoji.models.BoardSize.BOARD_4_BY_7
import enzzom.hexemoji.models.BoardSize.BOARD_4_BY_8
import enzzom.hexemoji.models.BoardSize.BOARD_5_BY_8
import enzzom.hexemoji.models.BoardSize.BOARD_6_BY_8
import enzzom.hexemoji.models.BoardSize.BOARD_7_BY_9
import enzzom.hexemoji.models.BoardSize.BOARD_8_BY_7
import enzzom.hexemoji.models.BoardSize.BOARD_8_BY_8
import enzzom.hexemoji.models.BoardSize.BOARD_9_BY_8
import enzzom.hexemoji.models.BoardSize.BOARD_9_BY_9
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject
import kotlin.math.floor

private const val ONE_SECOND_IN_MILLIS = 1000L

@HiltViewModel
class AgainstTheClockViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    statisticsRepository: StatisticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository, statisticsRepository,
    BoardSize.valueOf(savedStateHandle.get<String>(BaseGameModeFragment.BOARD_SIZE_ARG_KEY)!!),
    GameMode.valueOf(savedStateHandle.get<String>(BaseGameModeFragment.GAME_MODE_ARG_KEY)!!),
    savedStateHandle.get<Array<String>>(BaseGameModeFragment.SELECTED_CATEGORIES_ARG_KEY)!!.map {
        EmojiCategory.valueOf(it)
    }
) {

    private val startTimeMs: Long
    private var remainingTimeMs: Long

    private val _remainingSeconds = MutableLiveData<Long>()
    val remainingSeconds: LiveData<Long> = _remainingSeconds

    private var countdownTimer: CountDownTimer? = null
    private var timerPaused: Boolean = false

    init {
        val startTimeSeconds = when (boardSize) {
            BOARD_2_BY_4 -> 15;    BOARD_3_BY_4 -> 30;   BOARD_4_BY_4 -> 45
            BOARD_4_BY_7 -> 90;   BOARD_4_BY_8 -> 100;  BOARD_5_BY_8 -> 160
            BOARD_7_BY_9 -> 290;  BOARD_8_BY_7 -> 270;  BOARD_8_BY_8 -> 320
            BOARD_6_BY_8 -> 210;  BOARD_9_BY_8 -> 420;  BOARD_9_BY_9 -> 460
        }

        startTimeMs = startTimeSeconds * ONE_SECOND_IN_MILLIS
        remainingTimeMs = startTimeMs
        _remainingSeconds.value = startTimeSeconds.toLong()
    }

    fun startTimer() {
        countdownTimer = getTimer(startTimeMs).start()
        timerPaused = false
    }

    fun pauseTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
        timerPaused = true
    }

    fun resumeTimer() {
        countdownTimer?.cancel()
        timerPaused = false
        countdownTimer = getTimer(remainingTimeMs).start()
    }

    fun isTimerPaused(): Boolean = timerPaused

    override fun shouldUpdateChallengeOnVictory(challenge: Challenge): Boolean {
        return when (challenge) {
            is GeneralChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (challenge.boardSize == null || challenge.boardSize == boardSize) &&
                ((challenge.constrainedToCategory && challenge.category in selectedCategories)
                    || !challenge.constrainedToCategory)
            is TimedChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (startTimeMs - remainingTimeMs) / ONE_SECOND_IN_MILLIS <= challenge.timeLimitInSeconds
            else -> false
        }
    }

    private fun getTimer(millisInFuture: Long): CountDownTimer = object : CountDownTimer(millisInFuture, ONE_SECOND_IN_MILLIS) {
        override fun onTick(millisUntilFinished: Long) {
            remainingTimeMs = millisUntilFinished

            val secondsUntilFinished  = floor(millisUntilFinished / ONE_SECOND_IN_MILLIS.toFloat()).toLong()

            if (secondsUntilFinished == 0L && getGameStatus() == GameStatus.IN_PROGRESS) {
                setGameStatus(GameStatus.DEFEAT)
            }

            _remainingSeconds.value = secondsUntilFinished
        }

        override fun onFinish() {}
    }
}