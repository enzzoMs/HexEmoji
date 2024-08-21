package ems.hexemoji.ui.fragments.game.gamemodes

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ems.hexemoji.R
import ems.hexemoji.databinding.FragmentShuffledBinding
import ems.hexemoji.models.GameStatus
import ems.hexemoji.ui.fragments.game.BaseGameModeFragment
import ems.hexemoji.ui.fragments.game.gamemodes.model.ShuffledViewModel

private const val SHUFFLE_INDICATOR_ANIM_DURATION_MS = 400L

@AndroidEntryPoint
class ShuffledFragment : BaseGameModeFragment() {

    override val gameViewModel: ShuffledViewModel by viewModels()
    private lateinit var shuffleIndicatorAnimator: ObjectAnimator

    override fun onPause() {
        shuffleIndicatorAnimator.pause()
        gameViewModel.shuffleTimerCurrentMs = shuffleIndicatorAnimator.currentPlayTime
        super.onPause()
    }

    override fun onResume() {
        shuffleIndicatorAnimator.resume()
        super.onResume()
    }

    override fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        val binding = FragmentShuffledBinding.inflate(inflater, container, false)

        binding.apply {
            shuffleIndicatorAnimator = ObjectAnimator.ofInt(
                shuffleCountdownIndicator, "progress", 100, 0
            ).apply {
                interpolator = LinearInterpolator()
                duration = gameViewModel.shuffleIntervalMs

                doOnEnd {
                    if (gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
                        ObjectAnimator.ofArgb(
                            shuffledIndicatorCard, "strokeColor",
                            ContextCompat.getColor(requireContext(), R.color.game_mode_primary_color_shuffled),
                            ContextCompat.getColor(requireContext(), R.color.surface_color)
                        ).apply {
                            duration = SHUFFLE_INDICATOR_ANIM_DURATION_MS
                        }.start()

                        gameViewModel.shufflePair()?.let {
                            shuffledGameBoard.rebindCardEmoji(it.first, it.second)
                        }

                        shuffleIndicatorAnimator.start()
                    }
                }
            }
        }

        if (gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
            shuffleIndicatorAnimator.currentPlayTime = gameViewModel.shuffleTimerCurrentMs
            shuffleIndicatorAnimator.start()
        } else {
            binding.shuffleCountdownIndicator.progress = 100
            binding.shuffledStartCountdown.addOnCountdownFinished {
                shuffleIndicatorAnimator.start()
            }
        }

        return binding.let {
            GameViews(
                layoutRoot = it.root,
                gameBoardView = it.shuffledGameBoard,
                countDownView = it.shuffledStartCountdown
            )
        }
    }

    override fun getGameModeThemeId(): Int = R.style.ThemeOverlay_HexEmoji_GameMode_Shuffled

    override fun getTutorialDescriptionsArrayId(): Int = R.array.game_tutorial_descriptions_shuffled

    override fun getTutorialImagesArrayId(): Int = R.array.game_tutorial_images_shuffled
}