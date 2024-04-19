package sarueh.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import sarueh.hexemoji.data.entities.Challenge
import sarueh.hexemoji.data.entities.GeneralChallenge
import sarueh.hexemoji.data.entities.LimitedMovesChallenge
import sarueh.hexemoji.data.repositories.ChallengesRepository
import sarueh.hexemoji.data.repositories.EmojiRepository
import sarueh.hexemoji.data.repositories.PreferencesRepository
import sarueh.hexemoji.data.repositories.StatisticsRepository
import sarueh.hexemoji.models.BoardSize
import sarueh.hexemoji.models.GameStatus
import sarueh.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject

@HiltViewModel
class LimitedMovesViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    statisticsRepository: StatisticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository, statisticsRepository, savedStateHandle
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

    override fun shouldExecuteExitAnimation(): Boolean {
        return _remainingMoves.value!! == 0 || emojiCards!!.count { !it.matched } == 0
    }
}