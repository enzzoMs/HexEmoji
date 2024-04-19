package sarueh.hexemoji.ui.fragments.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sarueh.hexemoji.R
import sarueh.hexemoji.databinding.FragmentInstructionsBinding
import sarueh.hexemoji.ui.custom.PagedViewDataProvider

class InstructionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = ContextCompat.getColor(
            requireContext(), R.color.instructions_screen_status_bar_color
        )

        return FragmentInstructionsBinding.inflate(inflater, container, false).apply {
            instructionsToolbar.setNavigationOnClickListener { navigateToEntryScreen() }

            val appShowcaseTitles = listOf(
                resources.getString(R.string.app_showcase_title_play),
                resources.getString(R.string.app_showcase_title_statistics),
                resources.getString(R.string.app_showcase_title_emojis)
            )

            val appShowcaseDescriptions = listOf(
                resources.getString(R.string.app_showcase_descriptions_play),
                resources.getString(R.string.app_showcase_descriptions_statistics),
                resources.getString(R.string.app_showcase_descriptions_emojis)
            )

            val imagesTypedArray = resources.obtainTypedArray(R.array.app_showcase_images)

            val appShowcaseImages = List(imagesTypedArray.length()) { index ->
                imagesTypedArray.getResourceId(index, R.drawable.app_showcase_play)
            }

            imagesTypedArray.recycle()

            instructionsShowcase.setDataProvider(object : PagedViewDataProvider {

                override fun getTitle(position: Int): String {
                    return appShowcaseTitles[position]
                }

                override fun getDescription(position: Int): String {
                    return appShowcaseDescriptions[position]
                }

                override fun getDrawableId(position: Int): Int {
                    return appShowcaseImages[position]
                }

                override fun getTotalItems(): Int = appShowcaseTitles.size
            })

        }.root
    }

    private fun navigateToEntryScreen() {
        findNavController().popBackStack()
    }
}