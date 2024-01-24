package enzzom.hexemoji.data.entities

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.utils.StringUtils

abstract class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "total_games") val totalGames: Int,
    @ColumnInfo(name = "completed_games") val completedGames: Int,
    @ColumnInfo(name = "reward_emoji_unicode") val rewardEmojiUnicode: String,
    val category: EmojiCategory
) {
    @Ignore val rewardEmoji: String = StringUtils.unescapeString(rewardEmojiUnicode)
    @Ignore val completed: Boolean = completedGames == totalGames
}