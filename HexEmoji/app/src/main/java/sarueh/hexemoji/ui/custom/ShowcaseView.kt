package sarueh.hexemoji.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import sarueh.hexemoji.databinding.ViewShowcaseBinding

class ShowcaseView(context: Context, attrs: AttributeSet? = null) : BasePagedView(context, attrs) {

    override fun initializeViews(inflater: LayoutInflater, parent: ViewGroup): PagingViewsBundle {
        return ViewShowcaseBinding.inflate(inflater, parent).run {
            PagingViewsBundle(
                pageTitle = showcaseTitle,
                pageDescription = showcaseDescription,
                pageImage = showcaseImage,
                pageIndicatorLayout = showcasePageIndicatorLayout,
                buttonNext = showcaseButtonNext,
                buttonBack = showcaseButtonBack
            )
        }
    }
}