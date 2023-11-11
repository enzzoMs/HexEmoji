package enzzom.hexemoji.ui.fragments.game.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import kotlinx.coroutines.launch

/**
 * TODO
 */
class GameViewModel @AssistedInject constructor(
    private val emojiRepository: EmojiRepository,
    @Assisted val boardSize: BoardSize,
    @Assisted val selectedEmojiCategories: List<EmojiCategory>
) : ViewModel() {

    private var executeBoardEntryAnimation: Boolean = true
    private val numberOfEmojiCards = boardSize.getSizeInHexagonalLayout()
    private lateinit var emojis: List<Emoji>

    init {
        viewModelScope.launch {
            emojis = emojiRepository.getRandomUnlockedEmojis(selectedEmojiCategories, numberOfEmojiCards)
        }
    }

    fun shouldExecuteEntryAnimation(): Boolean = executeBoardEntryAnimation

    fun countdownFinished() {
        executeBoardEntryAnimation = false
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