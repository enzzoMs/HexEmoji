package enzzom.hexemoji.models

data class GameModeCard(
    val gameMode: GameMode,
    val title: String,
    val titleColor: Int,
    val description: String,
    val emoji: String,
    val emojiBackgroundColor: Int
)
