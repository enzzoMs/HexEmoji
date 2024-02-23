package enzzom.hexemoji.ui.fragments.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCard
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameStatus
import kotlinx.coroutines.launch

/**
 * TODO
 */
abstract class BaseGameViewModel(
    private val emojiRepository: EmojiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val challengesRepository: ChallengesRepository,
    private val boardSize: BoardSize,
    private val gameMode: GameMode,
    private val selectedCategories: List<EmojiCategory>
) : ViewModel() {

    private var gameStatus: GameStatus = GameStatus.STARTING
    private var executeEntryAnimation: Boolean = true

    private var allChallenges: List<Challenge> = listOf()

    private var emojiCards: List<EmojiCard>? = null
    private var lastFlippedCard: EmojiCard? = null

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

            emojiCards = gameEmojis.mapIndexed { index, emoji -> EmojiCard(emoji, index) }

            allChallenges = challengesRepository.getAllChallenges()
        }
    }

    fun flipCard(cardPosition: Int): FlipResult {
        val emojiCard = getCardForPosition(cardPosition)

        emojiCard?.flipped = true

        return if (emojiCard == null) {
            FlipResult.NoMatch

        } else if (lastFlippedCard == null) {
            lastFlippedCard = emojiCard
            FlipResult.NoMatch

        } else if (lastFlippedCard!!.emoji == emojiCard.emoji) {
            FlipResult.MatchSuccessful(lastFlippedCard!!.positionInBoard, emojiCard.positionInBoard)
                .also {
                lastFlippedCard!!.matched = true
                emojiCard.matched = true
                lastFlippedCard = null

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

    fun getRemainingCardsCount(): Int = emojiCards!!.count { !it.matched }

    fun getGameStatus(): GameStatus = gameStatus

    fun getBoardSize(): BoardSize = boardSize

    fun getPendingChallengesCount(): Int = allChallenges.count { !it.completed }

    fun getChallengesInProgressCount(): Int = allChallenges.count { !it.completed && it.completedGames != 0 }

    fun getCompletedChallengesCount(): Int = allChallenges.count { it.completed }

    fun getCardForPosition(position: Int): EmojiCard? = emojiCards?.get(position)

    fun isCardFlipped(position: Int): Boolean = emojiCards?.get(position)?.flipped ?: false

    fun shouldExecuteEntryAnimation(): Boolean = executeEntryAnimation

    fun shouldShowBoardTutorial(): Boolean = preferencesRepository.getBoolean(
        PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, true
    )

    fun entryAnimationFinished() {
        executeEntryAnimation = false
        gameStatus = GameStatus.IN_PROGRESS
    }

    fun boardTutorialShown() {
        preferencesRepository.putBoolean(
            PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, false
        )
    }

    protected fun setGameStatus(status: GameStatus) {
        gameStatus = status

        if (status == GameStatus.VICTORY || status == GameStatus.DEFEAT) {
            updateChallengesProgress()
        }
    }

    private fun updateChallengesProgress() {
        if (gameStatus == GameStatus.VICTORY) {
            val completedChallenges = allChallenges.filter { challenge ->
                when (challenge) {
                    is GeneralChallenge ->
                        !challenge.completed &&
                                challenge.gameMode == gameMode &&
                                (challenge.boardSize == null || challenge.boardSize == boardSize) &&
                                ((challenge.constrainedToCategory && challenge.category in selectedCategories)
                                        || !challenge.constrainedToCategory)
                    else -> false
                }
            }

            viewModelScope.launch {
                challengesRepository.incrementChallengesCompletion(completedChallenges)
            }

        } else if (gameStatus == GameStatus.DEFEAT) {
            val failedChallenges = allChallenges.filter { challenge ->
                if (challenge is GeneralChallenge) {
                    challenge.consecutiveGames
                } else {
                    false
                }
            }

            viewModelScope.launch {
                challengesRepository.resetChallengesCompletion(failedChallenges)
            }
        }
    }

    sealed class FlipResult {
        data object NoMatch : FlipResult()
        data class MatchSuccessful(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
        data class MatchFailed(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
    }
}