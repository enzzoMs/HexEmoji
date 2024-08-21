package ems.hexemoji.models

data class EmojiCard(
    val emoji: String,
    var positionInBoard: Int,
    var flipped: Boolean = false,
    var matched: Boolean = false
)