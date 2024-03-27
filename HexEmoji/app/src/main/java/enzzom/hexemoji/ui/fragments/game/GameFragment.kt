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
import androidx.core.os.bundleOf
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
import enzzom.hexemoji.ui.custom.GameTutorialView
import enzzom.hexemoji.ui.custom.PagedViewDataProvider
import enzzom.hexemoji.ui.fragments.game.gamemodes.AgainstTheClockFragment
import enzzom.hexemoji.ui.fragments.game.gamemodes.LimitedMovesFragment
import enzzom.hexemoji.ui.fragments.game.gamemodes.ZenFragment

@AndroidEntryPoint
class GameFragment : Fragment() {

    private val args: GameFragmentArgs by navArgs()
    private var gameTutorialDataProvider: PagedViewDataProvider? = null
    private var gameModeThemeId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameModeThemeId = when(args.gameMode) {
            GameMode.ZEN -> R.style.ThemeOverlay_HexEmoji_GameMode_Zen
            GameMode.AGAINST_THE_CLOCK -> R.style.ThemeOverlay_HexEmoji_GameMode_AgainstTheClock
            GameMode.LIMITED_MOVES -> R.style.ThemeOverlay_HexEmoji_GameMode_LimitedMoves
            else -> R.style.ThemeOverlay_HexEmoji_GameMode_Zen
        }

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
            val argsBundle = bundleOf().also {
                it.putString(BaseGameModeFragment.BOARD_SIZE_ARG_KEY, args.boardSize.name)
                it.putString(BaseGameModeFragment.GAME_MODE_ARG_KEY, args.gameMode.name)
                it.putStringArray(BaseGameModeFragment.SELECTED_CATEGORIES_ARG_KEY, args.selectedCategories)
            }

            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.game_fragment_container, when(args.gameMode) {
                    GameMode.ZEN -> ZenFragment()
                    GameMode.AGAINST_THE_CLOCK -> AgainstTheClockFragment()
                    GameMode.LIMITED_MOVES -> LimitedMovesFragment()
                    else -> ZenFragment()
                }.also {
                    it.arguments = argsBundle
                })
            }
        }

        val binding = FragmentGameBinding.inflate(
            LayoutInflater.from(ContextThemeWrapper(context, gameModeThemeId)),
            container,
            false
        ).apply {
            gameToolbar.apply {
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
        }

        return binding.root
    }

    fun setGameTutorialDataProvider(dataProvider: PagedViewDataProvider) {
        gameTutorialDataProvider = dataProvider
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_game_screen_to_main_screen)
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
            val gameTutorialView = GameTutorialView(ContextThemeWrapper(context, gameModeThemeId))

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
}