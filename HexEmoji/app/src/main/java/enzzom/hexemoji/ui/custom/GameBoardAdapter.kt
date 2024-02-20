package enzzom.hexemoji.ui.custom

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.models.EmojiCard
import enzzom.hexemoji.utils.recyclerview.HexagonalLayout

private const val BOARD_ANIMATION_DURATION = 1200L
private const val BOARD_ANIMATION_BASE_DELAY = 5L

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

    private var executeBoardExitAnimation: Boolean = false

    private var remainingAnimations: Int = 0

    private val _entryAnimationFinished =  MutableLiveData(!executeBoardEntryAnimation)
    val entryAnimationFinished: LiveData<Boolean> = _entryAnimationFinished

    private val _exitAnimationFinished =  MutableLiveData(true)
    val exitAnimationFinished: LiveData<Boolean> = _exitAnimationFinished

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

        if (executeBoardEntryAnimation || executeBoardExitAnimation) {
            remainingAnimations++

            val initialScale = if (executeBoardEntryAnimation) 0f else 1f
            val endScale = if (executeBoardEntryAnimation) 1f else 0f

            holder.itemView.apply {
                scaleX = initialScale
                scaleY = initialScale
            }

            ObjectAnimator.ofPropertyValuesHolder(
                holder.itemView,
                PropertyValuesHolder.ofFloat("scaleX", endScale),
                PropertyValuesHolder.ofFloat("scaleY", endScale)
            ).apply {
                // Creating a cascade effect by adding a delay based on the item's position
                startDelay = position * BOARD_ANIMATION_BASE_DELAY
                duration = BOARD_ANIMATION_DURATION
                doOnEnd {
                    remainingAnimations--

                    if (remainingAnimations == 0) {
                        if (executeBoardEntryAnimation) {
                            _entryAnimationFinished.value = true
                            executeBoardEntryAnimation = false

                        } else if (executeBoardExitAnimation) {
                            _exitAnimationFinished.value = true
                            executeBoardExitAnimation = false
                        }
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

    fun executeBoardExitAnimation() {
        cardInteractionEnabled = false
        executeBoardExitAnimation = true
        _exitAnimationFinished.value = false

        notifyItemRangeChanged(0, itemCount)
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
    }
}