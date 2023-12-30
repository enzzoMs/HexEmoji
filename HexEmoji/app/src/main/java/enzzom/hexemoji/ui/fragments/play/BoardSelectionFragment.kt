package enzzom.hexemoji.ui.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentBoardSelectionBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.ui.fragments.main.MainFragment
import enzzom.hexemoji.ui.fragments.play.adapters.BoardSizeAdapter
import enzzom.hexemoji.ui.fragments.play.model.PlayViewModel
import enzzom.hexemoji.utils.recyclerview.HexagonalSpanSizeLookup

class BoardSelectionFragment : Fragment() {

    private val playViewModel: PlayViewModel by activityViewModels()
    private var binding: FragmentBoardSelectionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardSelectionBinding.inflate(inflater, container, false)

        val mainFragment = parentFragment?.parentFragment as MainFragment
        mainFragment.apply {
            setToolbarTitle(playViewModel.getGameModeTitle(resources))
            showBackArrow(true)
            showNavigationViews(false)
        }

        playViewModel.hasSelectedBoardSize.observe(viewLifecycleOwner) {
            binding?.boardSelectionButtonPlay?.isEnabled = it
        }

        binding?.boardSelectionButtonPlay?.setOnClickListener {
            mainFragment.navigateToGameScreen(
                playViewModel.getSelectedGameMode()!!, playViewModel.getSelectedBoardSize()!!,
                playViewModel.getSelectedEmojiCategories()
            )
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardSizes = BoardSize.values().toList()
        val useHexagonBoard = resources.getBoolean(R.bool.board_selection_use_hexagon_board)
        val hexagonalGridSpan = resources.getInteger(R.integer.board_selection_hexagonal_grid_span)

        binding?.boardSizeList?.apply {
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

        // Manually configuring the behavior of the back button due to an error that
        // caused a synchronization issue between what was shown on the screen and the actual
        // current destination tracked by the NavController.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_board_selection_to_emojis_selection)
                    playViewModel.clearBoardSizeSelection()
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}