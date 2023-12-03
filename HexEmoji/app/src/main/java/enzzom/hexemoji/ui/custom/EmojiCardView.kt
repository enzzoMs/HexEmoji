package enzzom.hexemoji.ui.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.ViewEmojiCardBinding

private const val CARD_FLIP_ANIMATION_DURATION = 750L

class EmojiCardView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private val binding: ViewEmojiCardBinding
    var flipped: Boolean = false
        set(value) {
            field = value

            binding.apply {
                emoji.text = if (value) text else ""
                emojiCardBackground.setImageResource(if (value)
                    R.drawable.emoji_card_front
                else
                    R.drawable.emoji_card_back
                )
            }
        }

    var matched: Boolean = false
        set(value) {
            field = value

            binding.emojiCardBackground.setImageState(
                intArrayOf(if (value) R.attr.state_matched else -R.attr.state_matched), false
            )
        }

    var text: String = ""

    init {
        val inflater = LayoutInflater.from(context)

        binding = ViewEmojiCardBinding.inflate(inflater, this)
    }

    /**
     * Toggles the flipped state of the card and displays a flip animation.
     */
    fun flipCard(animStartDelay: Long = 0, onAnimationEnd: () -> Unit = {}) {
        ObjectAnimator.ofFloat(binding.root, "rotationY", 90f).apply {
            duration = CARD_FLIP_ANIMATION_DURATION / 2
            startDelay = animStartDelay
            doOnEnd {
                flipped = !flipped

                ObjectAnimator.ofFloat(binding.root, "rotationY", 0f).apply {
                    duration = CARD_FLIP_ANIMATION_DURATION / 2
                }.apply {
                    doOnEnd {
                        onAnimationEnd()
                    }
                }.start()
            }
        }.start()
    }

    /**
     * Changes the 'matched' status of the card to 'true' and initiates an animated sequence to visually
     * indicate that the card has been successfully matched.
     */
    fun matchCard() {
        binding.emojiCardBackground.apply {
            setImageResource(R.drawable.emoji_card_match_anim)

            (drawable as AnimatedVectorDrawable).apply {
                registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        matched =  true
                        setImageResource(if (flipped) R.drawable.emoji_card_front else R.drawable.emoji_card_back)
                    }
                })
                start()
            }
        }
    }
}