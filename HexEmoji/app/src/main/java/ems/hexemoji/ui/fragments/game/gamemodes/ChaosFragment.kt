package ems.hexemoji.ui.fragments.game.gamemodes

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ems.hexemoji.R
import ems.hexemoji.databinding.DialogGamePausedBinding
import ems.hexemoji.databinding.FragmentChaosBinding
import ems.hexemoji.models.GameStatus
import ems.hexemoji.ui.fragments.game.BaseGameModeFragment
import ems.hexemoji.ui.fragments.game.gamemodes.model.ChaosViewModel

private const val ONE_MINUTE_IN_SECONDS = 60

private const val TIMER_WARNING_POINT_IN_SECONDS = 30
private const val REMAINING_MOVES_TO_WARNING = 15

private const val SHUFFLE_INDICATOR_ANIM_DURATION_MS = 400L

@AndroidEntryPoint
class ChaosFragment : BaseGameModeFragment() {

    override val gameViewModel: ChaosViewModel by viewModels()
    private lateinit var shuffleIndicatorAnimator: ObjectAnimator

    override fun onStop() {
        gameViewModel.countdownManager.pauseTimer()
        pauseShuffleIndicator()
        super.onStop()
    }

    override fun onResume() {
        if (gameViewModel.countdownManager.isTimerPaused() && gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
            showGamePausedDialog()
            pauseShuffleIndicator()
        } else {
            shuffleIndicatorAnimator.resume()
        }

        super.onResume()
    }

    override fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        val binding = FragmentChaosBinding.inflate(inflater, container, false)

        binding.apply {
            // Against the clock ---------------------------------------------------------------

            chaosCountdown.addOnCountdownFinished {
                if (gameViewModel.getGameStatus() == GameStatus.STARTING) {
                    gameViewModel.countdownManager.startTimer()
                }
            }

            chaosTimer.setOnClickListener {
                if (gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
                    gameViewModel.countdownManager.pauseTimer()
                    pauseShuffleIndicator()
                    showGamePausedDialog()
                }
            }

            gameViewModel.countdownManager.remainingSeconds.observe(viewLifecycleOwner) { remainingSeconds ->
                chaosTimer.text = resources.getString(
                    R.string.game_timer_template,
                    remainingSeconds / ONE_MINUTE_IN_SECONDS,
                    remainingSeconds % ONE_MINUTE_IN_SECONDS
                )

                if (remainingSeconds <= TIMER_WARNING_POINT_IN_SECONDS) {
                    chaosTimer.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_color))
                        setIconTintResource(R.color.warning_color)
                    }
                }

                val gameStatus = gameViewModel.getGameStatus()

                if (gameStatus == GameStatus.VICTORY || gameStatus == GameStatus.DEFEAT) {
                    gameViewModel.countdownManager.pauseTimer()

                    if (remainingSeconds == 0L && gameViewModel.remainingMoves.value != 0) {
                        executeBoardExitAnimation()
                    }
                }
            }

            // Limited Moves -----------------------------------------------------------------

            var showingMovesWarning = false

            gameViewModel.remainingMoves.observe(viewLifecycleOwner) { remainingMoves ->
                chaosRemainingMoves.text = String.format(
                    "%0${gameViewModel.initialMoves.toString().length}d", remainingMoves
                )
                chaosMovesProgressIndicator.progress = (
                    (remainingMoves / gameViewModel.initialMoves.toFloat()) * 100
                ).toInt()

                if (!showingMovesWarning && remainingMoves <= REMAINING_MOVES_TO_WARNING) {
                    val warmingColor = ContextCompat.getColor(requireContext(), R.color.warning_color)

                    chaosRemainingMoves.setTextColor(warmingColor)
                    chaosMovesProgressIndicator.setIndicatorColor(warmingColor)
                    chaosMovesProgressIndicator.trackColor = ColorUtils.setAlphaComponent(
                        warmingColor, resources.getInteger(R.integer.progress_indicator_track_alpha)
                    )

                    showingMovesWarning = true
                }
            }

            // Shuffled ----------------------------------------------------------------------

            shuffleIndicatorAnimator = ObjectAnimator.ofInt(
                chaosShuffleCountdownIndicator, "progress", 100, 0
            ).apply {
                interpolator = LinearInterpolator()
                duration = gameViewModel.shuffleIntervalMs

                doOnEnd {
                    if (gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
                        ObjectAnimator.ofArgb(
                            chaosShuffleIndicatorCard, "strokeColor",
                            ContextCompat.getColor(requireContext(), R.color.game_mode_primary_color_shuffled),
                            ContextCompat.getColor(requireContext(), R.color.surface_color)
                        ).apply {
                            duration = SHUFFLE_INDICATOR_ANIM_DURATION_MS
                        }.start()

                        gameViewModel.shufflePair()?.let {
                            chaosGameBoard.rebindCardEmoji(it.first, it.second)
                        }

                        shuffleIndicatorAnimator.start()
                    }
                }
            }

            if (gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
                shuffleIndicatorAnimator.currentPlayTime = gameViewModel.shuffleTimerCurrentMs
                shuffleIndicatorAnimator.start()
            } else {
                chaosShuffleCountdownIndicator.progress = 100
                chaosCountdown.addOnCountdownFinished {
                    shuffleIndicatorAnimator.start()
                }
            }
        }

        return binding.let {
            GameViews(
                layoutRoot = it.root,
                gameBoardView = it.chaosGameBoard,
                countDownView = it.chaosCountdown
            )
        }
    }

    override fun getGameModeThemeId(): Int = R.style.ThemeOverlay_HexEmoji_GameMode_Chaos

    override fun getTutorialDescriptionsArrayId(): Int = R.array.game_tutorial_descriptions_chaos

    override fun getTutorialImagesArrayId(): Int = R.array.game_tutorial_images_chaos

    private fun pauseShuffleIndicator() {
        shuffleIndicatorAnimator.pause()
        gameViewModel.shuffleTimerCurrentMs = shuffleIndicatorAnimator.currentPlayTime
    }

    private fun showGamePausedDialog() {
        val gamePausedDialog = Dialog(requireContext())

        DialogGamePausedBinding.inflate(
            LayoutInflater.from(ContextThemeWrapper(context, getGameModeThemeId()))
        ).let {
            it.root.setOnClickListener { gamePausedDialog.dismiss() }

            gamePausedDialog.apply {
                setContentView(it.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setOnDismissListener {
                    gameViewModel.countdownManager.resumeTimer()
                    shuffleIndicatorAnimator.resume()
                }
            }.show()
        }
    }
}