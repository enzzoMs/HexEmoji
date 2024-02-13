package enzzom.hexemoji.ui.fragments.game.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCard
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.ui.fragments.game.GameFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * TODO
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val emojiRepository: EmojiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var executeEntryAnimation: Boolean = true

    private val boardSize = savedStateHandle.get<BoardSize>(GameFragment.BOARD_SIZE_ARG_KEY)!!

    private var emojiCards: List<EmojiCard>? = null
    private var lastFlippedCard: EmojiCard? = null

    init {
        viewModelScope.launch {
            val numberOfEmojiCards = boardSize.getSizeInHexagonalLayout()

            val selectedCategories = savedStateHandle.get<Array<String>>(
                GameFragment.SELECTED_CATEGORIES_ARG_KEY
            )!!.map { EmojiCategory.valueOf(it) }

            val gameEmojis = emojiRepository.getRandomUnlockedEmojis(
                selectedCategories, numberOfEmojiCards / 2
            ).toMutableList().apply {
                // Duplicating the emojis to make pairs
                addAll(this)
                shuffle()
            }

            emojiCards = gameEmojis.mapIndexed { index, emoji -> EmojiCard(emoji, index) }
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
            FlipResult.MatchSuccessful(lastFlippedCard!!.positionInBoard, emojiCard.positionInBoard).also {
                lastFlippedCard!!.matched = true
                emojiCard.matched = true
                lastFlippedCard = null
            }

        } else {
            FlipResult.MatchFailed(lastFlippedCard!!.positionInBoard, emojiCard.positionInBoard).also {
                lastFlippedCard!!.flipped = false
                emojiCard.flipped = false
                lastFlippedCard = null
            }
        }
    }

    fun getBoardSize(): BoardSize = boardSize

    fun getCardForPosition(position: Int): EmojiCard? = emojiCards?.get(position)

    fun isCardFlipped(position: Int): Boolean = emojiCards?.get(position)?.flipped ?: false

    fun shouldExecuteEntryAnimation(): Boolean = executeEntryAnimation

    fun shouldShowBoardTutorial(): Boolean = preferencesRepository.getBoolean(
        PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, true
    )

    fun entryAnimationFinished() {
        executeEntryAnimation = false
    }

    fun boardTutorialShown() {
        preferencesRepository.putBoolean(
            PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, false
        )
    }

    sealed class FlipResult {
        data object NoMatch : FlipResult()
        data class MatchSuccessful(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
        data class MatchFailed(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
    }
}