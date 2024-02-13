package enzzom.hexemoji.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.ViewGameTutorialBinding

/**
 * A custom view to display game tutorials. This view requires a [GameTutorialDataProvider] to
 * work properly, which should be set using the [setDataProvider] method.
 */
class GameTutorialView(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewGameTutorialBinding
    private var currentPagePosition: Int = 0
    private lateinit var dataProvider: GameTutorialDataProvider

    init {
        binding = ViewGameTutorialBinding.inflate(LayoutInflater.from(context), this).apply {
            gameTutorialButtonPrevious.setOnClickListener {
                currentPagePosition--

                updatePageIndicator()
                updateTutorialImage()
                updateTutorialDescription()

                if (currentPagePosition == 0) {
                    gameTutorialButtonPrevious.visibility = View.GONE
                }

                gameTutorialButtonNext.visibility = View.VISIBLE
            }

            gameTutorialButtonNext.setOnClickListener {
                currentPagePosition++

                updatePageIndicator()
                updateTutorialImage()
                updateTutorialDescription()

                if (currentPagePosition >= dataProvider.getTotalItems() - 1) {
                    gameTutorialButtonNext.visibility = View.GONE
                }

                gameTutorialButtonPrevious.visibility = View.VISIBLE
            }
        }
    }

    fun setDataProvider(dataProvider: GameTutorialDataProvider) {
        this.dataProvider = dataProvider

        currentPagePosition = 0

        if (dataProvider.getTotalItems() > 1) {
            binding.gameTutorialButtonNext.visibility = View.VISIBLE
            arrangePageIndicators()
        }

        updatePageIndicator()
        updateTutorialImage()
        updateTutorialDescription()
    }

    private fun arrangePageIndicators() {
        binding.gameTutorialPageIndicatorLayout.removeAllViews()

        for (indicatorPosition in 0 until dataProvider.getTotalItems()) {
            val indicatorSize = resources.getDimensionPixelSize(R.dimen.tutorial_page_indicator_size)
            val indicatorLayoutParams = LinearLayout.LayoutParams(indicatorSize, indicatorSize)

            indicatorLayoutParams.marginEnd = if (indicatorPosition < dataProvider.getTotalItems() - 1) {
                resources.getDimensionPixelSize(R.dimen.tutorial_page_indicator_margin)
            } else {
                0
            }
            
            val indicator = View(context).apply {
                layoutParams = indicatorLayoutParams
                setBackgroundResource(R.drawable.page_indicator_shape)
            }

            binding.gameTutorialPageIndicatorLayout.addView(indicator)
        }
    }

    private fun updateTutorialImage() {
        binding.gameTutorialImage.setImageResource(
            dataProvider.getDrawableId(currentPagePosition)
        )
    }

    private fun updateTutorialDescription() {
        binding.gameTutorialDescription.text = dataProvider.getDescription(currentPagePosition)
    }

    private fun updatePageIndicator() {
        for (i in 0 until dataProvider.getTotalItems()) {
            binding.gameTutorialPageIndicatorLayout.getChildAt(i).isSelected = i == currentPagePosition
        }
    }
}