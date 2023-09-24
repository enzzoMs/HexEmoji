package enzzom.hexemoji.ui.game

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.DialogExitGameBinding
import enzzom.hexemoji.databinding.FragmentGameBinding
import enzzom.hexemoji.models.GameMode

class GameFragment : Fragment() {

    private val args: GameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newInflater = LayoutInflater.from(ContextThemeWrapper(context, R.style.ThemeOverlay_HexEmoji_GameMode_Zen))
        val binding = FragmentGameBinding.inflate(newInflater, container, false)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.game_screen_status_bar_color)

        binding.gameToolbar.apply {
            title = GameMode.getGameModeTitle(args.gameMode, resources)
            setNavigationOnClickListener { showExitGameDialog() }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitGameDialog()
                }
            }
        )

        return binding.root
    }

    private fun showExitGameDialog() {
        val exitDialogBinding = DialogExitGameBinding.inflate(layoutInflater)

        val exitGameDialog = Dialog(requireContext()).apply {
            setContentView(exitDialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        exitDialogBinding.apply {
            exitGameButtonCancel.setOnClickListener {
                exitGameDialog.dismiss()
            }
            exitGameButtonConfirm.setOnClickListener {
                exitGameDialog.dismiss()
                navigateToMainScreen()
            }
        }
    }

    private fun navigateToMainScreen() {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark_color)
        findNavController().navigate(R.id.action_game_fragment_to_main_fragment)
    }
}