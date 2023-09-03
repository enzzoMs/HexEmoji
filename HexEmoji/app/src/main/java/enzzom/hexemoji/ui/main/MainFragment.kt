package enzzom.hexemoji.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        val navController = NavHostFragment.findNavController(
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        )

        // Manually wiring up the toolbar and screen navigation without using the
        // 'setupWithNavController()' method to prevent the navigation component from applying an
        // animation to the toolbar title when navigating back

        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            binding!!.toolbar.title = navDestination.label

            when (navDestination.id) {
                R.id.emojis_selection_fragment -> {
                    showNavigationViews(false)
                    binding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                }
                else -> {
                    showNavigationViews(true)
                    binding!!.toolbar.navigationIcon = null
                }
            }
        }

        binding!!.apply {
            toolbar.setNavigationOnClickListener {
                navController.popBackStack()
            }

            bottomNav?.setupWithNavController(navController)
            navRail?.setupWithNavController(navController)
            navView?.setupWithNavController(navController)
        }

        return binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

    private fun showNavigationViews(show: Boolean) {
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
}