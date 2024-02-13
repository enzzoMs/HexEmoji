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
        activity?.apply {
            window.statusBarColor = ContextCompat.getColor(
                requireContext(), R.color.game_screen_status_bar_color
            )
            onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object: OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        showExitGameDialog()
                    }
                }
            )
        }

        if (savedInstanceState == null) {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.game_fragment_container, when(args.gameMode) {
                    GameMode.ZEN -> ZenFragment()
                    else -> ZenFragment()
                })
            }
        }

        FragmentGameBinding.inflate(
            LayoutInflater.from(ContextThemeWrapper(context, getGameModeThemeId(args.gameMode))),
            container,
            false
        ).let {
            it.gameToolbar.apply {
                title = args.gameMode.getTitle(resources)
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
            return it.root
        }
    }

    fun setGameTutorialDataProvider(dataProvider: GameTutorialDataProvider) {
        gameTutorialDataProvider = dataProvider
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
        val exitGameDialog = Dialog(requireContext())

        DialogExitGameBinding.inflate(layoutInflater).let {
            it.exitGameButtonCancel.setOnClickListener {
                exitGameDialog.dismiss()
            }
            it.exitGameButtonConfirm.setOnClickListener {
                exitGameDialog.dismiss()
                navigateToMainScreen()
            }
            exitGameDialog.apply {
                setContentView(it.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
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
                Snackbar.make(it, R.string.no_available_game_tutorial_msg, Snackbar.LENGTH_SHORT).show()
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

    companion object {
        const val BOARD_SIZE_ARG_KEY = "boardSize"
        const val SELECTED_CATEGORIES_ARG_KEY = "selectedCategories"
    }
}