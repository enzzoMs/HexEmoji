package enzzom.hexemoji.ui.fragments.game.gamemodes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentZenBinding
import enzzom.hexemoji.ui.custom.PagedViewDataProvider
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.gamemodes.model.ZenViewModel

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

    override fun getTutorialDataProvider(): PagedViewDataProvider {
        val tutorialDescriptions = resources.getStringArray(R.array.game_tutorial_descriptions_zen)

        val imagesTypedArray = resources.obtainTypedArray(R.array.game_tutorial_images_zen)

        val tutorialImagesId = List(imagesTypedArray.length()) { index ->
            imagesTypedArray.getResourceId(index, R.drawable.game_tutorial_board_example)
        }

        imagesTypedArray.recycle()

        return object : PagedViewDataProvider {
            override fun getTitle(position: Int): String? = null

            override fun getDescription(position: Int): String {
                return tutorialDescriptions[position]
            }

            override fun getDrawableId(position: Int): Int {
                return tutorialImagesId[position]
            }

            override fun getTotalItems(): Int {
                return tutorialDescriptions.size
            }
        }
    }
}