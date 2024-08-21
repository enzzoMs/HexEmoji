package ems.hexemoji.ui.fragments.collection.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.RecyclerView
import ems.hexemoji.R
import ems.hexemoji.data.entities.Emoji
import ems.hexemoji.databinding.ItemCardCollectionEmojiBinding
import ems.hexemoji.databinding.ItemHeaderCollectionProgressBinding

private const val COLLECTION_PROGRESS_VIEW_TYPE = 0
private const val COLLECTION_EMOJI_VIEW_TYPE = 1
private const val HEADER_VIEW_COUNT = 1

class CollectionAdapter(
    private var collectionEmojis: List<Emoji>,
    private val collectionColor: Int,
    private val collectionLighterColor: Int,
    private val onUnlockedEmojiClicked: (Emoji) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val collectionEmojiCount = collectionEmojis.size
    val collectionUnlockedCount = collectionEmojis.count { emoji -> emoji.unlocked }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == COLLECTION_PROGRESS_VIEW_TYPE) {
            CollectionProgressHolder(
                ItemHeaderCollectionProgressBinding.inflate(inflater, parent, false)
            )
        }
        else {
            CollectionEmojiHolder(
                ItemCardCollectionEmojiBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CollectionProgressHolder) {
            holder.bind()
        } else if (holder is CollectionEmojiHolder) {
            holder.bind(collectionEmojis[position - HEADER_VIEW_COUNT])
        }
    }

    override fun getItemCount(): Int {
        return collectionEmojis.size + HEADER_VIEW_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == COLLECTION_PROGRESS_VIEW_POSITION) COLLECTION_PROGRESS_VIEW_TYPE else COLLECTION_EMOJI_VIEW_TYPE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceCollection(newCollection: List<Emoji>) {
        collectionEmojis = newCollection
        notifyDataSetChanged()
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
                    R.string.progress_ratio_template, collectionUnlockedCount, collectionEmojiCount
                )
                collectionProgressUnlockedRatio.setTextColor(collectionColor)
            }
        }
    }

    inner class CollectionEmojiHolder(
        private val binding: ItemCardCollectionEmojiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var emoji: Emoji

        init {
            binding.collectionCard.setOnClickListener {
                if (emoji.unlocked) {
                    onUnlockedEmojiClicked(emoji)
                } else {
                    (binding.collectionCardLockedIcon.drawable as AnimatedVectorDrawable).start()
                }
            }
        }

        fun bind(emoji: Emoji) {
            this.emoji = emoji

            binding.apply {
                collectionCard.strokeColor = collectionLighterColor

                if (emoji.unlocked) {
                    collectionCardEmoji.text = emoji.emoji
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