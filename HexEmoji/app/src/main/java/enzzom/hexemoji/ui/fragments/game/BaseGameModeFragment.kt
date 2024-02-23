package enzzom.hexemoji.ui.fragments.game

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.DialogGameEndedBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.ui.custom.BoardTutorialView
import enzzom.hexemoji.ui.custom.CountDownView
import enzzom.hexemoji.ui.custom.GameBoardAdapter
import enzzom.hexemoji.ui.custom.GameBoardView
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider
import enzzom.hexemoji.ui.fragments.game.BaseGameViewModel.FlipResult

private const val MATCH_FAILED_CARD_FLIP_DELAY = 150L

/**
 * TODO
 */
@AndroidEntryPoint
abstract class BaseGameModeFragment : Fragment() {

    protected abstract val gameViewModel: BaseGameViewModel
    private lateinit var gameBoardAdapter: GameBoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (parentFragment as GameFragment).setGameTutorialDataProvider(getTutorialDataProvider())

        val (layoutRoot, gameBoardView, countdownView) = initializeViews(inflater, container)
        val boardSize = gameViewModel.getBoardSize()

        gameBoardView.enableBoardMovement(!gameViewModel.shouldExecuteEntryAnimation())

        gameBoardAdapter = GameBoardAdapter(
            executeBoardEntryAnimation = gameViewModel.shouldExecuteEntryAnimation(),
            gridSpanCount = boardSize.numOfColumns,
            numberOfEmojiCards = boardSize.getSizeInHexagonalLayout(),
            emojiCardSizePx = resources.getDimensionPixelSize(R.dimen.emoji_card_size),
            emojiCardMarginPx = resources.getDimensionPixelSize(R.dimen.hexagonal_board_item_margin),
            getEmojiCardForPosition = { gameViewModel.getCardForPosition(it) },
            onEmojiCardClicked = { cardView, cardPosition ->
                if (!gameViewModel.isCardFlipped(cardPosition)) {
                    val flipResult = gameViewModel.flipCard(cardPosition)

                    if (flipResult !is FlipResult.NoMatch) {
                        gameBoardView.enableCardInteraction(false)
                    }

                    cardView.flipCard(onAnimationEnd = {
                        if (flipResult is FlipResult.MatchFailed) {
                            gameBoardView.getCardViewForPosition(flipResult.firstCardPosition)?.flipCard(
                                animStartDelay = MATCH_FAILED_CARD_FLIP_DELAY
                            )
                            gameBoardView.getCardViewForPosition(flipResult.secondCardPosition)?.flipCard(
                                animStartDelay = MATCH_FAILED_CARD_FLIP_DELAY,
                                onAnimationEnd = {
                                    gameBoardView.enableCardInteraction(true)
                                }
                            )
                        } else if (flipResult is FlipResult.MatchSuccessful) {
                            val remainingCardsCount = gameViewModel.getRemainingCardsCount()

                            gameBoardView.apply {
                                getCardViewForPosition(flipResult.firstCardPosition)?.matchCard()
                                getCardViewForPosition(flipResult.secondCardPosition)?.matchCard(
                                    onAnimationEnd = {
                                        if (remainingCardsCount == 0) {
                                            checkGameCompletion()
                                        }
                                    }
                                )
                                enableCardInteraction(true)
                            }
                        }
                    })
                }
            }
        ).apply {
            entryAnimationFinished.observe(viewLifecycleOwner) { finished ->
                if (gameViewModel.shouldExecuteEntryAnimation() && finished) {
                    countdownView.visibility = View.VISIBLE
                    countdownView.start()
                }
            }

            exitAnimationFinished.observe(viewLifecycleOwner) { finished ->
                val status = gameViewModel.getGameStatus()

                if (finished && (status == GameStatus.VICTORY || status == GameStatus.DEFEAT)) {
                    gameBoardView.visibility = View.INVISIBLE
                    showEndgameDialog()
                }
            }

            enableCardInteraction(!gameViewModel.shouldExecuteEntryAnimation())
            gameBoardView.setGameBoardAdapter(this)
        }

        countdownView.addOnCountdownFinished {
            gameViewModel.entryAnimationFinished()

            gameBoardView.apply {
                enableBoardMovement(true)
                enableCardInteraction(true)
            }

            if (gameBoardView.isBoardLargerThanViewport() && gameViewModel.shouldShowBoardTutorial()) {
                showBoardTutorialDialog()
                gameViewModel.boardTutorialShown()
            }
        }

        return layoutRoot
    }

    protected fun checkGameCompletion() {
        val status = gameViewModel.getGameStatus()

        if (status == GameStatus.VICTORY || status == GameStatus.DEFEAT) {
            gameBoardAdapter.executeBoardExitAnimation()
        }
    }

    private fun showBoardTutorialDialog() {
        val boardTutorialView = BoardTutorialView(ContextThemeWrapper(context, getGameModeThemeId()))

        Dialog(requireContext()).apply {
            setContentView(boardTutorialView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun showEndgameDialog() {
        val endgameDialog = Dialog(requireContext())

        DialogGameEndedBinding.inflate(
            LayoutInflater.from(ContextThemeWrapper(context, getGameModeThemeId()))
        ).let {
            it.gameEndedMessage.text = when (gameViewModel.getGameStatus()) {
                GameStatus.VICTORY -> resources.getString(R.string.game_ended_victory)
                GameStatus.DEFEAT ->  resources.getString(R.string.game_ended_defeat)
                else -> ""
            }

            it.gameEndedEmoji.text = resources.getString(
                if (gameViewModel.getGameStatus() == GameStatus.VICTORY) {
                    R.string.game_ended_victory_emoji
                } else {
                    R.string.game_ended_defeat_emoji
                }
            )

            it.gameEndedButtonExit.setOnClickListener {
                endgameDialog.dismiss()
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

            it.gameEndedButtonReplay.setOnClickListener {
                endgameDialog.dismiss()
                replayGame()
            }

            endgameDialog.apply {
                setContentView(it.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(false)
                setOnCancelListener { navigateToMainScreen() }
                show()
            }
        }
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_game_screen_to_main_screen)
    }

    private fun replayGame() {
        arguments?.let {
            findNavController().navigate(
                GameFragmentDirections.actionReplayGameScreen(
                    GameMode.valueOf(it.getString(GAME_MODE_ARG_KEY)!!),
                    BoardSize.valueOf(it.getString(BOARD_SIZE_ARG_KEY)!!),
                    it.getStringArray(SELECTED_CATEGORIES_ARG_KEY)!!
                )
            )
        }
    }

    protected abstract fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews

    protected abstract fun getGameModeThemeId(): Int

    protected abstract fun getTutorialDataProvider(): GameTutorialDataProvider

    data class GameViews(
        val layoutRoot: View,
        val gameBoardView: GameBoardView,
        val countDownView: CountDownView
    )

    companion object {
        const val BOARD_SIZE_ARG_KEY = "boardSize"
        const val GAME_MODE_ARG_KEY = "gameMode"
        const val SELECTED_CATEGORIES_ARG_KEY = "selectedCategories"
    }
}