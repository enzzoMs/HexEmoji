package enzzom.hexemoji.ui.fragments.game.gamemodes.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject
import kotlin.math.floor

private const val ONE_SECOND_IN_MILLIS = 1000L

private const val MILLIS_PER_CARD = 2000
private const val TIMER_SCALE_FACTOR = 1.5f

@HiltViewModel
class AgainstTheClockViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository,
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
        val boardSize = BoardSize.valueOf(savedStateHandle.get<String>(BaseGameModeFragment.BOARD_SIZE_ARG_KEY)!!)

        startTimeMs = (boardSize.getSizeInHexagonalLayout() * MILLIS_PER_CARD * TIMER_SCALE_FACTOR).toLong()
        remainingTimeMs = startTimeMs
        _remainingSeconds.value = startTimeMs / ONE_SECOND_IN_MILLIS
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