package enzzom.hexemoji.ui.fragments.emojis.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.entities.Emoji
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

    private var allEmojisByCategory: Map<EmojiCategory, List<Emoji>>? = null
    private var categoriesUnlockedCount: Map<EmojiCategory, Int>? = null
    private var categoriesEmojiCount: Map<EmojiCategory, Int>? = null

    init {
        viewModelScope.launch {
            allEmojisByCategory = emojiRepository.getAllEmojisByCategory()

            categoriesEmojiCount = allEmojisByCategory!!.mapValues { it.value.size }
            categoriesUnlockedCount = allEmojisByCategory!!.mapValues { it.value.count { emoji -> emoji.unlocked }  }

            _loadingCategoriesInfo.value = false
        }

    }

    fun getUnlockedCountForCategory(category: EmojiCategory): Int? = categoriesUnlockedCount?.get(category)

    fun getEmojiCountForCategory(category: EmojiCategory): Int? = categoriesEmojiCount?.get(category)

    fun getCategoryEmojis(category: EmojiCategory): List<Emoji>? = allEmojisByCategory?.get(category)
}