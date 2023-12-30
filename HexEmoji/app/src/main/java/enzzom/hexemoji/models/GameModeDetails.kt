package enzzom.hexemoji.models

import android.content.res.Resources
import enzzom.hexemoji.R

data class GameModeDetails(
    val gameMode: GameMode,
    val title: String,
    val description: String,
    val emoji: String,
    val primaryColor: Int,
    val backgroundColor: Int
) {
    companion object {
        fun getAll(resources: Resources): List<GameModeDetails> {
            val gameModeDetails = mutableListOf<GameModeDetails>()

            val gameModeDescriptions = resources.getStringArray(R.array.game_mode_descriptions)
            val gameModePrimaryColors = resources.getIntArray(R.array.game_mode_main_color)
            val gameModeBackgroundColors = resources.getIntArray(R.array.game_mode_emoji_back_color)
            val gameModeEmojis = resources.getStringArray(R.array.game_mode_emojis)

            GameMode.values().forEachIndexed { index, gameMode ->
                gameModeDetails.add(
                    GameModeDetails(
                        gameMode,
                        GameMode.getTitle(gameMode, resources),
                        gameModeDescriptions[index],
                        gameModeEmojis[index],
                        gameModePrimaryColors[index],
                        gameModeBackgroundColors[index]
                    )
                )
            }

            return gameModeDetails.toList()
        }
    }
}
