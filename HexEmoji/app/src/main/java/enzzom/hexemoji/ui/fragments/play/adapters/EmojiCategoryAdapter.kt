package enzzom.hexemoji.ui.fragments.play.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemCardEmojiCategoryBinding
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.EmojiCategoryDetails

class EmojiCategoryAdapter(
    private val emojiCategoryDetails: List<EmojiCategoryDetails>,
    private val onEmojiCategoryClicked: (EmojiCategory) -> Unit,
    private val isCategorySelected: (EmojiCategory) -> Boolean
) : RecyclerView.Adapter<EmojiCategoryAdapter.EmojiCategoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiCategoryHolder {
        val inflater = LayoutInflater.from(parent.context)

        return EmojiCategoryHolder(
            ItemCardEmojiCategoryBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EmojiCategoryHolder, position: Int) {
        holder.bind(emojiCategoryDetails[position])
    }

    override fun getItemCount(): Int = emojiCategoryDetails.size

    inner class EmojiCategoryHolder(
        private val binding: ItemCardEmojiCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var category: EmojiCategory

        init {
            binding.emojiCategoryCard.setOnClickListener {
                onEmojiCategoryClicked(category)
                binding.emojiCategoryCard.isSelected = isCategorySelected(category)
            }
        }

        fun bind(categoryDetails: EmojiCategoryDetails) {
            category = categoryDetails.category

            binding.apply {
                emojiCategoryCard.isSelected = isCategorySelected(categoryDetails.category)
                emojiCategoryTitle.text = categoryDetails.title
                emojiCategoryTitle.setTextColor(categoryDetails.color)
                emojiCategoryDescription?.text = categoryDetails.description
                emojiCategoryImage.setImageResource(categoryDetails.categoryImageId)
            }
        }
    }
}