package enzzom.hexemoji.ui.fragments.game.gamemodes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentSequenceBinding
import enzzom.hexemoji.ui.custom.EmojiCardView
import enzzom.hexemoji.ui.custom.GameBoardView
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.gamemodes.model.SequenceViewModel

@AndroidEntryPoint
class SequenceFragment : BaseGameModeFragment() {

    override val gameViewModel: SequenceViewModel by viewModels()
    private lateinit var gameBoard: GameBoardView

    override fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        val binding = FragmentSequenceBinding.inflate(inflater, container, false)
        gameBoard = binding.sequenceGameBoard

        binding.apply {
            gameViewModel.pairsFound.observe(viewLifecycleOwner) { pairsFound ->
                sequenceRemainingPairs.text = resources.getString(
                    R.string.progress_ratio_template,
                    pairsFound,
                    gameViewModel.sequencePairsGoal
                )

                if (pairsFound == 0) {
                    resetCardState(gameViewModel.getMatchedCardPositions())

                } else if (
                    pairsFound == gameViewModel.sequencePairsGoal &&
                    gameViewModel.getRemainingCardsCount() != 0
                ) (
                    executeBoardExitAnimation()
                )
            }
        }

        return binding.let {
            GameViews(
                layoutRoot = it.root,
                gameBoardView = it.sequenceGameBoard,
                countDownView = it.sequenceCountdown
            )
        }
    }

    override fun getGameModeThemeId(): Int = R.style.ThemeOverlay_HexEmoji_GameMode_Sequence

    override fun getTutorialDescriptionsArrayId(): Int = R.array.game_tutorial_descriptions_sequence

    override fun getTutorialImagesArrayId(): Int = R.array.game_tutorial_images_sequence

    private fun resetCardState(cardPositions: List<Int>) {
        cardPositions.forEach { position ->
            gameBoard.getCardViewForPosition(position)?.apply {
                flipCard(
                    // Synchronizing with the remaining cards on the board that may be flipping
                    animStartDelay = EmojiCardView.CARD_FLIP_ANIMATION_DURATION + CARD_FLIP_DELAY,
                    onAnimationEnd = {
                        matched = false
                    }
                )
            }
        }
    }
}

