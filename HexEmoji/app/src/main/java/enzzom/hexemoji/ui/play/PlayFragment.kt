package enzzom.hexemoji.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentPlayBinding
import enzzom.hexemoji.models.GameMode
import enzzom.hexemoji.ui.play.adapters.GameModeAdapter

class PlayFragment : Fragment() {
    private var binding: FragmentPlayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.gameModeList?.apply {
            setHasFixedSize(true)
            adapter = GameModeAdapter(getGameModes())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

    private fun getGameModes(): List<GameMode> {
        val gameModes = mutableListOf<GameMode>()

        val gameModeTitles = resources.getStringArray(R.array.game_mode_titles)
        val gameModeDescriptions = resources.getStringArray(R.array.game_mode_descriptions)
        val gameModeTitleColors = resources.getIntArray(R.array.game_mode_title_text_color)
        val gameModePrimaryColors = resources.getIntArray(R.array.game_mode_primary_color)
        val gameModeEmojis= resources.getStringArray(R.array.game_mode_emoji)

        repeat(gameModeTitles.size) { index ->
            gameModes.add(
                GameMode(
                    gameModeTitles[index],
                    gameModeDescriptions[index],
                    gameModeTitleColors[index],
                    gameModePrimaryColors[index],
                    gameModeEmojis[index]
                )
            )
        }

        return gameModes.toList()
    }
}