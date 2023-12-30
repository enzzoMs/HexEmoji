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
        fun getAll(resources: Resources): List<EmojiCategoryDetails> {
            val emojiCategoryDetails = mutableListOf<EmojiCategoryDetails>()

            val categoryTitles = resources.getStringArray(R.array.emoji_category_titles)
            val categoryColors = resources.getIntArray(R.array.emoji_category_color)
            val categoryDescriptions = resources.getStringArray(R.array.emoji_category_descriptions)
            val categoryEmojiIcons = resources.getStringArray(R.array.emoji_category_emoji_icons)
            val categoryImages = resources.obtainTypedArray(R.array.emoji_category_images)

            EmojiCategory.values().forEachIndexed { index, emojiCategory ->
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