package enzzom.hexemoji.ui.fragments.game
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentZenBinding
import enzzom.hexemoji.models.BoardSize
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider
import enzzom.hexemoji.ui.fragments.game.adapters.GameBoardAdapter
import enzzom.hexemoji.ui.fragments.game.model.GameViewModel
import enzzom.hexemoji.ui.fragments.game.model.GameViewModel.FlipResult
import javax.inject.Inject

private const val COUNTDOWN_FADE_ANIMATION_DURATION = 200L
private const val COUNTDOWN_FADE_ANIMATION_DELAY = 500L

private const val MATCH_FAILED_CARD_FLIP_DELAY = 100L

@AndroidEntryPoint
class ZenFragment : Fragment() {

    private lateinit var boardSize: BoardSize
    private lateinit var selectedEmojiCategories: List<EmojiCategory>
    @Inject lateinit var gameViewModelFactory: GameViewModel.Factory

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModel.provideFactory(gameViewModelFactory, boardSize, selectedEmojiCategories)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentZenBinding.inflate(inflater, container, false)

        (parentFragment as GameFragment).apply {
            boardSize = getBoardSize()
            selectedEmojiCategories = getSelectedEmojiCategories()
        }

        setGameTutorial()

        binding.apply {
            zenGameBoard.enableBoardMovement(!gameViewModel.shouldExecuteEntryAnimation())

            GameBoardAdapter(
                gridSpanCount = boardSize.numOfColumns,
                numberOfEmojiCards = boardSize.getSizeInHexagonalLayout(),
                emojiCardSizePx = resources.getDimensionPixelSize(R.dimen.emoji_card_size),
                emojiCardMarginPx = resources.getDimensionPixelSize(R.dimen.hexagonal_board_item_margin),
                getEmojiCardForPosition = { gameViewModel.getEmojiCardForPosition(it) },
                onEmojiCardClicked = { emojiCardView, position ->
                    if (!gameViewModel.isEmojiCardFlipped(position)) {
                        val flipResult = gameViewModel.flipEmojiCard(position)

                        if (flipResult !is FlipResult.NoMatch) {
                            zenGameBoard.enableCardInteraction(false)
                        }

                        emojiCardView.flipCard (onAnimationEnd = {
                            if (flipResult is FlipResult.MatchFailed) {
                                flipResult.firstCardPosition.let {
                                    gameViewModel.flipEmojiCard(it)
                                    zenGameBoard.getCardViewForPosition(it)?.flipCard(
                                        animStartDelay = MATCH_FAILED_CARD_FLIP_DELAY
                                    )
                                }
                                flipResult.secondCardPosition.let {
                                    gameViewModel.flipEmojiCard(it)
                                    zenGameBoard.getCardViewForPosition(it)?.flipCard(
                                        animStartDelay = MATCH_FAILED_CARD_FLIP_DELAY,
                                        onAnimationEnd = {
                                            zenGameBoard.enableCardInteraction(true)
                                        }
                                    )
                                }
                            } else if (flipResult is FlipResult.MatchSuccessful) {
                                zenGameBoard.getCardViewForPosition(flipResult.firstCardPosition)?.matchCard()
                                zenGameBoard.getCardViewForPosition(flipResult.secondCardPosition)?.matchCard()
                                zenGameBoard.enableCardInteraction(true)
                            }
                        })
                    }
                },
                executeBoardEntryAnimation = gameViewModel.shouldExecuteEntryAnimation()
            ).apply {
                animationFinished.observe(viewLifecycleOwner) { animationFinished ->
                    if (gameViewModel.shouldExecuteEntryAnimation() && animationFinished) {
                        zenCountdown.visibility = View.VISIBLE
                        zenCountdown.start()
                    }
                }
                enableCardInteraction(!gameViewModel.shouldExecuteEntryAnimation())
                zenGameBoard.setGameBoardAdapter(this)
            }

            zenCountdown.countdownFinished.observe(viewLifecycleOwner) { countdownFinished ->
                if (countdownFinished) {
                    ObjectAnimator.ofFloat(zenCountdown, "alpha", 0f).apply {
                        duration = COUNTDOWN_FADE_ANIMATION_DURATION
                        startDelay = COUNTDOWN_FADE_ANIMATION_DELAY

                        doOnEnd {
                            zenCountdown.visibility = View.GONE

                            zenGameBoard.enableBoardMovement(true)
                            zenGameBoard.enableCardInteraction(true)

                            gameViewModel.boardEntryAnimationFinished()

                            if (zenGameBoard.isBoardLargerThanScreen() && gameViewModel.shouldShowBoardTutorial()) {
                                (parentFragment as GameFragment).showBoardTutorialDialog()
                                gameViewModel.boardTutorialFinished()
                            }
                        }

                        start()
                    }
                }
            }
        }

        return binding.root
    }

    private fun setGameTutorial() {
        val gameTutorialDescriptions = resources.getStringArray(R.array.game_tutorial_descriptions_zen)

        val tutorialImagesTypedArray = resources.obtainTypedArray(R.array.game_tutorial_images_zen)
        val gameTutorialImagesId = Array(tutorialImagesTypedArray.length()) { 0 }

        for (i in 0 until tutorialImagesTypedArray.length()) {
            gameTutorialImagesId[i] = tutorialImagesTypedArray.getResourceId(i, R.drawable.game_tutorial_board_example)
        }

        tutorialImagesTypedArray.recycle()

        (parentFragment as GameFragment).setGameTutorialDataProvider(
            object : GameTutorialDataProvider {
                override fun getDescription(position: Int): String {
                    return gameTutorialDescriptions[position]
                }

                override fun getDrawableId(position: Int): Int {
                    return gameTutorialImagesId[position]
                }

                override fun getTotalItems(): Int {
                    return gameTutorialDescriptions.size
                }
            }
        )
    }
}