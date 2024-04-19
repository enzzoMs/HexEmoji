package sarueh.hexemoji.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import sarueh.hexemoji.models.EmojiCategory
import sarueh.hexemoji.models.GameMode

@Entity(
    tableName = "limited_moves_challenges",
    foreignKeys = [ForeignKey(
        entity = Emoji::class,
        parentColumns = ["unicode"],
        childColumns = ["reward_emoji_unicode"]
    )]
)
class LimitedMovesChallenge(
    id: Long = 0,
    totalGames: Int,
    completedGames: Int,
    category: EmojiCategory,
    rewardEmojiUnicode: String,
    @ColumnInfo(name = "game_mode") val gameMode: GameMode,
    @ColumnInfo(name = "move_limit") val moveLimit: Int
) : Challenge(id, totalGames, completedGames, rewardEmojiUnicode, category)