package enzzom.hexemoji.ui.fragments.game.adapters

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemGameBoardEmojiCardBinding
import enzzom.hexemoji.utils.recyclerview.HexagonalLayout

private const val ENTRY_ANIMATION_DURATION = 1200L
private const val ENTRY_ANIMATION_BASE_DELAY = 5L

class GameBoardAdapter(
    private val numberOfEmojiCards: Int,
    private val emojiCardSizePx: Int,
    private var executeBoardEntryAnimation: Boolean,
    val gridSpanCount: Int
) : RecyclerView.Adapter<GameBoardAdapter.GameBoardHolder>() {

    private var remainingAnimations: Int = 0
    private val _animationFinished =  MutableLiveData(!executeBoardEntryAnimation)
    val animationFinished: LiveData<Boolean> = _animationFinished

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
                duration = ENTRY_ANIMATION_DURATION
                // Creating a cascade effect by adding a delay based on the item's position
                startDelay = position * ENTRY_ANIMATION_BASE_DELAY
                doOnEnd {
                    remainingAnimations--

                    if (remainingAnimations == 0) {
                        _animationFinished.value = true
                        executeBoardEntryAnimation = false
                    }
                }
            }.start()
        }
    }

    override fun getItemCount(): Int = numberOfEmojiCards

    inner class GameBoardHolder(binding: ItemGameBoardEmojiCardBinding) : RecyclerView.ViewHolder(binding.root)
}