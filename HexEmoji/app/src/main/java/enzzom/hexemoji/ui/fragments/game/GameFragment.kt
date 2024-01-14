package enzzom.hexemoji.ui.fragments.game

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
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.DialogExitGameBinding
import enzzom.hexemoji.databinding.FragmentGameBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.ui.custom.BoardTutorialView
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider
import enzzom.hexemoji.ui.custom.GameTutorialView

@AndroidEntryPoint
class GameFragment : Fragment() {

    private val args: GameFragmentArgs by navArgs()
    private var gameTutorialDataProvider: GameTutorialDataProvider? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val themedInflater = LayoutInflater.from(ContextThemeWrapper(context, getGameModeThemeId(args.gameMode)))
        val binding = FragmentGameBinding.inflate(themedInflater, container, false)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.game_screen_status_bar_color)

        binding.gameToolbar.apply {
            title = GameMode.getTitle(args.gameMode, resources)
            setNavigationOnClickListener { showExitGameDialog() }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.game_info -> {
                        showGameTutorialDialog()
                        true
                    }
                    else -> false
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitGameDialog()
                }
            }
        )

        if (savedInstanceState == null) {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.game_fragment_container, when(args.gameMode) {
                    GameMode.ZEN -> ZenFragment()
                    else -> ZenFragment()
                })
            }
        }

        return binding.root
    }

    fun setGameTutorialDataProvider(dataProvider: GameTutorialDataProvider) {
        gameTutorialDataProvider = dataProvider
    }

    fun getBoardSize(): BoardSize = args.boardSize

    fun getSelectedEmojiCategories(): List<EmojiCategory> {
        return args.selectedEmojiCategories.map { EmojiCategory.valueOf(it) }
    }

    fun showBoardTutorialDialog() {
        val boardTutorialView = BoardTutorialView(ContextThemeWrapper(context, getGameModeThemeId(args.gameMode)))

        Dialog(requireContext()).apply {
            setContentView(boardTutorialView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
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

    private fun showGameTutorialDialog() {
        if (gameTutorialDataProvider != null) {
            val gameTutorialView = GameTutorialView(ContextThemeWrapper(context, getGameModeThemeId(args.gameMode)))

            gameTutorialView.setDataProvider(gameTutorialDataProvider!!)

            Dialog(requireContext()).apply {
                setContentView(gameTutorialView)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
            }
        } else {
            view?.let {
                Snackbar.make(it, R.string.snackbar_no_available_game_tutorial, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_game_screen_to_main_screen)
    }

    private fun getGameModeThemeId(gameMode: GameMode): Int = when(gameMode) {
        GameMode.ZEN -> R.style.ThemeOverlay_HexEmoji_GameMode_Zen
        else -> R.style.ThemeOverlay_HexEmoji_GameMode_Zen
    }
}