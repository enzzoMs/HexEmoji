package enzzom.hexemoji.ui.fragments.emojis.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.ItemCardEmojiCollectionInfoBinding
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.EmojiCategoryDetails

class EmojiCollectionInfoAdapter(
    private val emojiCategoryDetails: List<EmojiCategoryDetails>,
    private val getUnlockedCountForCategory: (EmojiCategory) -> Int?,
    private val getEmojiCountForCategory: (EmojiCategory) -> Int?
) : RecyclerView.Adapter<EmojiCollectionInfoAdapter.EmojiCollectionInfoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiCollectionInfoHolder {
        val inflater = LayoutInflater.from(parent.context)

        return EmojiCollectionInfoHolder(
            ItemCardEmojiCollectionInfoBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EmojiCollectionInfoHolder, position: Int) {
        holder.bind(emojiCategoryDetails[position])
    }

    override fun getItemCount(): Int = emojiCategoryDetails.size

    inner class EmojiCollectionInfoHolder(
        private val binding: ItemCardEmojiCollectionInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryDetails: EmojiCategoryDetails) {
            binding.apply {
                val emojiCount = getEmojiCountForCategory(categoryDetails.category)
                val unlockedCount = getUnlockedCountForCategory(categoryDetails.category)

                if (emojiCount == null || unlockedCount == null) {
                    emojiCollectionInfoUnlockedBar.isIndeterminate = true
                    emojiCollectionInfoUnlockedRatio.text = root.resources.getString(
                        R.string.emoji_collection_unlocked_ratio_template, 0, 0
                    )
                } else {
                    emojiCollectionInfoUnlockedBar.isIndeterminate = false
                    emojiCollectionInfoUnlockedBar.progress = ((unlockedCount / emojiCount.toFloat()) * 100).toInt()

                    emojiCollectionInfoUnlockedRatio.text = root.resources.getString(
                        R.string.emoji_collection_unlocked_ratio_template, unlockedCount, emojiCount
                    )
                }

                emojiCollectionInfoTitle.text = categoryDetails.title

                emojiCollectionInfoTitle.setTextColor(categoryDetails.color)
                emojiCollectionInfoDescription.text = categoryDetails.description
                emojiCollectionInfoIcon.text = categoryDetails.emojiIcon

                emojiCollectionInfoUnlockedBar.apply {
                    setIndicatorColor(categoryDetails.color)
                    trackColor = ColorUtils.setAlphaComponent(categoryDetails.color, trackColor.alpha)
                }

                emojiCollectionInfoUnlockedRatio.setTextColor(categoryDetails.color)
            }
        }
    }
}