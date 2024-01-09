package enzzom.hexemoji.ui.fragments.play.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.databinding.ItemCardEmojiCategoryBinding
import enzzom.hexemoji.databinding.ItemHeaderCheckboxBinding
import enzzom.hexemoji.databinding.ItemHeaderPageDescriptionBinding
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.EmojiCategoryDetails

private const val PAGE_DESCRIPTION_VIEW_TYPE = 0
private const val CHECKBOX_VIEW_TYPE = 1
private const val CATEGORY_VIEW_TYPE = 2
private const val HEADER_VIEW_COUNT = 2

class EmojiCategoryAdapter(
    private val emojiCategoryDetails: List<EmojiCategoryDetails>,
    private val onCategoryClicked: (EmojiCategory) -> Unit,
    private val isCategorySelected: (EmojiCategory) -> Boolean,
    private val useHeaderViews: Boolean = false,
    private val pageDescription: String = "",
    private val checkboxName: String = "",
    private val isCheckboxChecked: () -> Boolean = { false },
    private val onCheckboxClicked: (checked: Boolean) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val PAGE_DESCRIPTION_VIEW_POSITION = 0
        const val CHECKBOX_VIEW_POSITION = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when {
            useHeaderViews && viewType == PAGE_DESCRIPTION_VIEW_TYPE -> PageDescriptionHolder(
                ItemHeaderPageDescriptionBinding.inflate(inflater, parent, false)
            )
            useHeaderViews && viewType == CHECKBOX_VIEW_TYPE -> CheckboxHolder(
                ItemHeaderCheckboxBinding.inflate(inflater, parent, false)
            )
            else -> EmojiCategoryHolder(
                ItemCardEmojiCategoryBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            useHeaderViews && holder is PageDescriptionHolder -> holder.bind(pageDescription)
            useHeaderViews &&  holder is CheckboxHolder -> holder.bind()
            holder is EmojiCategoryHolder -> holder.bind(
                emojiCategoryDetails[if (useHeaderViews) position - HEADER_VIEW_COUNT else position]
            )
        }
    }

    override fun getItemCount(): Int {
        return emojiCategoryDetails.size + (if (useHeaderViews) HEADER_VIEW_COUNT else 0)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            useHeaderViews && position == PAGE_DESCRIPTION_VIEW_POSITION -> PAGE_DESCRIPTION_VIEW_TYPE
            useHeaderViews && position == CHECKBOX_VIEW_POSITION -> CHECKBOX_VIEW_TYPE
            else -> CATEGORY_VIEW_TYPE
        }
    }

    inner class EmojiCategoryHolder(
        private val binding: ItemCardEmojiCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var category: EmojiCategory

        init {
            binding.emojiCategoryCard.setOnClickListener {
                onCategoryClicked(category)
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

    inner class CheckboxHolder(
        private val binding: ItemHeaderCheckboxBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                checkbox.setOnClickListener { onCheckboxClicked(checkbox.isChecked) }
            }
        }

        fun bind() {
            binding.checkbox.apply {
                text = checkboxName
                isChecked = isCheckboxChecked()
            }
        }
    }
}