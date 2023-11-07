package enzzom.hexemoji.data.repositories

import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.data.source.EmojiDAO
import enzzom.hexemoji.models.EmojiCategory
import javax.inject.Inject

class EmojiRepository @Inject constructor(
    private val emojiDAO: EmojiDAO
) {

    /**
     * Get a list of random unlocked emojis from specified categories. The method tries to make
     * each category have roughly the same size.
     */
    suspend fun getRandomUnlockedEmojis(categories: List<EmojiCategory>, totalNumberOfEmojis: Int): List<Emoji> {
        val numberOfEmojisPerCategory = totalNumberOfEmojis / categories.size

        val emojis = mutableListOf<Emoji>()

        categories.forEachIndexed { index, category ->
            emojis.addAll(emojiDAO.getRandomUnlockedEmojis(category, if (index == categories.lastIndex)
                // If it is the last category, then add any remaining emojis
                numberOfEmojisPerCategory + (totalNumberOfEmojis % categories.size)
            else
                numberOfEmojisPerCategory
            ))
        }

        return emojis.toList()
    }
}