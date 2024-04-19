package sarueh.hexemoji.data.source

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import sarueh.hexemoji.data.entities.Emoji
import sarueh.hexemoji.models.EmojiCategory

@Dao
interface EmojiDAO {

    @Query("UPDATE emojis SET unlocked = 1 WHERE unicode = :unicode")
    suspend fun unlockEmoji(unicode: String)

    @Query("UPDATE emojis SET unlocked = 1 WHERE unlocked = 0")
    suspend fun unlockAllEmojis()

    @MapInfo(keyColumn = "category")
    @Query("SELECT * FROM emojis")
    suspend fun getAllEmojisByCategory(): Map<EmojiCategory, List<Emoji>>

    @Query("SELECT * FROM emojis WHERE unlocked = 0")
    suspend fun getAllLockedEmojis(): List<Emoji>

    @Query("SELECT unicode FROM emojis WHERE category = :category AND unlocked = 1 ORDER BY RANDOM() LIMIT :numOfEmojis")
    suspend fun getRandomUnlockedEmojis(category: EmojiCategory, numOfEmojis: Int): List<String>
}