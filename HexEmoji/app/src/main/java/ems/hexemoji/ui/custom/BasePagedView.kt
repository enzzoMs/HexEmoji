package ems.hexemoji.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ems.hexemoji.R

/**
 * A custom view that presents a sequence of pages. It is typically used for tutorials,
 * onboarding, or feature showcases. Each page includes a optional title, description, image and navigation
 * buttons that allow users to move through the showcase sequence.
 * This view requires a [PagedViewDataProvider] to work properly, which should be set using the [setDataProvider] method.
 */
abstract class BasePagedView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var currentPagePosition: Int = 0
    private var dataProvider: PagedViewDataProvider? = null
    private val pagingViewsBundle: PagingViewsBundle by lazy {
        initializeViews(LayoutInflater.from(context), this).also {
            setupPageListeners(it.buttonBack, it.buttonNext)
        }
    }

    fun setDataProvider(dataProvider: PagedViewDataProvider) {
        this.dataProvider = dataProvider

        currentPagePosition = 0

        if (dataProvider.getTotalItems() > 1) {
            pagingViewsBundle.buttonNext.visibility = View.VISIBLE
            arrangePageIndicators()
        }

        updateImage()
        updateTitle()
        updateDescription()
        updatePageIndicator()
    }

    private fun updateTitle() {
        pagingViewsBundle.pageTitle?.text = dataProvider?.getTitle(currentPagePosition)
    }

    private fun updateDescription() {
        pagingViewsBundle.pageDescription.text = dataProvider?.getDescription(currentPagePosition)
    }

    private fun updateImage() {
        dataProvider?.getDrawableId(currentPagePosition)?.let {
            pagingViewsBundle.pageImage.setImageResource(it)
        }
    }

    private fun updatePageIndicator() {
        for (i in 0 until (dataProvider?.getTotalItems() ?: 0)) {
            pagingViewsBundle.pageIndicatorLayout.getChildAt(i).isSelected = i == currentPagePosition
        }
    }

    private fun arrangePageIndicators() {
        if (dataProvider == null) return

        pagingViewsBundle.pageIndicatorLayout.removeAllViews()

        for (indicatorPosition in 0 until dataProvider!!.getTotalItems()) {
            val indicatorSize = resources.getDimensionPixelSize(R.dimen.tutorial_page_indicator_size)
            val indicatorLayoutParams = LinearLayout.LayoutParams(indicatorSize, indicatorSize)

            indicatorLayoutParams.marginEnd = if (indicatorPosition < dataProvider!!.getTotalItems() - 1) {
                resources.getDimensionPixelSize(R.dimen.page_indicator_margin)
            } else {
                0
            }

            val indicator = View(context).apply {
                layoutParams = indicatorLayoutParams
                setBackgroundResource(R.drawable.page_indicator_shape)
            }

            pagingViewsBundle.pageIndicatorLayout.addView(indicator)
        }
    }

    private fun setupPageListeners(buttonBack: ImageButton, buttonNext: ImageButton) {
        buttonBack.setOnClickListener {
            currentPagePosition--

            updateImage()
            updateTitle()
            updateDescription()
            updatePageIndicator()

            if (currentPagePosition == 0) {
                buttonBack.visibility = View.INVISIBLE
            }

            buttonNext.visibility = View.VISIBLE
        }

        buttonNext.setOnClickListener {
            currentPagePosition++

            updateImage()
            updateTitle()
            updateDescription()
            updatePageIndicator()

            if (dataProvider == null || currentPagePosition >= dataProvider!!.getTotalItems() - 1) {
                buttonNext.visibility = View.INVISIBLE
            }

            buttonBack.visibility = View.VISIBLE
        }
    }

    protected abstract fun initializeViews(inflater: LayoutInflater, parent: ViewGroup): PagingViewsBundle

    data class PagingViewsBundle(
        val pageTitle: TextView?,
        val pageDescription: TextView,
        val pageImage: ImageView,
        val pageIndicatorLayout: ViewGroup,
        val buttonNext: ImageButton,
        val buttonBack: ImageButton
    )
}