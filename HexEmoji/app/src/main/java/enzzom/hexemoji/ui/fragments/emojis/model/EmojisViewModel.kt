package enzzom.hexemoji.ui.fragments.emojis.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.models.EmojiCategory
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * TODO
 */
@HiltViewModel
class EmojisViewModel @Inject constructor(
    private val emojiRepository: EmojiRepository
) : ViewModel() {

    private val _loadingCategoriesInfo = MutableLiveData(true)
    val loadingCategoriesInfo: LiveData<Boolean> = _loadingCategoriesInfo

    private lateinit var categoriesUnlockedCount: Map<EmojiCategory, Int>
    private lateinit var categoriesEmojiCount: Map<EmojiCategory, Int>

    init {
        viewModelScope.launch {
            categoriesUnlockedCount = emojiRepository.getUnlockedCountForCategories(EmojiCategory.values().asList())
            categoriesEmojiCount = emojiRepository.getEmojiCountForCategories(EmojiCategory.values().asList())

            _loadingCategoriesInfo.value = false
        }
    }

    fun getUnlockedCountForCategory(category: EmojiCategory): Int? {
        return if (_loadingCategoriesInfo.value!!) null else categoriesUnlockedCount[category]
    }

    fun getEmojiCountForCategory(category: EmojiCategory): Int? {
        return if (_loadingCategoriesInfo.value!!) null else categoriesEmojiCount[category]
    }
}