package enzzom.hexemoji.ui.fragments.emojis.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.ItemCardCollectionDetailsBinding
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.EmojiCategoryDetails

class CollectionDetailsAdapter(
    private val categoryDetails: List<EmojiCategoryDetails>,
    private val getUnlockedCountForCategory: (EmojiCategory) -> Int?,
    private val getEmojiCountForCategory: (EmojiCategory) -> Int?,
    private val onCollectionClicked: (EmojiCategory) -> Unit
) : RecyclerView.Adapter<CollectionDetailsAdapter.CollectionDetailsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionDetailsHolder {
        val inflater = LayoutInflater.from(parent.context)

        return CollectionDetailsHolder(
            ItemCardCollectionDetailsBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CollectionDetailsHolder, position: Int) {
        holder.bind(categoryDetails[position])
    }

    override fun getItemCount(): Int = categoryDetails.size

    inner class CollectionDetailsHolder(
        private val binding: ItemCardCollectionDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var collectionCategory: EmojiCategory

        init {
            binding.root.setOnClickListener { onCollectionClicked(collectionCategory) }
        }

        fun bind(categoryDetails: EmojiCategoryDetails) {
            collectionCategory = categoryDetails.category

            binding.apply {
                val emojiCount = getEmojiCountForCategory(categoryDetails.category)
                val unlockedCount = getUnlockedCountForCategory(categoryDetails.category)

                if (emojiCount == null || unlockedCount == null) {
                    collectionDetailsUnlockedBar.isIndeterminate = true
                    collectionDetailsUnlockedRatio.text = root.resources.getString(
                        R.string.progress_ratio_template, 0, 0
                    )
                } else {
                    collectionDetailsUnlockedBar.isIndeterminate = false
                    collectionDetailsUnlockedBar.progress = ((unlockedCount / emojiCount.toFloat()) * 100).toInt()

                    collectionDetailsUnlockedRatio.text = root.resources.getString(
                        R.string.progress_ratio_template, unlockedCount, emojiCount
                    )
                }

                collectionDetailsTitle.text = categoryDetails.title

                collectionDetailsTitle.setTextColor(categoryDetails.color)
                collectionDetailsDescription.text = categoryDetails.description
                collectionDetailsIcon.text = categoryDetails.emojiIcon

                collectionDetailsUnlockedBar.apply {
                    setIndicatorColor(categoryDetails.color)
                    trackColor = ColorUtils.setAlphaComponent(categoryDetails.color, trackColor.alpha)
                }

                collectionDetailsUnlockedRatio.setTextColor(categoryDetails.color)
            }
        }
    }
}