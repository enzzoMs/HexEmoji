package enzzom.hexemoji.ui.fragments.game.gamemodes

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.DialogGamePausedBinding
import enzzom.hexemoji.databinding.FragmentAgainstTheClockBinding
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.gamemodes.model.AgainstTheClockViewModel

private const val ONE_MINUTE_IN_SECONDS = 60
private const val TIMER_WARNING_POINT_IN_SECONDS = 30

@AndroidEntryPoint
class AgainstTheClockFragment : BaseGameModeFragment() {

    override val gameViewModel: AgainstTheClockViewModel by viewModels()

    override fun onPause() {
        gameViewModel.countdownManager.pauseTimer()

        super.onPause()
    }

    override fun onResume() {
        if (gameViewModel.countdownManager.isTimerPaused() && gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
            showGamePausedDialog()
        }

        super.onResume()
    }

    override fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        val binding = FragmentAgainstTheClockBinding.inflate(inflater, container, false)

        binding.apply {
            againstTheClockCountdown.addOnCountdownFinished { gameViewModel.countdownManager.startTimer() }

            againstTheClockTimer.setOnClickListener {
                if (gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
                    gameViewModel.countdownManager.pauseTimer()
                    showGamePausedDialog()
                }
            }
        }

        gameViewModel.countdownManager.remainingSeconds.observe(viewLifecycleOwner) { remainingSeconds ->
            binding.againstTheClockTimer.text = resources.getString(
                R.string.game_timer_template,
                remainingSeconds / ONE_MINUTE_IN_SECONDS,
                remainingSeconds % ONE_MINUTE_IN_SECONDS
            )

            if (remainingSeconds <= TIMER_WARNING_POINT_IN_SECONDS) {
                binding.againstTheClockTimer.apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_color))
                    setIconTintResource(R.color.warning_color)
                }
            }

            val gameStatus = gameViewModel.getGameStatus()

            if (gameStatus == GameStatus.VICTORY || gameStatus == GameStatus.DEFEAT) {
                gameViewModel.countdownManager.pauseTimer()

                if (remainingSeconds == 0L) {
                    executeBoardExitAnimation()
                }
            }
        }

        return binding.let {
            GameViews(
                layoutRoot = it.root,
                gameBoardView = it.againstTheClockGameBoard,
                countDownView = it.againstTheClockCountdown
            )
        }
    }

    override fun getGameModeThemeId(): Int = R.style.ThemeOverlay_HexEmoji_GameMode_AgainstTheClock

    override fun getTutorialDescriptionsArrayId(): Int = R.array.game_tutorial_descriptions_against_the_clock

    override fun getTutorialImagesArrayId(): Int = R.array.game_tutorial_images_against_the_clock

    private fun showGamePausedDialog() {
        val gamePausedDialog = Dialog(requireContext())

        DialogGamePausedBinding.inflate(
            LayoutInflater.from(ContextThemeWrapper(context, getGameModeThemeId()))
        ).let {
            it.root.setOnClickListener { gamePausedDialog.dismiss() }

            gamePausedDialog.apply {
                setContentView(it.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setOnDismissListener { gameViewModel.countdownManager.resumeTimer() }
            }.show()
        }
    }
}
