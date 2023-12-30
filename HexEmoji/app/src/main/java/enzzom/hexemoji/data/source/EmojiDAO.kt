package enzzom.hexemoji.data.source

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import enzzom.hexemoji.models.EmojiCategory

@Dao
interface EmojiDAO {

    @MapInfo(keyColumn = "category", valueColumn = "COUNT()")
    @Query("SELECT category, COUNT() FROM emojis WHERE category IN (:categories) GROUP BY category")
    suspend fun getEmojiCountForCategories(categories: List<EmojiCategory>): Map<EmojiCategory, Int>

    @MapInfo(keyColumn = "category", valueColumn = "COUNT()")
    @Query("SELECT category, COUNT() FROM emojis WHERE category IN (:categories) AND unlocked = 1 GROUP BY category")
    suspend fun getUnlockedCountForCategories(categories: List<EmojiCategory>): Map<EmojiCategory, Int>

    @Query("SELECT unicode FROM emojis WHERE category = :category AND unlocked = 1 ORDER BY RANDOM() LIMIT :numOfEmojis")
    suspend fun getRandomUnlockedEmojis(category: EmojiCategory, numOfEmojis: Int): List<String>
}