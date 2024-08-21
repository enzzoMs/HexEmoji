package ems.hexemoji.ui.fragments.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ems.hexemoji.R
import ems.hexemoji.databinding.FragmentEntryBinding

class EntryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark_color)

        return FragmentEntryBinding.inflate(inflater, container, false).apply {
            entryScreenButtonPlay.setOnClickListener { navigateToMainScreen() }
            buttonInstructions.setOnClickListener { navigateToInstructionsScreen() }
        }.root
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_entry_screen_to_main_screen)
    }

    private fun navigateToInstructionsScreen() {
        findNavController().navigate(R.id.action_entry_screen_to_instructions)
    }
}