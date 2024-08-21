package ems.hexemoji.ui.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import ems.hexemoji.R
import ems.hexemoji.databinding.FragmentGameModesBinding
import ems.hexemoji.models.GameModeDetails
import ems.hexemoji.ui.fragments.main.MainFragment
import ems.hexemoji.ui.fragments.play.adapters.GameModeAdapter
import ems.hexemoji.ui.fragments.play.model.PlayViewModel

class GameModesFragment : Fragment() {

    private val playViewModel: PlayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (parentFragment?.parentFragment as MainFragment).setToolbarTitle(
            resources.getString(R.string.page_title_page_play)
        )

        val binding = FragmentGameModesBinding.inflate(inflater, container, false)

        playViewModel.clearGameModeSelection()

        binding.gameModeList.apply {
            setHasFixedSize(true)
            adapter = GameModeAdapter(
                gameModeDetails = GameModeDetails.getAll(resources),
                onGameModeClicked = { gameModeCard ->
                    playViewModel.selectGameMode(gameModeCard)

                    playViewModel.clearCategoriesSelection()

                    (parentFragment?.parentFragment as MainFragment).navigateToCategorySelection()
                },
                useHeaderViews = layoutManager is GridLayoutManager,
                pageDescription = resources.getString(R.string.page_description_game_modes)
            )
            if (layoutManager is GridLayoutManager) {
                (layoutManager as GridLayoutManager).spanSizeLookup = object : SpanSizeLookup() {

                    override fun getSpanSize(position: Int): Int {
                        return if (position == GameModeAdapter.PAGE_DESCRIPTION_VIEW_POSITION) {
                            resources.getInteger(R.integer.game_modes_grid_span)
                        } else 1
                    }
                }
            }
        }

        return binding.root
    }
}