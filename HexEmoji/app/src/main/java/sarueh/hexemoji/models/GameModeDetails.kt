package sarueh.hexemoji.models

import android.content.res.Resources
import sarueh.hexemoji.R

data class GameModeDetails(
    val gameMode: GameMode,
    val title: String,
    val description: String,
    val emoji: String,
    val primaryColor: Int,
    val backgroundColor: Int
) {
    companion object {
        fun getAll(res: Resources): List<GameModeDetails> {
            val gameModeDetails = mutableListOf<GameModeDetails>()

            val gameModeDescriptions = res.getStringArray(R.array.game_mode_descriptions)
            val gameModePrimaryColors = res.getIntArray(R.array.game_mode_main_color)
            val gameModeBackgroundColors = res.getIntArray(R.array.game_mode_emoji_back_color)
            val gameModeEmojis = res.getStringArray(R.array.game_mode_emojis)

            GameMode.entries.forEachIndexed { index, gameMode ->
                gameModeDetails.add(
                    GameModeDetails(
                        gameMode,
                        gameMode.getTitle(res),
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
