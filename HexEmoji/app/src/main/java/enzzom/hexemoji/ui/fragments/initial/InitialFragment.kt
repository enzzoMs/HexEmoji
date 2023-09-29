package enzzom.hexemoji.ui.fragments.initial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentInitialBinding

class InitialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentInitialBinding.inflate(inflater, container, false)

        binding.initialButtonPlay.setOnClickListener { navigateToMainScreen() }

        return binding.root
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_initial_screen_to_main_screen)
    }
}