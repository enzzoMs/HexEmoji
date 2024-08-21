package ems.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ems.hexemoji.data.entities.Challenge
import ems.hexemoji.data.entities.GeneralChallenge
import ems.hexemoji.data.entities.TimedChallenge
import ems.hexemoji.data.repositories.ChallengesRepository
import ems.hexemoji.data.repositories.EmojiRepository
import ems.hexemoji.data.repositories.PreferencesRepository
import ems.hexemoji.data.repositories.StatisticsRepository
import ems.hexemoji.models.BoardSize.BOARD_2_BY_4
import ems.hexemoji.models.BoardSize.BOARD_3_BY_4
import ems.hexemoji.models.BoardSize.BOARD_4_BY_4
import ems.hexemoji.models.BoardSize.BOARD_4_BY_7
import ems.hexemoji.models.BoardSize.BOARD_4_BY_8
import ems.hexemoji.models.BoardSize.BOARD_5_BY_8
import ems.hexemoji.models.BoardSize.BOARD_6_BY_8
import ems.hexemoji.models.BoardSize.BOARD_7_BY_9
import ems.hexemoji.models.BoardSize.BOARD_8_BY_7
import ems.hexemoji.models.BoardSize.BOARD_8_BY_8
import ems.hexemoji.models.BoardSize.BOARD_9_BY_8
import ems.hexemoji.models.BoardSize.BOARD_9_BY_9
import ems.hexemoji.models.GameStatus
import ems.hexemoji.ui.fragments.game.BaseGameViewModel
import ems.hexemoji.utils.CountdownManager
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