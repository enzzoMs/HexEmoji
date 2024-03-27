package enzzom.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.entities.LimitedMovesChallenge
import enzzom.hexemoji.data.entities.TimedChallenge
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
class LimitedMovesViewModel @Inject constructor(
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

    val initialMoves: Int = when (boardSize) {
        BoardSize.BOARD_2_BY_4 -> 10;    BoardSize.BOARD_3_BY_4 -> 16;   BoardSize.BOARD_4_BY_4 -> 26
        BoardSize.BOARD_4_BY_7 -> 64;   BoardSize.BOARD_4_BY_8 -> 75;  BoardSize.BOARD_5_BY_8 -> 92
        BoardSize.BOARD_7_BY_9 -> 180;  BoardSize.BOARD_8_BY_7 -> 150;  BoardSize.BOARD_8_BY_8 -> 180
        BoardSize.BOARD_6_BY_8 -> 110;  BoardSize.BOARD_9_BY_8 -> 235;  BoardSize.BOARD_9_BY_9 -> 250
    }

    private val _remainingMoves = MutableLiveData(initialMoves)
    val remainingMoves: LiveData<Int> = _remainingMoves

    override fun flipCard(cardPosition: Int): FlipResult {
        val flipResult = super.flipCard(cardPosition)

        if (_remainingMoves.value == 1 && getGameStatus() == GameStatus.IN_PROGRESS) {
            setGameStatus(GameStatus.DEFEAT)
        }

        _remainingMoves.value = _remainingMoves.value!! - 1

        return flipResult
    }

    override fun shouldUpdateChallengeOnVictory(challenge: Challenge): Boolean {
        return when (challenge) {
            is GeneralChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (challenge.boardSize == null || challenge.boardSize == boardSize) &&
                ((challenge.constrainedToCategory && challenge.category in selectedCategories)
                        || !challenge.constrainedToCategory)
            is LimitedMovesChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (initialMoves - _remainingMoves.value!!) <= challenge.moveLimit
            else -> false
        }
    }
}