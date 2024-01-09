package enzzom.hexemoji.ui.fragments.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentEntryBinding

class EntryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentEntryBinding.inflate(inflater, container, false).let {
            it.entryScreenButtonPlay.setOnClickListener { navigateToMainScreen() }
            it.root
        }
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_entry_screen_to_main_screen)
    }
}