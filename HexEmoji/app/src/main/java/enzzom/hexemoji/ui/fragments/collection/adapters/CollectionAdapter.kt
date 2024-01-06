package enzzom.hexemoji.ui.fragments.collection.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.databinding.ItemCardCollectionEmojiBinding
import enzzom.hexemoji.databinding.ItemHeaderCollectionProgressBinding

private const val COLLECTION_PROGRESS_VIEW_TYPE = 0
private const val COLLECTION_EMOJI_VIEW_TYPE = 1

class CollectionAdapter(
    private val collectionEmojis: List<Emoji>,
    private val collectionColor: Int,
    private val collectionLighterColor: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val collectionEmojiCount = collectionEmojis.size
    val collectionUnlockedCount = collectionEmojis.count { emoji -> emoji.unlocked }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            COLLECTION_PROGRESS_VIEW_TYPE -> CollectionProgressHolder(
                ItemHeaderCollectionProgressBinding.inflate(inflater, parent, false)
            )
            else -> CollectionEmojiHolder(
                ItemCardCollectionEmojiBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CollectionProgressHolder) {
            holder.bind()
        } else if (holder is CollectionEmojiHolder) {
            holder.bind(collectionEmojis[position])
        }
    }

    override fun getItemCount(): Int = collectionEmojis.size

    override fun getItemViewType(position: Int): Int {
        return if (position == COLLECTION_PROGRESS_VIEW_POSITION) COLLECTION_PROGRESS_VIEW_TYPE else COLLECTION_EMOJI_VIEW_TYPE
    }

    inner class CollectionProgressHolder(
        private val binding: ItemHeaderCollectionProgressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.apply {
                collectionProgressUnlockedBar.apply {
                    setIndicatorColor(collectionColor)
                    trackColor = ColorUtils.setAlphaComponent(collectionColor, trackColor.alpha)
                    progress = ((collectionUnlockedCount / collectionEmojiCount.toFloat()) * 100).toInt()
                }

                collectionProgressUnlockedRatio.text = root.resources.getString(
                    R.string.collection_unlocked_ratio_template, collectionUnlockedCount, collectionEmojiCount
                )
                collectionProgressUnlockedRatio.setTextColor(collectionColor)
            }
        }
    }

    inner class CollectionEmojiHolder(
        private val binding: ItemCardCollectionEmojiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(emoji: Emoji) {
            binding.apply {
                collectionCard.strokeColor = collectionLighterColor
                collectionCardEmoji.setTextColor(collectionColor)

                if (emoji.unlocked) {
                    collectionCardEmoji.text = emoji.unicode
                    collectionCardEmoji.visibility = View.VISIBLE
                    collectionCardLockedIcon.visibility = View.GONE
                } else {
                    collectionCardEmoji.visibility = View.GONE
                    collectionCardLockedIcon.visibility = View.VISIBLE
                    collectionCardLockedIcon.setColorFilter(collectionColor)
                    collectionCardLockedIcon.setBackgroundColor(collectionLighterColor)
                }
            }
        }
    }

    companion object {
        const val COLLECTION_PROGRESS_VIEW_POSITION = 0
    }
}