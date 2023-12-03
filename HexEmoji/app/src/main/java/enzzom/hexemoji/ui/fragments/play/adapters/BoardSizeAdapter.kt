package enzzom.hexemoji.ui.fragments.play.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemCardBoardSizeBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.utils.recyclerview.HexagonalLayout

class BoardSizeAdapter(
    private val boardSizes: List<BoardSize>,
    private val onBoardSizeCardClicked: (BoardSize) -> Unit,
    private val isBoardSizeCardSelected: (BoardSize) -> Boolean,
    private val useHexagonalLayout: Boolean,
    private val hexagonalGridSpanCount: Int = 0,
    private val hexagonViewSizePx: Int = 0
): RecyclerView.Adapter<BoardSizeAdapter.BoardSizeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardSizeHolder {
        val inflater = LayoutInflater.from(parent.context)

        return BoardSizeHolder(
            ItemCardBoardSizeBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BoardSizeHolder, position: Int) {
        if (useHexagonalLayout) {
            HexagonalLayout.setHexagonMargins(
                view = holder.itemView,
                viewPositionInGrid = position,
                spanCount = hexagonalGridSpanCount,
                viewSizePx = hexagonViewSizePx
            )
        }

        holder.bind(boardSizes[position])
    }

    override fun getItemCount(): Int = boardSizes.size

    inner class BoardSizeHolder(
         private val binding: ItemCardBoardSizeBinding
    ): RecyclerView.ViewHolder(binding.root) {

        private lateinit var boardSize: BoardSize

        init {
            binding.boardSizeBackground.setOnClickListener {
                onBoardSizeCardClicked(boardSize)
                binding.boardSizeCard.isSelected = isBoardSizeCardSelected(boardSize)
            }
        }

        fun bind(boardSize: BoardSize) {
            this.boardSize = boardSize
            binding.boardSizeCard.isSelected = isBoardSizeCardSelected(boardSize)
            binding.boardSize.text = boardSize.getLabel()
        }
    }
}