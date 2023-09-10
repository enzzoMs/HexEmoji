package enzzom.hexemoji.ui.play.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameModeCard

// TODO
/**
 * The [PlayViewModel] class holds and is responsible for managing information related to the chosen
 * game mode, selected emoji categories, and board size within the HexEmoji game. It provides methods
 * for interacting with and managing these selections.
*/

class PlayViewModel : ViewModel() {
    private var gameMode: GameMode = GameMode.ZEN
    private var gameModeTitle: String = ""
    private var selectedBoardSize: BoardSize? = null
    private val selectedEmojiCategories = mutableSetOf<EmojiCategory>()

    private val _hasSelectedAnyCategory = MutableLiveData(false)
    val hasSelectedAnyCategory: LiveData<Boolean> = _hasSelectedAnyCategory

    private val _hasSelectedAllCategories = MutableLiveData(false)
    val hasSelectedAllCategories: LiveData<Boolean> = _hasSelectedAllCategories

    private val _hasSelectedBoardSize = MutableLiveData(false)
    val hasSelectedBoardSize: LiveData<Boolean> = _hasSelectedBoardSize

    fun selectGameMode(gameModeCard: GameModeCard) {
        gameModeTitle = gameModeCard.title
        gameMode = gameModeCard.gameMode

        selectedBoardSize = null
        selectedEmojiCategories.clear()
        _hasSelectedBoardSize.value = false
        _hasSelectedAnyCategory.value = false
        _hasSelectedAllCategories.value = false
    }

    fun getGameModeTitle(): String = gameModeTitle

    fun toggleEmojiCategorySelection(category: EmojiCategory) {
        if (category in selectedEmojiCategories) {
            selectedEmojiCategories.remove(category)
        } else {
            selectedEmojiCategories.add(category)
        }

        _hasSelectedAnyCategory.value = selectedEmojiCategories.isNotEmpty()

        // 'selectedEmojiCategories' is a set, so when all categories are selected its size should
        // be equal to the size of the enum 'EmojiCategory'
        _hasSelectedAllCategories.value = selectedEmojiCategories.size == EmojiCategory.values().size
    }

    fun isEmojiCategorySelected(category: EmojiCategory): Boolean = selectedEmojiCategories.contains(category)

    fun selectAllEmojiCategories() {
        selectedEmojiCategories.addAll(EmojiCategory.values())

        _hasSelectedAnyCategory.value = true
        _hasSelectedAllCategories.value = true
    }

    fun clearEmojiCategoriesSelection() {
        selectedEmojiCategories.clear()

        _hasSelectedAnyCategory.value = false
        _hasSelectedAllCategories.value = false
    }

    fun selectBoardSize(boardSize: BoardSize) {
        selectedBoardSize = boardSize
        _hasSelectedBoardSize.value = true
    }

    fun getSelectedBoardSize(): BoardSize? = selectedBoardSize

    fun isBoardSizeSelected(boardSize: BoardSize): Boolean = boardSize == selectedBoardSize

}