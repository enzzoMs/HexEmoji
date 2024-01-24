package enzzom.hexemoji.ui.fragments.emojis.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.models.EmojiCategory
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_LOADING_TIME_MS = 700L

/**
 * TODO
 */
@HiltViewModel
class EmojisViewModel @Inject constructor(
    private val emojiRepository: EmojiRepository,
    private val challengesRepository: ChallengesRepository
) : ViewModel() {

    private val _categoriesLoadingFinished = MutableLiveData(false)
    private val _minLoadingTimeFinished = MutableLiveData(false)

    private val _collectionsLoadingFinished = MediatorLiveData(false).also {
        it.addSource(_categoriesLoadingFinished) { finished -> it.value = finished && _minLoadingTimeFinished.value!! }
        it.addSource(_minLoadingTimeFinished) { finished -> it.value = finished && _categoriesLoadingFinished.value!! }
    }
    val collectionsLoadingFinished: LiveData<Boolean> = _collectionsLoadingFinished

    private var allEmojisByCategory: Map<EmojiCategory, List<Emoji>>? = null
    private var allChallengesByCategory: Map<EmojiCategory, List<Challenge>>? = null

    private var categoriesUnlockedCount: Map<EmojiCategory, Int>? = null
    private var categoriesEmojiCount: Map<EmojiCategory, Int>? = null


    init {
        // Countdown to ensure a minimum time before allowing UI updates
        object : CountDownTimer(MIN_LOADING_TIME_MS, MIN_LOADING_TIME_MS) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                _minLoadingTimeFinished.value = true
            }
        }.start()

        viewModelScope.launch {
            allChallengesByCategory = challengesRepository.getAllChallengesByCategory()

            allEmojisByCategory = emojiRepository.getAllEmojisByCategory()

            categoriesEmojiCount = allEmojisByCategory!!.mapValues { it.value.size }
            categoriesUnlockedCount = allEmojisByCategory!!.mapValues { it.value.count { emoji -> emoji.unlocked }  }

            _categoriesLoadingFinished.value = true
        }
    }

    fun getChallengesForCategory(category: EmojiCategory): List<Challenge>? = allChallengesByCategory?.get(category)

    fun getUnlockedCountForCategory(category: EmojiCategory): Int? = categoriesUnlockedCount?.get(category)

    fun getEmojiCountForCategory(category: EmojiCategory): Int? = categoriesEmojiCount?.get(category)

    fun getCategoryEmojis(category: EmojiCategory): List<Emoji>? = allEmojisByCategory?.get(category)
}