package enzzom.hexemoji.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.ViewGameTutorialBinding

class GameTutorialView(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewGameTutorialBinding
    private var currentPagePosition: Int = 0
    private lateinit var dataProvider: GameTutorialDataProvider

    init {
        val inflater = LayoutInflater.from(context)

        binding = ViewGameTutorialBinding.inflate(inflater, this)

        binding.apply {
            gameTutorialButtonPrevious.setOnClickListener {
                currentPagePosition--

                updatePageIndicator()
                updateInstructionImage()
                updateInstructionDescription()

                if (currentPagePosition == 0) {
                    gameTutorialButtonPrevious.visibility = View.GONE
                }

                gameTutorialButtonNext.visibility = View.VISIBLE
            }

            gameTutorialButtonNext.setOnClickListener {
                currentPagePosition++

                updatePageIndicator()
                updateInstructionImage()
                updateInstructionDescription()

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
        updateInstructionImage()
        updateInstructionDescription()
    }

    private fun arrangePageIndicators() {
        binding.gameTutorialPageIndicatorLayout.removeAllViews()

        for (i in 0 until dataProvider.getTotalItems()) {
            val indicator = View(context)

            indicator.setBackgroundResource(R.drawable.page_indicator_shape)

            val indicatorSize = resources.getDimensionPixelSize(R.dimen.game_tutorial_page_indicator_size)

            val layoutParams = LinearLayout.LayoutParams(indicatorSize, indicatorSize)

            layoutParams.marginEnd = if (i < dataProvider.getTotalItems() - 1) {
                resources.getDimensionPixelSize(R.dimen.game_tutorial_page_indicator_margin)
            } else {
                0
            }

            indicator.layoutParams = layoutParams

            binding.gameTutorialPageIndicatorLayout.addView(indicator)
        }
    }

    private fun updateInstructionImage() {
        val imageId = dataProvider.getDrawableId(currentPagePosition)
        binding.gameTutorialDescriptionImage.setImageResource(imageId)
    }

    private fun updateInstructionDescription() {
        val description = dataProvider.getDescription(currentPagePosition)
        binding.gameTutorialDescription.text = description
    }

    private fun updatePageIndicator() {
        for (i in 0 until dataProvider.getTotalItems()) {
            binding.gameTutorialPageIndicatorLayout.getChildAt(i).isSelected = i == currentPagePosition
        }
    }
}