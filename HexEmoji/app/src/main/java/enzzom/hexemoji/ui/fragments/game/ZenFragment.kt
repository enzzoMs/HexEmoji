package enzzom.hexemoji.ui.fragments.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentZenBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider
import enzzom.hexemoji.ui.fragments.game.adapters.GameBoardAdapter
import enzzom.hexemoji.ui.fragments.game.models.GameViewModel
import javax.inject.Inject


@AndroidEntryPoint
class ZenFragment : Fragment() {

    private lateinit var boardSize: BoardSize
    private lateinit var selectedEmojiCategories: List<EmojiCategory>
    @Inject lateinit var gameViewModelFactory: GameViewModel.Factory

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModel.provideFactory(gameViewModelFactory, boardSize, selectedEmojiCategories)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentZenBinding.inflate(inflater, container, false)

        (parentFragment as GameFragment).apply {
            boardSize = getBoardSize()
            selectedEmojiCategories = getSelectedEmojiCategories()
        }

        setGameTutorial()

        binding.zenGameBoard.setGameBoardAdapter(GameBoardAdapter(
            gridSpanCount = boardSize.numOfColumns,
            numberOfEmojiCards = boardSize.getSizeInHexagonalLayout(),
            emojiCardSizePx = resources.getDimensionPixelSize(R.dimen.game_board_card_size)
        ))

        return binding.root
    }

    private fun setGameTutorial() {
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
    }
}