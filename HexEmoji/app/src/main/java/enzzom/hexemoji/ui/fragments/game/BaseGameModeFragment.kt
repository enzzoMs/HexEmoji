package enzzom.hexemoji.ui.fragments.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.ui.custom.CountDownView
import enzzom.hexemoji.ui.custom.GameBoardAdapter
import enzzom.hexemoji.ui.custom.GameBoardView
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider
import enzzom.hexemoji.ui.fragments.game.model.GameViewModel
import enzzom.hexemoji.ui.fragments.game.model.GameViewModel.FlipResult

private const val MATCH_FAILED_CARD_FLIP_DELAY = 150L

/**
 * TODO
 */
@AndroidEntryPoint
abstract class BaseGameModeFragment : Fragment() {

    private val gameViewModel: GameViewModel by viewModels({ requireParentFragment() })
    private lateinit var gameBoardAdapter: GameBoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (parentFragment as GameFragment).setGameTutorialDataProvider(getTutorialDataProvider())

        val (layoutRoot, gameBoardView, countdownView) = getGameViews(inflater, container)
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
                                            checkGameStatus()
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
                if (finished && gameViewModel.getGameStatus() == GameStatus.VICTORY) {
                    gameBoardView.visibility = View.INVISIBLE
                    (parentFragment as GameFragment).showVictoryDialog()
                }
            }

            enableCardInteraction(!gameViewModel.shouldExecuteEntryAnimation())
            gameBoardView.setGameBoardAdapter(this)
        }

        countdownView.setOnCountdownFinished {
            gameViewModel.entryAnimationFinished()

            gameBoardView.apply {
                enableBoardMovement(true)
                enableCardInteraction(true)
            }

            if (gameBoardView.isBoardLargerThanViewport() && gameViewModel.shouldShowBoardTutorial()) {
                (parentFragment as GameFragment).showBoardTutorialDialog()
                gameViewModel.boardTutorialShown()
            }
        }

        return layoutRoot
    }

    private fun checkGameStatus() {
        when (gameViewModel.getGameStatus()) {
            GameStatus.VICTORY -> {
                gameBoardAdapter.executeBoardExitAnimation()
            }
            else -> Unit
        }
    }

    protected abstract fun getTutorialDataProvider(): GameTutorialDataProvider

    protected abstract fun getGameViews(inflater: LayoutInflater, container: ViewGroup?): GameViews

    data class GameViews(
        val layoutRoot: View,
        val gameBoardView: GameBoardView,
        val countDownView: CountDownView
    )
}