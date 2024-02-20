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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.DialogExitGameBinding
import enzzom.hexemoji.databinding.DialogGameFinishedBinding
import enzzom.hexemoji.databinding.FragmentGameBinding
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.ui.custom.BoardTutorialView
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider
import enzzom.hexemoji.ui.custom.GameTutorialView
import enzzom.hexemoji.ui.fragments.game.model.GameViewModel

@AndroidEntryPoint
class GameFragment : Fragment() {

    private val args: GameFragmentArgs by navArgs()
    private val gameViewModel: GameViewModel by viewModels()
    private var gameTutorialDataProvider: GameTutorialDataProvider? = null
    private var gameModeThemeId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameModeThemeId = when(args.gameMode) {
            GameMode.ZEN -> R.style.ThemeOverlay_HexEmoji_GameMode_Zen
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
            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.game_fragment_container, when(args.gameMode) {
                    GameMode.ZEN -> ZenFragment()
                    else -> ZenFragment()
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

    fun setGameTutorialDataProvider(dataProvider: GameTutorialDataProvider) {
        gameTutorialDataProvider = dataProvider
    }

    fun showBoardTutorialDialog() {
        val boardTutorialView = BoardTutorialView(ContextThemeWrapper(context, gameModeThemeId))

        Dialog(requireContext()).apply {
            setContentView(boardTutorialView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    fun showVictoryDialog() {
        val victoryDialog = Dialog(requireContext())

        DialogGameFinishedBinding.inflate(
            LayoutInflater.from(ContextThemeWrapper(context, gameModeThemeId))
        ).let {
            it.gameFinishedButtonExit.setOnClickListener {
                victoryDialog.dismiss()
                navigateToMainScreen()
            }

            it.challengesProgressPending?.text = resources.getString(
                R.string.challenges_progress_template_pending,
                gameViewModel.getPendingChallengesCount().toString()
            )

            it.challengesProgressInProgress?.text = resources.getString(
                R.string.challenges_progress_template_in_progress,
                gameViewModel.getChallengesInProgressCount().toString()
            )

            it.challengesProgressCompleted?.text = resources.getString(
                R.string.challenges_progress_template_completed,
                gameViewModel.getCompletedChallengesCount().toString()
            )

            it.gameFinishedButtonReplay.setOnClickListener {
                victoryDialog.dismiss()
                replayGame()
            }

            victoryDialog.apply {
                setContentView(it.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(false)
                setOnCancelListener { navigateToMainScreen() }
                show()
            }
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

    private fun replayGame() {
        findNavController().navigate(
            GameFragmentDirections.actionReplayGameScreen(
                args.gameMode, args.boardSize, args.selectedCategories
            )
        )
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_game_screen_to_main_screen)
    }

    companion object {
        const val BOARD_SIZE_ARG_KEY = "boardSize"
        const val GAME_MODE_ARG_KEY = "gameMode"
        const val SELECTED_CATEGORIES_ARG_KEY = "selectedCategories"
    }
}