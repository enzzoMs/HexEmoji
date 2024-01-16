package enzzom.hexemoji.ui.fragments.game.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCard
import enzzom.hexemoji.models.EmojiCategory
import kotlinx.coroutines.launch

/**
 * TODO
 */
class GameViewModel @AssistedInject constructor(
    private val emojiRepository: EmojiRepository,
    private val preferencesRepository: PreferencesRepository,
    @Assisted val boardSize: BoardSize,
    @Assisted val selectedEmojiCategories: List<EmojiCategory>,
) : ViewModel() {

    private var currentEmojiPair = Pair<EmojiCard?, EmojiCard?>(null, null)
    private var executeBoardEntryAnimation: Boolean = true
    private val numberOfEmojiCards = boardSize.getSizeInHexagonalLayout()
    private var emojiCards: List<EmojiCard>? = null

    init {
        viewModelScope.launch {
            emojiCards = getGameEmojis().mapIndexed { index, emoji -> EmojiCard(emoji, index) }
        }
    }

    fun getEmojiCardForPosition(position: Int): EmojiCard? = if (emojiCards != null) emojiCards!![position] else null

    fun flipEmojiCard(cardPosition: Int): FlipResult {
        val emojiCard = getEmojiCardForPosition(cardPosition)

        emojiCard?.apply {
            flipped = !flipped
        }

        return if (emojiCard == null || !emojiCard.flipped) {
            FlipResult.NoMatch

        } else if (
            (currentEmojiPair.first == null && currentEmojiPair.second == null) ||
            (currentEmojiPair.first != null && currentEmojiPair.second != null)
        ) {
            currentEmojiPair = Pair(emojiCard, null)
            FlipResult.NoMatch

        } else if (currentEmojiPair.first!!.emoji == emojiCard.emoji) {
            currentEmojiPair = Pair(currentEmojiPair.first, emojiCard).also {
                it.first!!.matched = true
                it.second.matched = true
            }

            FlipResult.MatchSuccessful(currentEmojiPair.first!!.positionInBoard, currentEmojiPair.second!!.positionInBoard)

        } else {
            FlipResult.MatchFailed(currentEmojiPair.first!!.positionInBoard, emojiCard.positionInBoard).also {
                currentEmojiPair = Pair(null, null)
            }
        }
    }

    fun isEmojiCardFlipped(position: Int): Boolean = emojiCards?.get(position)?.flipped ?: false

    fun shouldShowBoardTutorial(): Boolean = preferencesRepository.getBoolean(
        PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, true
    )

    fun boardTutorialFinished() {
        preferencesRepository.putBoolean(
            PreferencesRepository.PREFERENCE_KEY_SHOW_BOARD_TUTORIAL, false
        )
    }

    fun shouldExecuteEntryAnimation(): Boolean = executeBoardEntryAnimation

    fun boardEntryAnimationFinished() {
        executeBoardEntryAnimation = false
    }

    private suspend fun getGameEmojis(): List<String> {
        val firstSetOfEmojis = emojiRepository.getRandomUnlockedEmojis(
            selectedEmojiCategories, numberOfEmojiCards / 2
        ).toMutableList()

        return firstSetOfEmojis.apply {
            // Duplicating the emojis to make pairs
            addAll(firstSetOfEmojis)
            shuffle()
        }
    }

    sealed class FlipResult {
        data object NoMatch : FlipResult()
        data class MatchSuccessful(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
        data class MatchFailed(val firstCardPosition: Int, val secondCardPosition: Int) : FlipResult()
    }

    @AssistedFactory
    interface Factory {
        fun create(boardSize: BoardSize, selectedEmojiCategories: List<EmojiCategory>): GameViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            factory: Factory, boardSize: BoardSize, selectedEmojiCategories: List<EmojiCategory>
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(boardSize, selectedEmojiCategories) as T
                }
            }
        }
    }
}