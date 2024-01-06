package enzzom.hexemoji.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.databinding.FragmentMainBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark_color)

        val navController = NavHostFragment.findNavController(
            childFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        )

        // Manually wiring up the toolbar and screen navigation without using the
        // 'setupWithNavController()' method to prevent the navigation component from applying an
        // animation to the toolbar title when navigating back

        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            when (navDestination.id) {
                R.id.play_fragment, R.id.statistics_fragment, R.id.emojis_fragment -> {
                    showNavigationViews(true)
                    setToolbarTitle(navDestination.label as String)
                    showBackArrow(false)
                }
            }
        }

        binding?.apply {
            mainToolbar.setNavigationOnClickListener {
                navController.popBackStack()
            }

            mainToolbar.navigationContentDescription = resources.getString(R.string.icon_content_description_back)

            bottomNav?.setupWithNavController(navController)
            navRail?.setupWithNavController(navController)
            navView?.setupWithNavController(navController)
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

    fun showNavigationViews(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE

        binding?.apply {
            mainToolbarHoneycomb.visibility = visibility
            bottomNav?.visibility = visibility
            navRail?.visibility = visibility
            navView?.visibility = visibility
        }
    }

    fun setToolbarTitle(title: String) {
        binding?.mainToolbar?.title = title
    }

    fun showBackArrow(show: Boolean) {
        if (show) {
            binding?.mainToolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        } else {
            binding?.mainToolbar?.navigationIcon = null
        }
    }

    fun navigateToGameScreen(
        selectedGameMode: GameMode, selectedBoardSize: BoardSize,
        selectedEmojiCategories: List<EmojiCategory>
    ) {
        findNavController().navigate(
            MainFragmentDirections.actionMainScreenToGameScreen(
                selectedGameMode, selectedBoardSize, selectedEmojiCategories.map { it.name }.toTypedArray()
            )
        )
    }

    fun navigateToCollectionScreen(category: EmojiCategory, categoryEmojis: List<Emoji>) {
        findNavController().navigate(
            MainFragmentDirections.actionMainScreenToEmojiCollection(
                category, categoryEmojis.toTypedArray()
            )
        )
    }
}