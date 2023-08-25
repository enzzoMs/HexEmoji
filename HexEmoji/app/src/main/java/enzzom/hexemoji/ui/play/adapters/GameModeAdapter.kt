package enzzom.hexemoji.ui.play.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemGameModeCardBinding
import enzzom.hexemoji.models.GameMode

class GameModeAdapter(
    private val gameModes: List<GameMode>
) : RecyclerView.Adapter<GameModeAdapter.GameModeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameModeHolder {
        val inflater = LayoutInflater.from(parent.context)

        return GameModeHolder(
            ItemGameModeCardBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GameModeHolder, position: Int) {
        holder.bind(gameModes[position])
    }

    override fun getItemCount() = gameModes.size

    inner class GameModeHolder(
        private val binding: ItemGameModeCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gameMode: GameMode) {
            binding.apply {
                gameModeTitle.text = gameMode.title
                gameModeTitle.setTextColor(gameMode.titleColor)
                gameModeDescription?.text = gameMode.description
                emojiBackground?.backgroundTintList = ColorStateList.valueOf(gameMode.primaryColor)
                emoji.text = gameMode.emoji
            }
        }
    }
}