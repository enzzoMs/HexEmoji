package enzzom.hexemoji.data.repositories

import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.data.source.EmojiDAO
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.utils.StringUtils
import javax.inject.Inject

class EmojiRepository @Inject constructor(
    private val emojiDAO: EmojiDAO
) {

    suspend fun getAllEmojisByCategory(): Map<EmojiCategory, List<Emoji>> {
        return emojiDAO.getAllEmojisByCategory()
    }

    /**
     * Get a list of random unlocked emojis from specified categories. The method tries to make
     * each category have roughly the same size.
     */
    suspend fun getRandomUnlockedEmojis(categories: List<EmojiCategory>, totalNumberOfEmojis: Int): List<String> {
        val numberOfEmojisPerCategory = if (categories.size > totalNumberOfEmojis) 1 else totalNumberOfEmojis / categories.size

        val emojis = mutableListOf<String>()

        for (i in 0..categories.size) {
            emojis.addAll(emojiDAO.getRandomUnlockedEmojis(categories[i], if (i == categories.lastIndex)
                // If it is the last category, then add any remaining emojis
                numberOfEmojisPerCategory + (totalNumberOfEmojis % categories.size)
            else
                numberOfEmojisPerCategory
            ))
            if (emojis.size >= totalNumberOfEmojis) break
        }

        return emojis.map { StringUtils.unescapeString(it) }
    }
}