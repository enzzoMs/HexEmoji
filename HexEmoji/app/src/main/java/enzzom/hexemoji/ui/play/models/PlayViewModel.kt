package enzzom.hexemoji.ui.play.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameModeCard

/**
 * [PlayViewModel] holds information about the chosen game mode and selected emoji categories, providing
 * methods for managing both.
 */
class PlayViewModel : ViewModel() {
    private var gameMode: GameMode = GameMode.ZEN
    private var gameModeTitle: String = ""
    private val selectedEmojiCategories = mutableSetOf<EmojiCategory>()

    private val _hasSelectedAnyCategory = MutableLiveData(false)
    val hasSelectedAnyCategory: LiveData<Boolean> = _hasSelectedAnyCategory

    private val _hasSelectedAllCategories = MutableLiveData(false)
    val hasSelectedAllCategories: LiveData<Boolean> = _hasSelectedAllCategories

    fun selectGameMode(gameModeCard: GameModeCard) {
        gameModeTitle = gameModeCard.title
        gameMode = gameModeCard.gameMode

        selectedEmojiCategories.clear()
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
}