package enzzom.hexemoji.ui.custom

import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.ViewBoardTutorialBinding

private const val NUM_OF_TUTORIAL_PAGES = 2
private const val SCALING_TUTORIAL_PAGE_POSITION = 0
private const val PANNING_TUTORIAL_PAGE_POSITION = 1

class BoardTutorialView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    val binding: ViewBoardTutorialBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = ViewBoardTutorialBinding.inflate(inflater, this)

        binding.apply {
            boardTutorialButtonPrevious.setOnClickListener {
                stopTutorialAnimation()

                updatePageIndicator(SCALING_TUTORIAL_PAGE_POSITION)

                boardTutorialDescription.text = resources.getString(R.string.board_tutorial_scaling)
                boardTutorialImage.setImageResource(R.drawable.board_tutorial_scaling)
                startTutorialAnimation()

                boardTutorialButtonPrevious.visibility = View.GONE
                boardTutorialButtonNext.visibility = View.VISIBLE
            }

            boardTutorialButtonNext.setOnClickListener {
                stopTutorialAnimation()

                updatePageIndicator(PANNING_TUTORIAL_PAGE_POSITION)

                boardTutorialDescription.text = resources.getString(R.string.board_tutorial_panning)
                boardTutorialImage.setImageResource(R.drawable.board_tutorial_panning)
                startTutorialAnimation()

                boardTutorialButtonNext.visibility = View.GONE
                boardTutorialButtonPrevious.visibility = View.VISIBLE
            }

            boardTutorialDescription.text = resources.getString(R.string.board_tutorial_scaling)
            boardTutorialImage.setImageResource(R.drawable.board_tutorial_scaling)
        }

        arrangePageIndicators()
        updatePageIndicator(SCALING_TUTORIAL_PAGE_POSITION)
        startTutorialAnimation()
    }

    private fun startTutorialAnimation() {
        val tutorialImage = binding.boardTutorialImage.drawable

        if (tutorialImage is AnimatedVectorDrawable) {
            tutorialImage.start()
            tutorialImage.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    tutorialImage.start()
                }
            })
        }
    }

    private fun stopTutorialAnimation() {
        val tutorialImage = binding.boardTutorialImage.drawable

        if (tutorialImage is AnimatedVectorDrawable) {
            tutorialImage.stop()
            tutorialImage.clearAnimationCallbacks()
        }
    }

    private fun arrangePageIndicators() {
        binding.boardTutorialPageIndicatorLayout.removeAllViews()

        for (i in 0 until NUM_OF_TUTORIAL_PAGES) {
            val indicator = View(context)

            indicator.setBackgroundResource(R.drawable.page_indicator_shape)

            val indicatorSize = resources.getDimensionPixelSize(R.dimen.tutorial_page_indicator_size)

            val layoutParams = LinearLayout.LayoutParams(indicatorSize, indicatorSize)

            layoutParams.marginEnd = if (i < NUM_OF_TUTORIAL_PAGES - 1) {
                resources.getDimensionPixelSize(R.dimen.tutorial_page_indicator_margin)
            } else {
                0
            }

            indicator.layoutParams = layoutParams

            binding.boardTutorialPageIndicatorLayout.addView(indicator)
        }
    }

    private fun updatePageIndicator(currentPagePosition: Int) {
        for (i in 0 until NUM_OF_TUTORIAL_PAGES) {
            binding.boardTutorialPageIndicatorLayout.getChildAt(i).isSelected = i == currentPagePosition
        }
    }
}