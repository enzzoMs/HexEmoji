package enzzom.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.repositories.ChallengesRepository
import enzzom.hexemoji.data.repositories.EmojiRepository
import enzzom.hexemoji.data.repositories.PreferencesRepository
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject

@HiltViewModel
class ZenViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository,
    BoardSize.valueOf(savedStateHandle.get<String>(BaseGameModeFragment.BOARD_SIZE_ARG_KEY)!!),
    GameMode.valueOf(savedStateHandle.get<String>(BaseGameModeFragment.GAME_MODE_ARG_KEY)!!),
    savedStateHandle.get<Array<String>>(BaseGameModeFragment.SELECTED_CATEGORIES_ARG_KEY)!!.map {
        EmojiCategory.valueOf(it)
    }
) {

    override fun shouldUpdateChallengeOnVictory(challenge: Challenge): Boolean {
        return when (challenge) {
            is GeneralChallenge -> !challenge.completed &&
                challenge.gameMode == gameMode &&
                (challenge.boardSize == null || challenge.boardSize == boardSize) &&
                ((challenge.constrainedToCategory && challenge.category in selectedCategories)
                    || !challenge.constrainedToCategory)
            else -> false
        }
    }
}