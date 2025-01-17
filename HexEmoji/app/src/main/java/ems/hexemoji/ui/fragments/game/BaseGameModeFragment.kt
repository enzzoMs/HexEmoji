package ems.hexemoji.ui.fragments.game

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.review.ReviewManagerFactory
import ems.hexemoji.R
import ems.hexemoji.databinding.DialogGameEndedBinding
import ems.hexemoji.models.BoardSize
import ems.hexemoji.models.GameMode
import ems.hexemoji.models.GameStatus
import ems.hexemoji.ui.custom.BoardTutorialView
import ems.hexemoji.ui.custom.CountDownView
import ems.hexemoji.ui.custom.EmojiCardView
import ems.hexemoji.ui.custom.GameBoardAdapter
import ems.hexemoji.ui.custom.GameBoardView
import ems.hexemoji.ui.custom.PagedViewDataProvider
import ems.hexemoji.ui.fragments.game.BaseGameViewModel.FlipResult

private const val BOARD_EXIT_ANIMATION_DURATION = 400L

/**
 * Abstract base class for game mode fragments. It implements the basic functionality needed to manage
 * the game board, handle user interaction, and display endgame dialogs.
 */
abstract class BaseGameModeFragment : Fragment() {

    protected abstract val gameViewModel: BaseGameViewModel
    private lateinit var endgameDialog: Dialog
    private lateinit var gameBoard: GameBoardView
    private lateinit var entryCountdownTimer: CountDownView

    private var timerPaused = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (parentFragment as GameFragment).setGameTutorialDataProvider(getTutorialDataProvider())

        val (layoutRoot, gameBoardView, countdownView) = initializeViews(inflater, container)
        gameBoard = gameBoardView
        entryCountdownTimer = countdownView
        endgameDialog = Dialog(requireContext())

        val gameStatus = gameViewModel.getGameStatus()

        if (gameStatus == GameStatus.VICTORY || gameStatus == GameStatus.DEFEAT) {
            gameBoardView.visibility = View.INVISIBLE
            gameBoardView.enableCardInteraction(false)
            showEndgameDialog()
        }

        gameBoardView.enableBoardMovement(!gameViewModel.shouldExecuteEntryAnimation())

        val boardSize = gameViewModel.getGameBoardSize()

        GameBoardAdapter(
            executeBoardEntryAnimation = gameViewModel.shouldExecuteEntryAnimation(),
            gridSpanCount = boardSize.numOfColumns,
            numberOfEmojiCards = boardSize.getSizeInHexagonalLayout(),
            emojiCardSizePx = resources.getDimensionPixelSize(R.dimen.emoji_card_size),
            emojiCardMarginPx = resources.getDimensionPixelSize(R.dimen.hexagonal_board_item_margin),
            getEmojiCardForPosition = { gameViewModel.getCardForPosition(it) },
            onEmojiCardClicked = ::onEmojiCardClicked
        ).apply {
            entryAnimationFinished.observe(viewLifecycleOwner) { finished ->
                if (gameViewModel.shouldExecuteEntryAnimation() && finished) {
                    countdownView.visibility = View.VISIBLE
                    countdownView.start()
                }
            }

            enableCardInteraction(!gameViewModel.shouldExecuteEntryAnimation())
            gameBoardView.setGameBoardAdapter(this)
        }

        countdownView.addOnCountdownFinished {
            if (!countdownView.isVisible) {
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
        }

        return layoutRoot
    }

    override fun onPause() {
        entryCountdownTimer.reset()
        timerPaused = true
        super.onPause()
    }

    override fun onResume() {
        if (timerPaused) {
            entryCountdownTimer.start()
        }
        super.onResume()
    }

    protected fun executeBoardExitAnimation() {
        gameBoard.apply {
            enableCardInteraction(false)
            enableBoardMovement(false)
        }

        ObjectAnimator.ofFloat(gameBoard, "alpha", 0f).apply {
            duration = BOARD_EXIT_ANIMATION_DURATION
            doOnEnd {
                showEndgameDialog()

                if (gameViewModel.shouldShowAppReview()) {
                    showAppReviewDialog()
                }
            }
        }.start()
    }

