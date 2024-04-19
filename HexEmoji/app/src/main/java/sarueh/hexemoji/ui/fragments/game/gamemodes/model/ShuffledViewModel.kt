package sarueh.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import sarueh.hexemoji.data.repositories.ChallengesRepository
import sarueh.hexemoji.data.repositories.EmojiRepository
import sarueh.hexemoji.data.repositories.PreferencesRepository
import sarueh.hexemoji.data.repositories.StatisticsRepository
import sarueh.hexemoji.models.BoardSize
import sarueh.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject

private const val ONE_SECOND_IN_MILLIS = 1000L

@HiltViewModel
class ShuffledViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    statisticsRepository: StatisticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository, statisticsRepository, savedStateHandle
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
