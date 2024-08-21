package ems.hexemoji.ui.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ems.hexemoji.R
import ems.hexemoji.databinding.FragmentBoardSelectionBinding
import ems.hexemoji.models.BoardSize
import ems.hexemoji.models.EmojiCategory
import ems.hexemoji.models.GameMode
import ems.hexemoji.ui.fragments.play.adapters.BoardSizeAdapter
import ems.hexemoji.ui.fragments.play.model.PlayViewModel
import ems.hexemoji.utils.recyclerview.HexagonalSpanSizeLookup

class BoardSelectionFragment : Fragment() {

    private val playViewModel: PlayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBoardSelectionBinding.inflate(inflater, container, false)

        playViewModel.getSelectedGameMode()?.let {
            binding.boardSelectionToolbar.title = it.getTitle(resources)
        }

        playViewModel.hasSelectedBoardSize.observe(viewLifecycleOwner) {
            binding.boardSelectionButtonPlay.isEnabled = it
        }

        binding.boardSelectionToolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.boardSelectionButtonPlay.setOnClickListener {
            navigateToGameScreen(
                playViewModel.getSelectedGameMode()!!,
                playViewModel.getSelectedBoardSize()!!,
                playViewModel.getSelectedCategories()
            )
        }

        val boardSizes = BoardSize.entries
        val useHexagonBoard = resources.getBoolean(R.bool.board_selection_use_hexagon_board)
        val hexagonalGridSpan = resources.getInteger(R.integer.board_selection_hexagonal_grid_span)

        binding.boardSizeList.apply {
            // Removing the recycler view animations (mainly to prevent blink after 'notifyItemChanged')
            itemAnimator = null
            setHasFixedSize(true)

            adapter = BoardSizeAdapter(
                boardSizes = BoardSize.entries,
                onBoardSizeCardClicked = { newSelectedBoard ->
                    val previousSelectedBoardIndex = boardSizes.indexOf(playViewModel.getSelectedBoardSize())
                    playViewModel.selectBoardSize(newSelectedBoard)
                    this.adapter?.notifyItemChanged(previousSelectedBoardIndex)
                },
                isBoardSizeCardSelected = { playViewModel.isBoardSizeSelected(it) },
                useHexagonalLayout = useHexagonBoard,
                hexagonalGridSpanCount = hexagonalGridSpan,
                hexagonViewSizePx = resources.getDimensionPixelSize(R.dimen.board_size_card_size),
                hexagonMarginPx = resources.getDimensionPixelSize(R.dimen.hexagonal_board_item_margin)
            )

            if (useHexagonBoard) {
                val gridManager = GridLayoutManager(context, hexagonalGridSpan, RecyclerView.VERTICAL, false)
                gridManager.spanSizeLookup = HexagonalSpanSizeLookup(hexagonalGridSpan)

                layoutManager = gridManager
            }
        }


        return binding.root
    }

    private fun navigateToGameScreen(
        selectedGameMode: GameMode, selectedBoardSize: BoardSize,
        selectedEmojiCategories: List<EmojiCategory>
    ) {
        findNavController().navigate(
            BoardSelectionFragmentDirections.actionBoardSelectionToGameScreen(
                selectedGameMode, selectedBoardSize, selectedEmojiCategories.map { it.name }.toTypedArray()
            )
        )
    }
}