package enzzom.hexemoji.ui.fragments.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import enzzom.hexemoji.R
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider

class ZenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val gameTutorialDescriptions = resources.getStringArray(R.array.game_tutorial_descriptions_zen)

        val tutorialImagesTypedArray = resources.obtainTypedArray(R.array.game_tutorial_images_zen)
        val gameTutorialImagesId = Array(tutorialImagesTypedArray.length()) { 0 }

        for (i in 0 until tutorialImagesTypedArray.length()) {
            gameTutorialImagesId[i] = tutorialImagesTypedArray.getResourceId(i, R.drawable.game_tutorial_board_example)
        }

        tutorialImagesTypedArray.recycle()

        (parentFragment as GameFragment).setGameTutorialDataProvider(
            object : GameTutorialDataProvider {
                override fun getDescription(position: Int): String {
                    return gameTutorialDescriptions[position]
                }

                override fun getDrawableId(position: Int): Int {
                    return gameTutorialImagesId[position]
                }

                override fun getTotalItems(): Int {
                    return gameTutorialDescriptions.size
                }
            }
        )

        return inflater.inflate(R.layout.fragment_zen, container, false)
    }
}