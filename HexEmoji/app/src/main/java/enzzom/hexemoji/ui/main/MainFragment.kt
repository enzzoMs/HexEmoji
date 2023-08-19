package enzzom.hexemoji.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

        val navController = NavHostFragment.findNavController(
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        )

        val destinationIds = mutableSetOf<Int>()

        navController.graph.nodes.forEach {_, navDestination ->
            destinationIds.add(navDestination.id)
        }

        val appBarConfiguration = AppBarConfiguration(destinationIds)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNav?.setupWithNavController(navController)
        binding.navRail?.setupWithNavController(navController)
        binding.navView?.setupWithNavController(navController)

        return binding.root
    }
}