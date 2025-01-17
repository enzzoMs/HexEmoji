package ems.hexemoji.ui.fragments.play.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ems.hexemoji.models.BoardSize
import ems.hexemoji.models.EmojiCategory
import ems.hexemoji.models.GameMode
import ems.hexemoji.models.GameModeDetails

/**
 * This class holds and is responsible for managing information related to the chosen game mode,
 * selected emoji categories and board size within the HexEmoji game. It provides methods
 * for interacting with and managing these selections.
*/
class PlayViewModel : ViewModel() {
    private var selectedGameMode: GameMode? = null
    private var selectedBoardSize: BoardSize? = null
    private val selectedCategories = mutableSetOf<EmojiCategory>()

    private val _hasSelectedGameMode = MutableLiveData(false)

    private val _hasSelectedAnyCategory = MutableLiveData(false)
    val hasSelectedAnyCategory: LiveData<Boolean> = _hasSelectedAnyCategory

    private val _hasSelectedAllCategories = MutableLiveData(false)
    val hasSelectedAllCategories: LiveData<Boolean> = _hasSelectedAllCategories

    private val _hasSelectedBoardSize = MutableLiveData(false)
    val hasSelectedBoardSize: LiveData<Boolean> = _hasSelectedBoardSize

    fun selectGameMode(gameModeDetails: GameModeDetails) {
        selectedGameMode = gameModeDetails.gameMode
        _hasSelectedGameMode.value = true
    }

    fun getSelectedGameMode(): GameMode? = selectedGameMode

    fun clearGameModeSelection() {
        selectedGameMode = null
        _hasSelectedGameMode.value = false
    }

    fun toggleCategorySelection(category: EmojiCategory) {
        if (category in selectedCategories) {
            selectedCategories.remove(category)
        } else {
            selectedCategories.add(category)
        }

        _hasSelectedAnyCategory.value = selectedCategories.isNotEmpty()

        // 'selectedEmojiCategories' is a set, so when all categories are selected its size should
        // be equal to the size of the enum 'EmojiCategory'
        _hasSelectedAllCategories.value = selectedCategories.size == EmojiCategory.entries.size
    }

    fun isCategorySelected(category: EmojiCategory): Boolean = selectedCategories.contains(category)

    fun selectAllCategories() {
        selectedCategories.addAll(EmojiCategory.entries.toTypedArray())

        _hasSelectedAnyCategory.value = true
        _hasSelectedAllCategories.value = true
    }

    fun clearCategoriesSelection() {
        selectedCategories.clear()

        _hasSelectedAnyCategory.value = false
        _hasSelectedAllCategories.value = false
    }

    fun getSelectedCategories(): List<EmojiCategory> = selectedCategories.toList()

    fun selectBoardSize(boardSize: BoardSize) {
        selectedBoardSize = boardSize
        _hasSelectedBoardSize.value = true
    }

    fun getSelectedBoardSize(): BoardSize? = selectedBoardSize

    fun isBoardSizeSelected(boardSize: BoardSize): Boolean = boardSize == selectedBoardSize

    fun clearBoardSizeSelection() {
        selectedBoardSize = null
        _hasSelectedBoardSize.value = false
    }
}