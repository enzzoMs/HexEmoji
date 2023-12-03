package enzzom.hexemoji.data.source

import androidx.room.Dao
import androidx.room.Query
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.models.EmojiCategory

@Dao
interface EmojiDAO {

    @Query("SELECT unicode FROM emojis WHERE unlocked = 1 AND category = :category ORDER BY RANDOM() LIMIT :numOfEmojis")
    suspend fun getRandomUnlockedEmojis(category: EmojiCategory, numOfEmojis: Int): List<String>
}