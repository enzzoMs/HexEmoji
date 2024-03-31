package enzzom.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.data.repositories.StatisticsRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject

@HiltViewModel
class SequenceViewModel @Inject constructor(
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

    val sequencePairsGoal = when (boardSize) {
        BoardSize.BOARD_2_BY_4, BoardSize.BOARD_3_BY_4 -> 3
        BoardSize.BOARD_4_BY_4, BoardSize.BOARD_4_BY_7, BoardSize.BOARD_4_BY_8 -> 4
        BoardSize.BOARD_5_BY_8, BoardSize.BOARD_7_BY_9 -> 6
        BoardSize.BOARD_8_BY_7, BoardSize.BOARD_8_BY_8, BoardSize.BOARD_6_BY_8 -> 7
        BoardSize.BOARD_9_BY_8, BoardSize.BOARD_9_BY_9 -> 8
    }

    private val _pairsFound = MutableLiveData(0)
    val pairsFound: LiveData<Int> = _pairsFound

    private val matchedCardPositions = mutableListOf<Int>()

    override fun flipCard(cardPosition: Int): FlipResult {
        val flipResult = super.flipCard(cardPosition)


        if (flipResult is FlipResult.MatchSuccessful) {
            _pairsFound.value = _pairsFound.value!! + 1

            matchedCardPositions.add(flipResult.firstCardPosition)
            matchedCardPositions.add(flipResult.secondCardPosition)

        } else if (flipResult is FlipResult.MatchFailed) {
            _pairsFound.value = 0

            emojiCards?.forEachIndexed { index, emojiCard ->
                if (index in matchedCardPositions) {
                    emojiCard.matched = false
                    emojiCard.flipped = false
                }
            }
            matchedCardPositions.clear()
        }

        if (_pairsFound.value == sequencePairsGoal) {
            setGameStatus(GameStatus.VICTORY)
        }

        return flipResult
    }

    fun getMatchedCardPositions(): List<Int> = matchedCardPositions

    override fun shouldExecuteExitAnimation(): Boolean = (sequencePairsGoal - _pairsFound.value!!) == 0
}