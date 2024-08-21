package ems.hexemoji.ui.custom

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import ems.hexemoji.models.EmojiCard
import ems.hexemoji.utils.recyclerview.HexagonalLayout

private const val BOARD_ENTRY_ANIMATION_DURATION = 1200L
private const val BOARD_ENTRY_ANIMATION_BASE_DELAY = 5L

class GameBoardAdapter(
    private val numberOfEmojiCards: Int,
    private val emojiCardSizePx: Int,
    private val emojiCardMarginPx: Int = 0,
    private val onEmojiCardClicked: (cardView: EmojiCardView, position: Int) -> Unit,
    private val getEmojiCardForPosition: (Int) -> EmojiCard?,
    private var executeBoardEntryAnimation: Boolean,
    val gridSpanCount: Int
) : RecyclerView.Adapter<GameBoardAdapter.EmojiCardHolder>() {

    private var cardInteractionEnabled: Boolean = true

    private var remainingAnimations: Int = 0

    private val _entryAnimationFinished =  MutableLiveData(!executeBoardEntryAnimation)
    val entryAnimationFinished: LiveData<Boolean> = _entryAnimationFinished

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiCardHolder {
        return EmojiCardHolder(EmojiCardView(parent.context))
    }

    override fun onBindViewHolder(holder: EmojiCardHolder, position: Int) {
        HexagonalLayout.setHexagonMargins(
            view = holder.itemView,
            viewPositionInGrid = position,
            viewSizePx = emojiCardSizePx,
            marginPx = emojiCardMarginPx,
            spanCount = gridSpanCount
        )

        if (executeBoardEntryAnimation) {
            remainingAnimations++

            holder.itemView.apply {
                scaleX = 0f
                scaleY = 0f
            }

            ObjectAnimator.ofPropertyValuesHolder(
                holder.itemView,
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f)
            ).apply {
                // Creating a cascade effect by adding a delay based on the item's position
                startDelay = position * BOARD_ENTRY_ANIMATION_BASE_DELAY
                duration = BOARD_ENTRY_ANIMATION_DURATION
                doOnEnd {
                    remainingAnimations--

                    if (remainingAnimations == 0) {
                        _entryAnimationFinished.value = true
                        executeBoardEntryAnimation = false
                    }
                }
            }.start()
        }

        holder.bind(position)
    }

    override fun getItemCount(): Int = numberOfEmojiCards

    fun enableCardInteraction(enable: Boolean) {
        cardInteractionEnabled = enable
    }

    inner class EmojiCardHolder(val emojiCardView: EmojiCardView) : RecyclerView.ViewHolder(emojiCardView) {

        private var position: Int = 0
        private var dataInitialized: Boolean = false

        init {
            emojiCardView.setOnClickListener {
                // The card view data may take some time to load since it will probably be fetched
                // from the database. If the user clicks on view and the data is not initialized yet,
                // then we try to bind the data again to make sure that the view is is properly populated
                if (!dataInitialized) {
                    bind(position)
                }

                // If the data is not ready yet, then we block the click event to avoid displaying
                // incorrect information or undesired behavior
                if (cardInteractionEnabled && dataInitialized) {
                    onEmojiCardClicked(emojiCardView, position)
                }
            }
        }

        fun bind(position: Int) {
            this.position = position

            getEmojiCardForPosition(position).let {
                dataInitialized = it != null
                emojiCardView.apply {
                    emoji = it?.emoji ?: ""
                    flipped = it?.flipped ?: false
                    matched = it?.matched ?: false
                }
            }
        }

        fun rebindCardEmoji(position: Int) {
            emojiCardView.emoji = getEmojiCardForPosition(position)!!.emoji
        }
    }
}