package enzzom.hexemoji.ui.main

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val bottomNav = binding.bottomNav

        val navController = NavHostFragment.findNavController(
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        )

        // This prevents navigation and animation from occurring when a item is reselected
        bottomNav.setOnItemReselectedListener {  }

        // Setting up the background animation for when a bottom nav item is selected

        // Initialize with the ID of the first item
        var currentSelectedMenuId: Int = binding.bottomNav.menu.getItem(0).itemId

        startAnimationForViewBackground(bottomNav.findViewById(currentSelectedMenuId))

        bottomNav.setOnItemSelectedListener { menuItem ->
            navController.navigate(menuItem.itemId)

            resetAnimationForViewBackground(bottomNav.findViewById(currentSelectedMenuId))
            startAnimationForViewBackground(bottomNav.findViewById(menuItem.itemId))
            currentSelectedMenuId = menuItem.itemId

            true
        }

        return binding.root
    }


    private fun startAnimationForViewBackground(view: View) {
        (view.background as AnimatedVectorDrawable).start()
    }

    private fun resetAnimationForViewBackground(view: View) {
        (view.background as AnimatedVectorDrawable).apply {
            stop()
            reset()
        }
    }
}