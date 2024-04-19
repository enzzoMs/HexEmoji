package sarueh.hexemoji.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import sarueh.hexemoji.models.BoardSize
import sarueh.hexemoji.models.EmojiCategory
import sarueh.hexemoji.models.GameMode

@Entity(
    tableName = "general_challenges",
    foreignKeys = [ForeignKey(
        entity = Emoji::class,
        parentColumns = ["unicode"],
        childColumns = ["reward_emoji_unicode"]
    )]
)
class GeneralChallenge(
    id: Long = 0,
    totalGames: Int,
    completedGames: Int,
    category: EmojiCategory,
    rewardEmojiUnicode: String,
    @ColumnInfo(name = "game_mode") val gameMode: GameMode,
    @ColumnInfo(name = "board_size") val boardSize: BoardSize?,
    @ColumnInfo(name = "consecutive_games") val consecutiveGames: Boolean,
    @ColumnInfo(name = "constrained_to_category") val constrainedToCategory: Boolean
) : Challenge(id, totalGames, completedGames, rewardEmojiUnicode, category)