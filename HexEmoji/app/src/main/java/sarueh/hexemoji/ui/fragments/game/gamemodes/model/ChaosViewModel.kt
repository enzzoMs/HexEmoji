package sarueh.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import sarueh.hexemoji.data.entities.Challenge
import sarueh.hexemoji.data.entities.GeneralChallenge
import sarueh.hexemoji.data.entities.LimitedMovesChallenge
import sarueh.hexemoji.data.entities.TimedChallenge
import sarueh.hexemoji.data.repositories.ChallengesRepository
import sarueh.hexemoji.data.repositories.EmojiRepository
import sarueh.hexemoji.data.repositories.PreferencesRepository
import sarueh.hexemoji.data.repositories.StatisticsRepository
import sarueh.hexemoji.models.BoardSize
import sarueh.hexemoji.models.GameStatus
import sarueh.hexemoji.ui.fragments.game.BaseGameViewModel
import sarueh.hexemoji.utils.CountdownManager
import javax.inject.Inject

private const val ONE_SECOND_IN_MILLIS = 1000L

@HiltViewModel
class ChaosViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    statisticsRepository: StatisticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository, statisticsRepository, savedStateHandle
) {

    val countdownManager: CountdownManager

    val initialMoves: Int

    private val _remainingMoves = MutableLiveData<Int>()
    val remainingMoves: LiveData<Int> = _remainingMoves

    val shuffleIntervalMs: Long
    var shuffleTimerCurrentMs: Long

    init {
        val (startTimeSeconds, initialMovesCount, shuffleIntervalSeconds) = when (boardSize) {
            BoardSize.BOARD_2_BY_4 -> Triple(15, 10, 3)
            BoardSize.BOARD_3_BY_4 -> Triple(30, 16, 3)
            BoardSize.BOARD_4_BY_4 -> Triple(45, 26, 6)
            BoardSize.BOARD_4_BY_7 -> Triple(90, 64, 6)
            BoardSize.BOARD_4_BY_8 -> Triple(100, 75, 6)
            BoardSize.BOARD_5_BY_8 -> Triple(160, 92, 10)
            BoardSize.BOARD_7_BY_9 -> Triple(290, 180, 10)
            BoardSize.BOARD_8_BY_7 -> Triple(270, 150, 14)
            BoardSize.BOARD_8_BY_8 -> Triple(320, 180, 14)
            BoardSize.BOARD_6_BY_8 -> Triple(210, 110, 14)
            BoardSize.BOARD_9_BY_8 -> Triple(420, 235, 18)
            BoardSize.BOARD_9_BY_9 -> Triple(460, 250, 18)
        }

        initialMoves = initialMovesCount
        _remainingMoves.value = initialMovesCount

        countdownManager = CountdownManager(
            startTimeSeconds * ONE_SECOND_IN_MILLIS,
            onCountdownFinished = {
                if (getGameStatus() == GameStatus.IN_PROGRESS) {
                    setGameStatus(GameStatus.DEFEAT)
                }
            }
        )

        shuffleIntervalMs = shuffleIntervalSeconds * ONE_SECOND_IN_MILLIS
        shuffleTimerCurrentMs = shuffleIntervalMs
    }

    /**
     * @return The board positions of the shuffled pair
     */
    fun shufflePair(): Pair<Int, Int>? {
        val availableCards = emojiCards?.filter { !it.flipped }?.toMutableList()

        return if (availableCards == null || availableCards.size < 2) {
            null
        } else {
            val firstCard = availableCards.random()
            availableCards.remove(firstCard)
            val secondCard = availableCards.random()

            val firstCardPosition = firstCard.positionInBoard
            val secondCardPosition = secondCard.positionInBoard

            val temp = emojiCards!![firstCardPosition]
            emojiCards!![firstCardPosition] = emojiCards!![secondCardPosition]
            emojiCards!![secondCardPosition] = temp

            firstCard.positionInBoard = secondCardPosition
            secondCard.positionInBoard = firstCardPosition

            Pair(firstCardPosition, secondCardPosition)
        }
    }

    override fun flipCard(cardPosition: Int): FlipResult {
        val flipResult = super.flipCard(cardPosition)

        if (_remainingMoves.value == 1 && getGameStatus() == GameStatus.IN_PROGRESS) {
            setGameStatus(GameStatus.DEFEAT)
        }

        _remainingMoves.value = _remainingMoves.value!! - 1

        return flipResult
    }

    override fun shouldUpdateChallengeOnVictory(challenge: Challenge): Boolean {
        return when (challenge) {
            is GeneralChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (challenge.boardSize == null || challenge.boardSize == boardSize) &&
                ((challenge.constrainedToCategory && challenge.category in selectedCategories)
                    || !challenge.constrainedToCategory)
            is TimedChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode && (
                    countdownManager.startTimeMs - (countdownManager.remainingSeconds.value!! * ONE_SECOND_IN_MILLIS)
                ) / ONE_SECOND_IN_MILLIS <= challenge.timeLimitInSeconds
            is LimitedMovesChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (initialMoves - _remainingMoves.value!!) <= challenge.moveLimit
            else -> false
        }
    }

    override fun shouldExecuteExitAnimation(): Boolean {
        return _remainingMoves.value!! == 0 || emojiCards!!.count { !it.matched } == 0
    }
}