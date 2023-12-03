package enzzom.hexemoji.ui.fragments.play.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemCardGameModeBinding
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameModeCard

class GameModeAdapter(
    private val gameModeCards: List<GameModeCard>,
    private val onGameModeClicked: (GameModeCard) -> Unit,
) : RecyclerView.Adapter<GameModeAdapter.GameModeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameModeHolder {
        val inflater = LayoutInflater.from(parent.context)

        return GameModeHolder(
            ItemCardGameModeBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GameModeHolder, position: Int) {
        holder.bind(gameModeCards[position])
    }

    override fun getItemCount(): Int = gameModeCards.size

    inner class GameModeHolder(
        private val binding: ItemCardGameModeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var gameModeCard: GameModeCard

        init {
            binding.gameModeCard.setOnClickListener {
                onGameModeClicked(gameModeCard)
            }
        }

        fun bind(card: GameModeCard) {
            gameModeCard = card

            binding.apply {
                gameModeTitle.text = card.title
                gameModeTitle.setTextColor(card.titleColor)
                gameModeDescription?.text = card.description
                emojiBackground.backgroundTintList = ColorStateList.valueOf(card.emojiBackgroundColor)
                gameModeEmoji.text = card.emoji
            }
        }
    }
}