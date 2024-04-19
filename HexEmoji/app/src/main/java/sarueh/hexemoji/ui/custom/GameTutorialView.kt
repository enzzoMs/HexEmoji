package sarueh.hexemoji.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import sarueh.hexemoji.databinding.ViewGameTutorialBinding

class GameTutorialView(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BasePagedView(context, attrs, defStyleAttr) {

    override fun initializeViews(inflater: LayoutInflater, parent: ViewGroup): PagingViewsBundle {
        return ViewGameTutorialBinding.inflate(inflater, parent).run {
            PagingViewsBundle(
                pageTitle = null,
                pageDescription = gameTutorialDescription,
                pageImage = gameTutorialImage,
                pageIndicatorLayout = gameTutorialPageIndicatorLayout,
                buttonNext = gameTutorialButtonNext,
                buttonBack = gameTutorialButtonPrevious
            )
        }
    }
}