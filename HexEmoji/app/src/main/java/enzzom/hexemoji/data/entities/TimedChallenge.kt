package enzzom.hexemoji.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.GameMode

@Entity(
    tableName = "timed_challenges",
    foreignKeys = [ForeignKey(
        entity = Emoji::class,
        parentColumns = ["unicode"],
        childColumns = ["reward_emoji_unicode"]
    )]
)
class TimedChallenge(
    id: Long = 0,
    totalGames: Int,
    completedGames: Int,
    category: EmojiCategory,
    rewardEmojiUnicode: String,
    @ColumnInfo(name = "game_mode") val gameMode: GameMode,
    @ColumnInfo(name = "time_limit_in_seconds") val timeLimitInSeconds: Int
) : Challenge(id, totalGames, completedGames, rewardEmojiUnicode, category)