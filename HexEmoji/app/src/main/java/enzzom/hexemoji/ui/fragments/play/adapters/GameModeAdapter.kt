package enzzom.hexemoji.ui.fragments.play.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemCardGameModeBinding
import enzzom.hexemoji.databinding.ItemHeaderPageDescriptionBinding
import enzzom.hexemoji.models.GameModeDetails

private const val PAGE_DESCRIPTION_VIEW_TYPE = 0
private const val GAME_MODE_VIEW_TYPE = 1
private const val HEADER_VIEW_COUNT = 1

class GameModeAdapter(
    private val gameModeDetails: List<GameModeDetails>,
    private val onGameModeClicked: (GameModeDetails) -> Unit,
    private val useHeaderViews: Boolean = false,
    private val pageDescription: String = ""
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val PAGE_DESCRIPTION_VIEW_POSITION = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (useHeaderViews && viewType == PAGE_DESCRIPTION_VIEW_TYPE) {
            PageDescriptionHolder(
                ItemHeaderPageDescriptionBinding.inflate(inflater, parent, false)
            )
        } else {
            GameModeHolder(
                ItemCardGameModeBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PageDescriptionHolder) {
            holder.bind(pageDescription)
        } else if (holder is GameModeHolder) {
            holder.bind(gameModeDetails[if (useHeaderViews) position - HEADER_VIEW_COUNT else position])
        }
    }

    override fun getItemCount(): Int {
        return gameModeDetails.size + (if (useHeaderViews) HEADER_VIEW_COUNT else 0)
    }

    override fun getItemViewType(position: Int): Int {
        return if (useHeaderViews && position == PAGE_DESCRIPTION_VIEW_POSITION) {
            PAGE_DESCRIPTION_VIEW_TYPE
        } else {
            GAME_MODE_VIEW_TYPE
        }
    }

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
                emojiBackground.setBackgroundColor(modeDetails.backgroundColor)
                gameModeEmoji.text = modeDetails.emoji
            }
        }
    }
}