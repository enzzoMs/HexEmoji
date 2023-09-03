package enzzom.hexemoji.models

data class EmojiCategoryCard(
    val category: EmojiCategory,
    val title: String,
    val titleColor: Int,
    val description: String,
    val categoryImageId: Int
)