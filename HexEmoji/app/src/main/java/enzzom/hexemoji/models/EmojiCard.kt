package enzzom.hexemoji.models

data class EmojiCard(
    val emoji: String,
    val positionInBoard: Int,
    var flipped: Boolean = false,
    var matched: Boolean = false
)