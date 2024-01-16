package enzzom.hexemoji.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.databinding.FragmentMainBinding
import enzzom.hexemoji.models.EmojiCategory

class MainFragment : Fragment() {

    private val _toolbarTitle = MutableLiveData("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark_color)

        val navController = (
            childFragmentManager.findFragmentById(R.id.main_nav_host_fragment
        ) as NavHostFragment).findNavController()


        // Manually wiring up the toolbar and screen navigation to prevent the navigation
        // component from applying a slide animation to the toolbar title when navigating

        _toolbarTitle.observe(viewLifecycleOwner) {
            binding.mainToolbar.title = it
        }

        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            _toolbarTitle.value = navDestination.label as String
        }

        binding.apply {
            bottomNav?.setupWithNavController(navController)
            navRail?.setupWithNavController(navController)
            navView?.setupWithNavController(navController)
        }

        return binding.root
    }

    fun navigateToCategorySelection() {
        findNavController().navigate(R.id.action_main_screen_to_category_selection)
    }

    fun navigateToCollectionScreen(category: EmojiCategory, categoryEmojis: List<Emoji>) {
        findNavController().navigate(
            MainFragmentDirections.actionMainScreenToEmojiCollection(
                category, categoryEmojis.toTypedArray()
            )
        )
    }
}