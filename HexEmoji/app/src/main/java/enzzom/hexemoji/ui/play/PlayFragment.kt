package enzzom.hexemoji.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentPlayBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategoryCard
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.ui.play.adapters.BoardSizeAdapter
import enzzom.hexemoji.ui.play.adapters.EmojiCategoryAdapter
import enzzom.hexemoji.ui.play.adapters.GameModeAdapter
import enzzom.hexemoji.ui.play.models.PlayViewModel
import enzzom.hexemoji.utils.recyclerview.HexagonalSpanSizeLookup

/**
 * A fragment used for larger screens that serves as a composition of three distinct pages:
 * Game Modes, Emojis Selection and Board Selection Page.
 *
 * @see GameModesFragment
 * @see EmojisSelectionFragment
 * @see BoardSelectionFragment
 */
class PlayFragment : Fragment() {

    private val playViewModel: PlayViewModel by activityViewModels()
    private lateinit var emojiCategoryCards: List<EmojiCategoryCard>
    private var binding: FragmentPlayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayBinding.inflate(inflater, container, false)

        playViewModel.apply {
            clearGameModeSelection()
            clearEmojiCategoriesSelection()
            clearBoardSizeSelection()
        }

        binding?.apply {
            // Emojis Selection Page ----------------------------------------------

            // The PlayFragment combines three different fragments.
            // Two of them have buttons to go to different pages.
            // In this case, we hide the "Continue" button on the Emojis Selection Page as it is not
            // needed and remain with only the button on the Board Selection Page
            emojisSelectionPage.buttonContinue.visibility = View.GONE

            emojiCategoryCards = playViewModel.getEmojiCategoryCards(resources)

            playViewModel.hasSelectedAllCategories.observe(viewLifecycleOwner) {
                emojisSelectionPage.allEmojisCheckBox.isChecked = it
            }

            // Removing the recycler view animations (mainly to prevent blink after 'notifyItemChanged')
            emojisSelectionPage.emojiCategoriesList.itemAnimator = null

            emojisSelectionPage.allEmojisCheckBox.setOnClickListener {
                if (emojisSelectionPage.allEmojisCheckBox.isChecked) {
                    playViewModel.selectAllEmojiCategories()
                } else {
                    playViewModel.clearEmojiCategoriesSelection()
                }

                emojisSelectionPage.emojiCategoriesList.adapter?.notifyItemRangeChanged(0, emojiCategoryCards.size)
            }

            // Board Selection Page ----------------------------------------------

            playViewModel.allFieldsSelected.observe(viewLifecycleOwner) {
                boardSelectionPage.boardSelectionButtonPlay.isEnabled = it
            }
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up the RecyclerViews and their adapters

        binding?.apply {
            // Game Modes Page ---------------------------------------------------

            val gameModes = GameMode.values().toList()

            gameModesPage.gameModeList.apply {
                // Removing the recycler view animations (mainly to prevent blink after 'notifyItemChanged')
                itemAnimator = null
                setHasFixedSize(true)
                adapter = GameModeAdapter(
                    gameModeCards = playViewModel.getGameModeCards(resources),
                    onGameModeClicked = { gameModeCard ->
                        val previousSelectedGameModeIndex = gameModes.indexOf(playViewModel.getSelectedGameMode())
                        playViewModel.selectGameMode(gameModeCard)
                        this.adapter?.notifyItemChanged(previousSelectedGameModeIndex)
                    },
                    isGameModeSelected = { playViewModel.isGameModeSelected(it) }
                )
            }

            // Emojis Selection Page ----------------------------------------------

            emojisSelectionPage.emojiCategoriesList.apply {
                setHasFixedSize(true)
                adapter = EmojiCategoryAdapter(
                    emojiCategoryCards = emojiCategoryCards,
                    onEmojiCategoryClicked = { playViewModel.toggleEmojiCategorySelection(it) },
                    isCategorySelected = { playViewModel.isEmojiCategorySelected(it) }
                )
            }

            // Board Selection Page ----------------------------------------------

            val boardSizes = BoardSize.values().toList()
            val useHexagonBoard = resources.getBoolean(R.bool.board_selection_use_hexagon_board)
            val hexagonalGridSpan = resources.getInteger(R.integer.board_selection_hexagonal_grid_span)

            boardSelectionPage.boardSizeList.apply {
                // Removing the recycler view animations (mainly to prevent blink after 'notifyItemChanged')
                itemAnimator = null
                setHasFixedSize(true)
                adapter = BoardSizeAdapter(
                    boardSizes = BoardSize.values().toList(),
                    onBoardSizeCardClicked = { newSelectedBoard ->
                        val previousSelectedBoardIndex = boardSizes.indexOf(playViewModel.getSelectedBoardSize())
                        playViewModel.selectBoardSize(newSelectedBoard)
                        this.adapter?.notifyItemChanged(previousSelectedBoardIndex)
                    },
                    isBoardSizeCardSelected = { playViewModel.isBoardSizeSelected(it) },
                    useHexagonalLayout = useHexagonBoard,
                    hexagonalGridSpanCount = hexagonalGridSpan,
                    hexagonViewSizePx = resources.getDimensionPixelSize(R.dimen.board_size_card_size)
                )

                if (useHexagonBoard) {
                    val gridManager = GridLayoutManager(context, hexagonalGridSpan, RecyclerView.VERTICAL, false)
                    gridManager.spanSizeLookup = HexagonalSpanSizeLookup(hexagonalGridSpan)

                    layoutManager = gridManager
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}