package sarueh.hexemoji.ui.fragments.game.gamemodes.model

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import sarueh.hexemoji.data.repositories.ChallengesRepository
import sarueh.hexemoji.data.repositories.EmojiRepository
import sarueh.hexemoji.data.repositories.PreferencesRepository
import sarueh.hexemoji.data.repositories.StatisticsRepository
import sarueh.hexemoji.ui.fragments.game.BaseGameViewModel
import javax.inject.Inject

@HiltViewModel
class ZenViewModel @Inject constructor(
    emojiRepository: EmojiRepository,
    preferencesRepository: PreferencesRepository,
    challengesRepository: ChallengesRepository,
    statisticsRepository: StatisticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseGameViewModel(
    emojiRepository, preferencesRepository, challengesRepository, statisticsRepository, savedStateHandle
)