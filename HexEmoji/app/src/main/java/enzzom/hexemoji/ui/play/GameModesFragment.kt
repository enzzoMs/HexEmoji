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
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.models.GameModeCard
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

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.gameModeList?.apply {
            setHasFixedSize(true)
            adapter = GameModeAdapter(
                gameModeCards = getGameModeCards(),
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
        findNavController().navigate(R.id.action_navigate_to_emojis_selection)
    }

    private fun getGameModeCards(): List<GameModeCard> {
        val gameModeCards = mutableListOf<GameModeCard>()

        val gameModeTitles = resources.getStringArray(R.array.game_mode_titles)
        val gameModeDescriptions = resources.getStringArray(R.array.game_mode_descriptions)
        val gameModeTitleColors = resources.getIntArray(R.array.game_mode_title_text_color)
        val gameModeEmojiBackColors = resources.getIntArray(R.array.game_mode_emoji_back_color)
        val gameModeEmojis= resources.getStringArray(R.array.game_mode_emoji)

        GameMode.values().forEachIndexed { index, gameMode ->
            gameModeCards.add(
                GameModeCard(
                    gameMode,
                    gameModeTitles[index],
                    gameModeTitleColors[index],
                    gameModeDescriptions[index],
                    gameModeEmojis[index],
                    gameModeEmojiBackColors[index]
                )
            )
        }

        return gameModeCards.toList()
    }
}