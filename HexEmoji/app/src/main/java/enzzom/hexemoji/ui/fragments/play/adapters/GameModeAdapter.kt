package enzzom.hexemoji.ui.fragments.play.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemCardGameModeBinding
import enzzom.hexemoji.models.GameModeDetails

class GameModeAdapter(
    private val gameModeDetails: List<GameModeDetails>,
    private val onGameModeClicked: (GameModeDetails) -> Unit,
) : RecyclerView.Adapter<GameModeAdapter.GameModeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameModeHolder {
        val inflater = LayoutInflater.from(parent.context)

        return GameModeHolder(
            ItemCardGameModeBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GameModeHolder, position: Int) {
        holder.bind(gameModeDetails[position])
    }

    override fun getItemCount(): Int = gameModeDetails.size

    inner class GameModeHolder(
        private val binding: ItemCardGameModeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var gameModeDetails: GameModeDetails

        init {
            binding.gameModeCard.setOnClickListener {
                onGameModeClicked(gameModeDetails)
            }
        }

        fun bind(modeDetails: GameModeDetails) {
            gameModeDetails = modeDetails

            binding.apply {
                gameModeTitle.text = modeDetails.title
                gameModeTitle.setTextColor(modeDetails.primaryColor)
                gameModeDescription?.text = modeDetails.description
                emojiBackground.backgroundTintList = ColorStateList.valueOf(modeDetails.backgroundColor)
                gameModeEmoji.text = modeDetails.emoji
            }
        }
    }
}