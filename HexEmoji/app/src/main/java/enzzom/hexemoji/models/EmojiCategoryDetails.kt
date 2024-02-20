package enzzom.hexemoji.models

import android.content.res.Resources
import enzzom.hexemoji.R

data class EmojiCategoryDetails(
    val category: EmojiCategory,
    val title: String,
    val description: String,
    val emojiIcon: String,
    val color: Int,
    val categoryImageId: Int
) {
    companion object {
        fun getAll(res: Resources): List<EmojiCategoryDetails> {
            val emojiCategoryDetails = mutableListOf<EmojiCategoryDetails>()

            val categoryTitles = res.getStringArray(R.array.emoji_category_titles)
            val categoryColors = res.getIntArray(R.array.emoji_category_color)
            val categoryDescriptions = res.getStringArray(R.array.emoji_category_descriptions)
            val categoryEmojiIcons = res.getStringArray(R.array.emoji_category_emoji_icons)
            val categoryImages = res.obtainTypedArray(R.array.emoji_category_images)

            EmojiCategory.entries.forEachIndexed { index, emojiCategory ->
                emojiCategoryDetails.add(
                    EmojiCategoryDetails(
                        emojiCategory,
                        categoryTitles[index],
                        categoryDescriptions[index],
                        categoryEmojiIcons[index],
                        categoryColors[index],
                        categoryImages.getResourceId(index, R.color.surface_color)
                    )
                )
            }

            categoryImages.recycle()

            return emojiCategoryDetails.toList()
        }
    }
}