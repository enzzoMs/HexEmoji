package enzzom.hexemoji.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentGameModesBinding
import enzzom.hexemoji.ui.play.adapters.GameModeAdapter
import enzzom.hexemoji.ui.play.models.PlayViewModel

class GameModesFragment : Fragment() {
    private val playViewModel: PlayViewModel by activityViewModels()
    private var binding: FragmentGameModesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameModesBinding.inflate(inflater, container, false)

        playViewModel.apply {
            clearGameModeSelection()
            clearEmojiCategoriesSelection()
            clearBoardSizeSelection()
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.gameModeList?.apply {
            setHasFixedSize(true)
            adapter = GameModeAdapter(
                gameModeCards = playViewModel.getGameModeCards(resources),
                onGameModeClicked = { gameModeCard ->
                    playViewModel.selectGameMode(gameModeCard)
                    navigateToEmojisSelection()
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

    private fun navigateToEmojisSelection() {
        findNavController().navigate(R.id.emojis_selection_fragment)
    }
}