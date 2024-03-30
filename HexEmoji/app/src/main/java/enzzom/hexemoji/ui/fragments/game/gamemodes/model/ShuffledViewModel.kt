package enzzom.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.data.repositories.StatisticsRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject
import kotlin.random.Random

private const val ONE_SECOND_IN_MILLIS = 1000L

@HiltViewModel
class ShuffledViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    statisticsRepository: StatisticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository, statisticsRepository,
    BoardSize.valueOf(savedStateHandle.get<String>(BaseGameModeFragment.BOARD_SIZE_ARG_KEY)!!),
    GameMode.valueOf(savedStateHandle.get<String>(BaseGameModeFragment.GAME_MODE_ARG_KEY)!!),
    savedStateHandle.get<Array<String>>(BaseGameModeFragment.SELECTED_CATEGORIES_ARG_KEY)!!.map {
        EmojiCategory.valueOf(it)
    }
) {
    val shuffleIntervalMs = when (boardSize) {
        BoardSize.BOARD_2_BY_4, BoardSize.BOARD_3_BY_4 -> 3
        BoardSize.BOARD_4_BY_4, BoardSize.BOARD_4_BY_7, BoardSize.BOARD_4_BY_8 -> 6
        BoardSize.BOARD_5_BY_8, BoardSize.BOARD_7_BY_9 -> 10
        BoardSize.BOARD_8_BY_7, BoardSize.BOARD_8_BY_8, BoardSize.BOARD_6_BY_8 -> 14
        BoardSize.BOARD_9_BY_8, BoardSize.BOARD_9_BY_9 -> 18
    } * ONE_SECOND_IN_MILLIS

    var shuffleTimerCurrentMs: Long = shuffleIntervalMs

    /**
     * @return The board positions of the shuffled pair
     */
    fun shufflePair(): Pair<Int, Int>? {
        val availableCards = emojiCards?.filter { !it.flipped }?.toMutableList()

        return if (availableCards == null || availableCards.size < 2) {
            null
        } else {
            val firstCard = availableCards.random()
            availableCards.remove(firstCard)
            val secondCard = availableCards.random()

            val firstCardPosition = firstCard.positionInBoard
            val secondCardPosition = secondCard.positionInBoard

            val temp = emojiCards!![firstCardPosition]
            emojiCards!![firstCardPosition] = emojiCards!![secondCardPosition]
            emojiCards!![secondCardPosition] = temp

            firstCard.positionInBoard = secondCardPosition
            secondCard.positionInBoard = firstCardPosition

            Pair(firstCardPosition, secondCardPosition)
        }
    }
}
