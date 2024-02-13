package enzzom.hexemoji.ui.fragments.game

import android.view.LayoutInflater
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentZenBinding
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider

@AndroidEntryPoint
class ZenFragment : BaseGameModeFragment() {

    override fun getGameViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        return FragmentZenBinding.inflate(inflater, container, false).let {
            GameViews(
                layoutRoot = it.root,
                gameBoardView = it.zenGameBoard,
                countDownView = it.zenCountdown
            )
        }
    }

    override fun getTutorialDataProvider(): GameTutorialDataProvider {
        val tutorialDescriptions = resources.getStringArray(R.array.game_tutorial_descriptions_zen)

        val imagesTypedArray = resources.obtainTypedArray(R.array.game_tutorial_images_zen)

        val tutorialImagesId = List(imagesTypedArray.length()) { index ->
            imagesTypedArray.getResourceId(index, R.drawable.game_tutorial_board_example)
        }

        imagesTypedArray.recycle()

        return object : GameTutorialDataProvider {
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