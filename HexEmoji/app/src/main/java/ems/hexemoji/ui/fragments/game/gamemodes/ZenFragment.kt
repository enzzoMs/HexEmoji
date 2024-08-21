package ems.hexemoji.ui.fragments.game.gamemodes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ems.hexemoji.R
import ems.hexemoji.databinding.FragmentZenBinding
import ems.hexemoji.ui.fragments.game.BaseGameModeFragment
import ems.hexemoji.ui.fragments.game.gamemodes.model.ZenViewModel

@AndroidEntryPoint
class ZenFragment : BaseGameModeFragment() {

    override val gameViewModel: ZenViewModel by viewModels()

    override fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        return FragmentZenBinding.inflate(inflater, container, false).let {
            GameViews(
                layoutRoot = it.root,
                gameBoardView = it.zenGameBoard,
                countDownView = it.zenCountdown
            )
        }
    }

    override fun getGameModeThemeId(): Int = R.style.ThemeOverlay_HexEmoji_GameMode_Zen

    override fun getTutorialDescriptionsArrayId(): Int = R.array.game_tutorial_descriptions_zen

    override fun getTutorialImagesArrayId(): Int = R.array.game_tutorial_images_zen
}