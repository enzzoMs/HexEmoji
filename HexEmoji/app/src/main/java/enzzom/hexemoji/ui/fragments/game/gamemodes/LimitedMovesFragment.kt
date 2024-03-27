package enzzom.hexemoji.ui.fragments.game.gamemodes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentLimitedMovesBinding
import enzzom.hexemoji.models.GameStatus
import enzzom.hexemoji.ui.custom.PagedViewDataProvider
import enzzom.hexemoji.ui.fragments.game.BaseGameModeFragment
import enzzom.hexemoji.ui.fragments.game.gamemodes.model.LimitedMovesViewModel

private const val REMAINING_MOVES_TO_WARNING = 15

@AndroidEntryPoint
class LimitedMovesFragment : BaseGameModeFragment() {

    override val gameViewModel: LimitedMovesViewModel by viewModels()

    override fun initializeViews(inflater: LayoutInflater, container: ViewGroup?): GameViews {
        val binding = FragmentLimitedMovesBinding.inflate(inflater, container, false)

        var showingWarning = false

        binding.apply {
            gameViewModel.remainingMoves.observe(viewLifecycleOwner) { remainingMoves ->
                limitedMovesRemainingMoves.text = String.format("%02d", remainingMoves)
                limitedMovesProgressIndicator.progress = (
                    (remainingMoves / gameViewModel.initialMoves.toFloat()) * 100
                ).toInt()

                if (!showingWarning && remainingMoves <= REMAINING_MOVES_TO_WARNING) {
                    val warmingColor = ContextCompat.getColor(requireContext(), R.color.warning_color)

                    limitedMovesRemainingMoves.setTextColor(warmingColor)
                    limitedMovesProgressIndicator.setIndicatorColor(warmingColor)
                    limitedMovesProgressIndicator.trackColor = ColorUtils.setAlphaComponent(
                        warmingColor, resources.getInteger(R.integer.progress_indicator_track_alpha)
                    )

                    showingWarning = true
                }

                if (remainingMoves == 0 && gameViewModel.getGameStatus() == GameStatus.DEFEAT) {
                    executeBoardExitAnimation()
                }
            }
        }

        return binding.let {
            GameViews(
                layoutRoot = it.root,
                gameBoardView = it.limitedMovesGameBoard,
                countDownView = it.limitedMovesCountdown
            )
        }
    }

    override fun getGameModeThemeId(): Int = R.style.ThemeOverlay_HexEmoji_GameMode_LimitedMoves

    override fun getTutorialDescriptionsArrayId(): Int = R.array.game_tutorial_descriptions_limited_moves

    override fun getTutorialImagesArrayId(): Int = R.array.game_tutorial_images_limited_moves
}