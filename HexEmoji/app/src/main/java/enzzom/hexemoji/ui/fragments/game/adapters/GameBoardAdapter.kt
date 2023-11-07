package enzzom.hexemoji.ui.fragments.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemGameBoardEmojiCardBinding
import enzzom.hexemoji.utils.recyclerview.HexagonalLayout

class GameBoardAdapter(
    private val numberOfEmojiCards: Int,
    private val emojiCardSizePx: Int,
    val gridSpanCount: Int
) : RecyclerView.Adapter<GameBoardAdapter.GameBoardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameBoardAdapter.GameBoardHolder {
        val inflater = LayoutInflater.from(parent.context)

        return GameBoardHolder(
            ItemGameBoardEmojiCardBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GameBoardAdapter.GameBoardHolder , position: Int) {
        HexagonalLayout.setHexagonMargins(
            view = holder.itemView,
            viewPositionInGrid = position,
            spanCount = gridSpanCount,
            viewSizePx = emojiCardSizePx
        )
    }

    override fun getItemCount(): Int = numberOfEmojiCards

    inner class GameBoardHolder(binding: ItemGameBoardEmojiCardBinding) : RecyclerView.ViewHolder(binding.root)
}