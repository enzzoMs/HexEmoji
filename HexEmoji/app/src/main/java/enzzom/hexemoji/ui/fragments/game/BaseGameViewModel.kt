package enzzom.hexemoji.ui.fragments.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GameStatistic
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.data.repositories.StatisticsRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCard
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.models.WeekDay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private const val MIN_GAMES_UNTIL_REVIEW = 4

/**
 * Base ViewModel for managing game-related logic and data.
 * This class provides the basic set of methods needed to handle game functionality, including
 * card interaction, updating game status, managing challenges, and updating game statistics.
 */
abstract class BaseGameViewModel(
    private val emojiRepository: EmojiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val challengesRepository: ChallengesRepository,
    private val statisticsRepository: StatisticsRepository,
    protected val boardSize: BoardSize,
    protected val gameMode: GameMode,
    protected val selectedCategories: List<EmojiCategory>
) : ViewModel() {

    private var gameStatus: GameStatus = GameStatus.STARTING

    private var executeEntryAnimation = true
    private var showAppReviewDialog = false

    private var allChallenges: List<Challenge> = listOf()

    protected var emojiCards: MutableList<EmojiCard>? = null
    private var lastFlippedCard: EmojiCard? = null

    private var numOfPairsFound = 0

    init {
        viewModelScope.launch {
            val numberOfEmojiCards = boardSize.getSizeInHexagonalLayout()

            val gameEmojis = emojiRepository.getRandomUnlockedEmojis(
                selectedCategories, numberOfEmojiCards / 2
            ).toMutableList().apply {
                // Duplicating the emojis to make pairs
                addAll(this)
                shuffle()
            }

            emojiCards = gameEmojis.mapIndexed { index, emoji -> EmojiCard(emoji, index) }.toMutableList()

            allChallenges = challengesRepository.getAllChallenges()

            showAppReviewDialog = statisticsRepository.countAllGameStatistics() == MIN_GAMES_UNTIL_REVIEW
        }
    }

    /**
     * Flips the card at the specified position and determines the result of the flip.
     * Can be overridden in subclasses if additional operations are needed before or after flipping the card.
     */
    open fun flipCard(cardPosition: Int): FlipResult {
        val emojiCard = getCardForPosition(cardPosition)

        emojiCard?.flipped = true

        return if (emojiCard == null) {
            FlipResult.NoMatch

        } else if (lastFlippedCard == null) {
            lastFlippedCard = emojiCard
            FlipResult.NoMatch

        } else if (lastFlippedCard!!.emoji == emojiCard.emoji) {
            FlipResult.MatchSuccessful(lastFlippedCard!!.positionInBoard, emojiCard.positionInBoard).also {
                lastFlippedCard!!.matched = true
                emojiCard.matched = true
                lastFlippedCard = null

                numOfPairsFound++

                if (emojiCards!!.none { !it.matched } && getGameStatus() == GameStatus.IN_PROGRESS) {
                    setGameStatus(GameStatus.VICTORY)
                }
            }

        } else {
            FlipResult.MatchFailed(lastFlippedCard!!.positionInBoard, emojiCard.positionInBoard).also {
                lastFlippedCard!!.flipped = false
                emojiCard.flipped = false
                lastFlippedCard = null
            }
        }
    }

    fun getGameStatus(): GameStatus = gameStatus

    fun getGameBoardSize(): BoardSize = boardSize

    fun getPendingChallengesCount(): Int = allChallenges.count { !it.completed }

    fun getChallengesInProgressCount(): Int = allChallenges.count { !it.completed && it.completedGames != 0 }

    fun getCompletedChallengesCount(): Int = allChallenges.count { it.completed }

    fun getCardForPosition(position: Int): EmojiCard? = emojiCards?.get(position)

    fun isCardFlipped(position: Int): Boolean = emojiCards?.get(position)?.flipped ?: false

    fun shouldShowAppReview(): Boolean = showAppReviewDialog

    fun shouldExecuteEntryAnimation(): Boolean = executeEntryAnimation

    fun shouldShowBoardTutorial(): Boolean = preferencesRepository.getBoolean(
        PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, true
    )

    /**
     * Returns whether the exit animation should be executed. In general, this decision is tied
     * to the winning conditions of the game mode. The default implementation returns
     * 'True' only when all cards have benn successfully matched.
     */
    open fun shouldExecuteExitAnimation(): Boolean = emojiCards!!.count { !it.matched } == 0

    fun entryAnimationFinished() {
        executeEntryAnimation = false
        gameStatus = GameStatus.IN_PROGRESS
    }

    fun boardTutorialShown() {
        preferencesRepository.putBoolean(
            PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, false
        )
    }

    /**
     * Determines whether a challenge should be updated upon victory in the game. It can be customized
     * if the game mode has exclusive challenges.
     */
    open fun shouldUpdateChallengeOnVictory(challenge: Challenge): Boolean {
        return when (challenge) {
            is GeneralChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (challenge.boardSize == null || challenge.boardSize == boardSize) &&
                ((challenge.constrainedToCategory && challenge.category in selectedCategories)
                    || !challenge.constrainedToCategory)
            else -> false
        }
    }


    protected fun setGameStatus(status: GameStatus) {
        gameStatus = status

        if (status == GameStatus.VICTORY || status == GameStatus.DEFEAT) {
            updateChallengesProgress()
            updateGameStatistics()
        }
    }

    private fun updateChallengesProgress() {
        viewModelScope.launch {
            if (gameStatus == GameStatus.VICTORY) {
                challengesRepository.incrementChallengesCompletion(
                    allChallenges.filter(::shouldUpdateChallengeOnVictory)
                )

            } else if (gameStatus == GameStatus.DEFEAT) {
                challengesRepository.resetChallengesCompletion(allChallenges.filter {
                    it is GeneralChallenge && it.consecutiveGames &&
                    !it.completed && it.gameMode == gameMode &&
                    (it.boardSize == null || it.boardSize == boardSize) &&
                    ((it.constrainedToCategory && it.category in selectedCategories) || !it.constrainedToCategory)
                })
            }

            allChallenges = challengesRepository.getAllChallenges()
        }
    }

    private fun updateGameStatistics() {
        val currentDate = LocalDateTime.now()

        viewModelScope.launch {
            statisticsRepository.insertGameStatistic(
                GameStatistic(
                    victory = gameStatus == GameStatus.VICTORY,
                    numOfPairsFound = numOfPairsFound,
                    gameMode = gameMode,
                    boardSize = boardSize,
                    weekDay = WeekDay.valueOf(currentDate.dayOfWeek.name),
                    day = currentDate.dayOfMonth,
                    month = currentDate.monthValue
                )
            )
        }
    }

    sealed class FlipResult {
        data object NoMatch : FlipResult()
        data class MatchSuccessful(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
        data class MatchFailed(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
    }
}