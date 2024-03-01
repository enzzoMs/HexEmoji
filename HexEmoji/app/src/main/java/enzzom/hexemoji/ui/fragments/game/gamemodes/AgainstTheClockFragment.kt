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
import enzzom.hexemoji.ui.custom.GameTutorialDataProvider
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.gamemodes.model.AgainstTheClockViewModel

private const val ONE_MINUTE_IN_SECONDS = 60
private const val TIMER_WARMING_POINT_IN_SECONDS = 30

@AndroidEntryPoint
class AgainstTheClockFragment : BaseGameModeFragment() {

    override val gameViewModel: AgainstTheClockViewModel by viewModels()

    override fun onPause() {
        super.onPause()

        gameViewModel.pauseTimer()
    }

    override fun onResume() {
        super.onResume()

        if (gameViewModel.isTimerPaused() && gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
            showGamePausedDialog()
        }
    }


    override fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        val binding = FragmentAgainstTheClockBinding.inflate(inflater, container, false)

        binding.apply {
            againstTheClockCountdown.addOnCountdownFinished { gameViewModel.startTimer() }

            againstTheClockTimer.setOnClickListener {
                if (gameViewModel.getGameStatus() == GameStatus.IN_PROGRESS) {
                    gameViewModel.pauseTimer()
                    showGamePausedDialog()
                }
            }
        }

        gameViewModel.remainingSeconds.observe(viewLifecycleOwner) { remainingSeconds ->
            binding.againstTheClockTimer.text = resources.getString(
                R.string.game_timer_template,
                remainingSeconds / ONE_MINUTE_IN_SECONDS,
                remainingSeconds % ONE_MINUTE_IN_SECONDS
            )

            if (remainingSeconds <= TIMER_WARMING_POINT_IN_SECONDS) {
                binding.againstTheClockTimer.apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.warming_color))
                    setIconTintResource(R.color.warming_color)
                }
            }

            val gameStatus = gameViewModel.getGameStatus()

            if (gameStatus == GameStatus.VICTORY || gameStatus == GameStatus.DEFEAT) {
                gameViewModel.pauseTimer()

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


    override fun getTutorialDataProvider(): GameTutorialDataProvider {
        val tutorialDescriptions = resources.getStringArray(R.array.game_tutorial_descriptions_against_the_clock)

        val imagesTypedArray = resources.obtainTypedArray(R.array.game_tutorial_images_against_the_clock)

        val tutorialImagesId = List(imagesTypedArray.length()) { index ->
            imagesTypedArray.getResourceId(index, R.drawable.game_tutorial_board_example)
        }

        imagesTypedArray.recycle()

        return object : GameTutorialDataProvider {
            override fun getDescription(position: Int): String {
                return tutorialDescriptions.getOrElse(position) { tutorialDescriptions.last() }
            }

            override fun getDrawableId(position: Int): Int {
                return tutorialImagesId[position]
            }

            override fun getTotalItems(): Int {
                return tutorialImagesId.size
            }
        }
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
                setOnDismissListener { gameViewModel.resumeTimer() }
            }.show()
        }
    }
}
