package sarueh.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import sarueh.hexemoji.data.entities.Challenge
import sarueh.hexemoji.data.entities.GeneralChallenge
import sarueh.hexemoji.data.entities.TimedChallenge
import sarueh.hexemoji.data.repositories.ChallengesRepository
import sarueh.hexemoji.data.repositories.EmojiRepository
import sarueh.hexemoji.data.repositories.PreferencesRepository
import sarueh.hexemoji.data.repositories.StatisticsRepository
import sarueh.hexemoji.models.BoardSize.BOARD_2_BY_4
import sarueh.hexemoji.models.BoardSize.BOARD_3_BY_4
import sarueh.hexemoji.models.BoardSize.BOARD_4_BY_4
import sarueh.hexemoji.models.BoardSize.BOARD_4_BY_7
import sarueh.hexemoji.models.BoardSize.BOARD_4_BY_8
import sarueh.hexemoji.models.BoardSize.BOARD_5_BY_8
import sarueh.hexemoji.models.BoardSize.BOARD_6_BY_8
import sarueh.hexemoji.models.BoardSize.BOARD_7_BY_9
import sarueh.hexemoji.models.BoardSize.BOARD_8_BY_7
import sarueh.hexemoji.models.BoardSize.BOARD_8_BY_8
import sarueh.hexemoji.models.BoardSize.BOARD_9_BY_8
import sarueh.hexemoji.models.BoardSize.BOARD_9_BY_9
import sarueh.hexemoji.models.GameStatus
import sarueh.hexemoji.ui.fragments.game.BaseGameViewModel
import sarueh.hexemoji.utils.CountdownManager
import javax.inject.Inject

private const val ONE_SECOND_IN_MILLIS = 1000L

@HiltViewModel
class AgainstTheClockViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    statisticsRepository: StatisticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository, statisticsRepository, savedStateHandle
) {

    val countdownManager = CountdownManager(
        startTimeMs = when (boardSize) {
            BOARD_2_BY_4 -> 15;    BOARD_3_BY_4 -> 30;   BOARD_4_BY_4 -> 45
            BOARD_4_BY_7 -> 90;   BOARD_4_BY_8 -> 100;  BOARD_5_BY_8 -> 160
            BOARD_7_BY_9 -> 290;  BOARD_8_BY_7 -> 270;  BOARD_8_BY_8 -> 320
            BOARD_6_BY_8 -> 210;  BOARD_9_BY_8 -> 420;  BOARD_9_BY_9 -> 460
        } * ONE_SECOND_IN_MILLIS,
        onCountdownFinished = {
            if (getGameStatus() == GameStatus.IN_PROGRESS) {
                setGameStatus(GameStatus.DEFEAT)
            }
        }
    )

    override fun shouldUpdateChallengeOnVictory(challenge: Challenge): Boolean {
        return when (challenge) {
            is GeneralChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (challenge.boardSize == null || challenge.boardSize == boardSize) &&
                ((challenge.constrainedToCategory && challenge.category in selectedCategories)
                    || !challenge.constrainedToCategory)
            is TimedChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode && (
                    countdownManager.startTimeMs - (countdownManager.remainingSeconds.value!! * ONE_SECOND_IN_MILLIS)
                ) / ONE_SECOND_IN_MILLIS <= challenge.timeLimitInSeconds
            else -> false
        }
    }
}