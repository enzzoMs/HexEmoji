package sarueh.hexemoji.ui.fragments.emojis.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import sarueh.hexemoji.data.entities.Challenge
import sarueh.hexemoji.data.entities.Emoji
import sarueh.hexemoji.data.entities.GeneralChallenge
import sarueh.hexemoji.data.entities.LimitedMovesChallenge
import sarueh.hexemoji.data.entities.TimedChallenge
import sarueh.hexemoji.data.repositories.ChallengesRepository
import sarueh.hexemoji.data.repositories.EmojiRepository
import sarueh.hexemoji.models.BoardSize
import sarueh.hexemoji.models.EmojiCategory
import sarueh.hexemoji.models.GameMode
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.random.Random

private const val MIN_LOADING_TIME_MS = 500L

private const val MAX_CHALLENGES_PER_CATEGORY = 5
private const val MAX_GAMES_PER_CHALLENGE = 5

private const val NUM_OF_CHALLENGE_TYPES = 3

/**
 * This class is responsible for managing data related to the categories collections and their
 * challenges. It provides access to this data and handles operations to load, complete, and create
 * new challenges when needed.
 */
@HiltViewModel
class EmojisViewModel @Inject constructor(
    private val emojiRepository: EmojiRepository,
    private val challengesRepository: ChallengesRepository
) : ViewModel() {

    private val _collectionsLoadingFinished = MutableLiveData(false)
    val collectionsLoadingFinished: LiveData<Boolean> = _collectionsLoadingFinished

    private val _challengesLoading = MutableLiveData(true)
    val challengesLoading: LiveData<Boolean> = _challengesLoading

    private val _currentChallenges = MutableLiveData<List<Challenge>?>(null)
    val currentChallenges: LiveData<List<Challenge>?> = _currentChallenges

    private var currentCategory: EmojiCategory? = null
    private val loadChallengesTimer = object : CountDownTimer(MIN_LOADING_TIME_MS, MIN_LOADING_TIME_MS) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            _challengesLoading.value = false
            _currentChallenges.value = allChallengesByCategory?.get(currentCategory).let {
                if (it.isNullOrEmpty()) null else it
            }
        }
    }

    private var allEmojisByCategory: MutableMap<EmojiCategory, List<Emoji>>? = null
    private var allChallengesByCategory: MutableMap<EmojiCategory, List<Challenge>>? = null

    private var categoriesUnlockedCount: MutableMap<EmojiCategory, Int>? = null
    private var categoriesEmojiCount: Map<EmojiCategory, Int>? = null

    init {
        viewModelScope.launch {
            allChallengesByCategory = challengesRepository.getAllChallengesByCategory().toMutableMap()

            allEmojisByCategory = emojiRepository.getAllEmojisByCategory().toMutableMap()

            categoriesEmojiCount = allEmojisByCategory!!.mapValues { it.value.size }
            categoriesUnlockedCount = allEmojisByCategory!!.mapValues {
                it.value.count { emoji -> emoji.unlocked }
            }.toMutableMap()

            _collectionsLoadingFinished.value = true
        }
    }

    /**
     * Initiates the loading state, fetches challenges for a given category, and updates 'currentChallenges'
     * with the result.
     * @see challengesLoading
     * @see currentChallenges
     */
    fun loadChallengesForCategory(category: EmojiCategory) {
        _challengesLoading.value = true

        loadChallengesTimer.cancel()
        currentCategory = category
        loadChallengesTimer.start()
    }

    /**
     * Generates a complete new set of challenges for a given category, replacing ALL the old ones.
     * @return 'True' if the operation was successful, 'False' otherwise.
     */
    fun refreshChallenges(category: EmojiCategory): Boolean {
        if (allEmojisByCategory == null || allChallengesByCategory == null) {
            return false
        }

        val lockedEmojisForCategory = allEmojisByCategory!![category]!!.filter { !it.unlocked }

        val newChallenges = generateChallenges(
            category = category,
            count = min(lockedEmojisForCategory.size, MAX_CHALLENGES_PER_CATEGORY),
            availableEmojis = lockedEmojisForCategory
        )

        val oldChallenges = allChallengesByCategory!![category]!!

        viewModelScope.launch {
            allChallengesByCategory!![category] = challengesRepository.replaceChallenges(
                category, oldChallenges, newChallenges
            )
        }

        return true
    }

    /**
     * Completes a given challenge, unlocking the rewards and generating a new challenge if necessary.
     * @return 'True' if the operation was successful, 'False' otherwise.
     */
    fun completeChallenge(completedChallenge: Challenge): Boolean {
        if (allEmojisByCategory == null || allChallengesByCategory == null) {
            return false
        }

        categoriesUnlockedCount!!.compute(completedChallenge.category) { _, count -> count!!.plus(1) }

        allEmojisByCategory!!.compute(completedChallenge.category) { _, emojiList ->
            emojiList!!.map {
                if (it.unicode == completedChallenge.rewardEmojiUnicode) it.copy(unlocked = true) else it
            }
        }

        allChallengesByCategory!!.compute(completedChallenge.category) { _, challenges ->
            challenges!!.minus(completedChallenge)
        }

        viewModelScope.launch {
            emojiRepository.unlockEmoji(completedChallenge.rewardEmojiUnicode)

            val emojisAlreadyInChallenges = allChallengesByCategory!![completedChallenge.category]!!.map {
                it.rewardEmojiUnicode
            }

            val availableEmojisForCategory = allEmojisByCategory!![completedChallenge.category]!!.filter {
                !it.unlocked && !emojisAlreadyInChallenges.contains(it.unicode)
            }

            val newChallenge = if (availableEmojisForCategory.isNotEmpty()) {
                generateChallenges(completedChallenge.category, 1, availableEmojisForCategory)
            } else {
                null
            }

            allChallengesByCategory!![completedChallenge.category] = challengesRepository.replaceChallenges(
                completedChallenge.category, listOf(completedChallenge), newChallenge
            )
        }

        return true
    }

    fun getEmojiByUnicode(unicode: String, category: EmojiCategory): Emoji? {
        return allEmojisByCategory?.get(category)?.find { it.unicode == unicode }
    }

    fun getUnlockedCountForCategory(category: EmojiCategory): Int? = categoriesUnlockedCount?.get(category)

    fun getEmojiCountForCategory(category: EmojiCategory): Int? = categoriesEmojiCount?.get(category)

    fun getCategoryEmojis(category: EmojiCategory): List<Emoji>? = allEmojisByCategory?.get(category)

    private fun generateChallenges(
        category: EmojiCategory, count: Int, availableEmojis: List<Emoji>
    ): List<Challenge> {
        val rewardEmojis = generateSequence {
            availableEmojis.random().unicode
        }.distinct()
        .take(count)
        .toList()

        val constraintChanceRange = 0..6
        val timedChallengeRange = 10..50 step 10
        val limitedMovesChallengeRange = 10..50 step 10

        val challengesChangeRange = 0..4

        return List(count) { index ->
            when (challengesChangeRange.random()) {
                0 -> TimedChallenge(
                    totalGames = (1..MAX_GAMES_PER_CHALLENGE).random(),
                    completedGames = 0,
                    category = category,
                    rewardEmojiUnicode = rewardEmojis[index],
                    gameMode = if (Random.nextInt(3) == 0) GameMode.AGAINST_THE_CLOCK else GameMode.CHAOS,
                    timeLimitInSeconds = timedChallengeRange.shuffled().first()
                )
                1 -> LimitedMovesChallenge(
                    totalGames = (1..MAX_GAMES_PER_CHALLENGE).random(),
                    completedGames = 0,
                    category = category,
                    rewardEmojiUnicode = rewardEmojis[index],
                    gameMode = if (Random.nextInt(3) == 0) GameMode.LIMITED_MOVES else GameMode.CHAOS,
                    moveLimit = limitedMovesChallengeRange.shuffled().first()
                )
                else -> GeneralChallenge(
                    totalGames = (1..MAX_GAMES_PER_CHALLENGE).random(),
                    completedGames = 0,
                    category = category,
                    rewardEmojiUnicode = rewardEmojis[index],
                    gameMode = GameMode.entries.random(),
                    boardSize = if (constraintChanceRange.random() == 0) BoardSize.entries.random() else null,
                    consecutiveGames = constraintChanceRange.random() == 0,
                    constrainedToCategory = Random.nextBoolean()
                )
            }
        }
    }
}