   private fun onEmojiCardClicked(cardView: EmojiCardView, cardPosition: Int) {
       if (!gameViewModel.isCardFlipped(cardPosition)) {
           val flipResult = gameViewModel.flipCard(cardPosition)

           if (flipResult !is FlipResult.NoMatch) {
               gameBoard.enableCardInteraction(false)
           }

           cardView.flipCard(onAnimationEnd = {
               val showBoardExitAnimation = gameViewModel.shouldExecuteExitAnimation()

               if (flipResult is FlipResult.MatchFailed) {
                   gameBoard.getCardViewForPosition(flipResult.firstCardPosition)?.flipCard(
                       animStartDelay = CARD_FLIP_DELAY
                   )
                   gameBoard.getCardViewForPosition(flipResult.secondCardPosition)?.flipCard(
                       animStartDelay = CARD_FLIP_DELAY,
                       onAnimationEnd = {
                           val status = gameViewModel.getGameStatus()

                           if ((status == GameStatus.VICTORY || status == GameStatus.DEFEAT) &&
                               showBoardExitAnimation) {

                               executeBoardExitAnimation()
                           } else {
                               gameBoard.enableCardInteraction(true)
                           }
                       }
                   )
               } else if (flipResult is FlipResult.MatchSuccessful) {
                   gameBoard.apply {
                       getCardViewForPosition(flipResult.firstCardPosition)?.matchCard()
                       getCardViewForPosition(flipResult.secondCardPosition)?.matchCard(
                           onAnimationEnd = {
                               val status = gameViewModel.getGameStatus()

                               if ((status == GameStatus.VICTORY || status == GameStatus.DEFEAT) &&
                                   showBoardExitAnimation) {

                                   executeBoardExitAnimation()
                               }
                           }
                       )
                       enableCardInteraction(true)
                   }
               }
           })
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

    private fun showAppReviewDialog() {
        val reviewManager = ReviewManagerFactory.create(requireContext())

        reviewManager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful && isAdded) {
               reviewManager.launchReviewFlow(requireActivity(), task.result)
            }
        }
    }

    private fun showEndgameDialog() {
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

    private fun getTutorialDataProvider(): PagedViewDataProvider {
        val tutorialDescriptions = resources.getStringArray(getTutorialDescriptionsArrayId())

        val imagesTypedArray = resources.obtainTypedArray(getTutorialImagesArrayId())

        val tutorialImagesId = List(imagesTypedArray.length()) { index ->
            imagesTypedArray.getResourceId(index, R.drawable.game_tutorial_board_example)
        }

        imagesTypedArray.recycle()

        return object : PagedViewDataProvider {
            override fun getTitle(position: Int): String? = null

            override fun getDescription(position: Int): String {
                return tutorialDescriptions[position]
            }

            override fun getDrawableId(position: Int): Int {
                return tutorialImagesId.getOrElse(position) { tutorialImagesId.last() }
            }

            override fun getTotalItems(): Int {
                return tutorialDescriptions.size
            }
        }
    }

    private fun navigateToMainScreen() {
        if (isAdded) {
            findNavController().navigate(R.id.action_game_screen_to_main_screen)
        }
    }

    private fun replayGame() {
        arguments?.let {
            if (isAdded) {
                findNavController().navigate(
                    GameFragmentDirections.actionReplayGameScreen(
                        GameMode.valueOf(it.getString(GAME_MODE_ARG_KEY)!!),
                        BoardSize.valueOf(it.getString(BOARD_SIZE_ARG_KEY)!!),
                        it.getStringArray(SELECTED_CATEGORIES_ARG_KEY)!!
                    )
                )
            }
        }
    }

    protected abstract fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews

    protected abstract fun getGameModeThemeId(): Int

    protected abstract fun getTutorialDescriptionsArrayId(): Int

    protected abstract fun getTutorialImagesArrayId(): Int

    data class GameViews(
        val layoutRoot: View,
        val gameBoardView: GameBoardView,
        val countDownView: CountDownView
    )

    companion object {
        const val BOARD_SIZE_ARG_KEY = "boardSize"
        const val GAME_MODE_ARG_KEY = "gameMode"
        const val SELECTED_CATEGORIES_ARG_KEY = "selectedCategories"
        const val CARD_FLIP_DELAY = 150L
    }
}