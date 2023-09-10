package enzzom.hexemoji.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        val navController = NavHostFragment.findNavController(
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        )

        // Manually wiring up the toolbar and screen navigation without using the
        // 'setupWithNavController()' method to prevent the navigation component from applying an
        // animation to the toolbar title when navigating back

        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            when (navDestination.id) {
                R.id.game_modes_fragment, R.id.statistics_fragment, R.id.emojis_fragment -> {
                    showNavigationViews(true)
                    setToolbarTitle(navDestination.label as String)
                    showBackArrow(false)
                }
            }
        }

        binding?.apply {
            toolbar.setNavigationOnClickListener {
                navController.popBackStack()
            }

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
            toolbarHoneycomb.visibility = visibility
            bottomNav?.visibility = visibility
            navRail?.visibility = visibility
            navView?.visibility = visibility
        }
    }

    fun setToolbarTitle(title: String) {
        binding?.toolbar?.title = title
    }

    fun showBackArrow(show: Boolean) {
        if (show) {
            binding?.toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        } else {
            binding?.toolbar?.navigationIcon = null
        }
    }
}