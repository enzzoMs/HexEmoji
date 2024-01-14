package enzzom.hexemoji.ui.fragments.collection

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.databinding.DialogEmojiDetailsBinding
import enzzom.hexemoji.databinding.FragmentEmojiCollectionBinding
import enzzom.hexemoji.models.EmojiCategoryDetails
import enzzom.hexemoji.ui.fragments.collection.adapters.CollectionAdapter

class EmojiCollection : Fragment() {

    private val args: EmojiCollectionArgs by navArgs()
    private lateinit var categoryDetails: EmojiCategoryDetails
    private var categoryLighterColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEmojiCollectionBinding.inflate(inflater, container, false)

        categoryDetails = EmojiCategoryDetails.getAll(resources).find { it.category == args.category }!!

        categoryLighterColor = ColorUtils.blendARGB(
            categoryDetails.color,
            ContextCompat.getColor(requireContext(), R.color.surface_color),
            resources.getInteger(R.integer.emoji_collection_category_color_blend_percentage) / 100f
        )

        activity?.window?.statusBarColor = categoryDetails.color

        binding.apply {
            collectionToolbar.apply {
                setNavigationOnClickListener { findNavController().popBackStack() }

                title = categoryDetails.title
                setBackgroundColor(categoryLighterColor)
                setTitleTextColor(categoryDetails.color)
                setNavigationIconTint(categoryDetails.color)
            }

            collectionDescription.text = categoryDetails.description
            collectionDescriptionBackground.setBackgroundColor(categoryLighterColor)

            collectionEmojiList.apply {
                adapter = CollectionAdapter(
                    collectionEmojis = args.categoryEmojis.toList(),
                    collectionColor = categoryDetails.color,
                    collectionLighterColor = categoryLighterColor,
                    onUnlockedEmojiClicked = ::showEmojiDetailsDialog
                )
                setHasFixedSize(true)

                val gridSpan = resources.getInteger(R.integer.emoji_collection_grid_span)

                layoutManager = GridLayoutManager(
                    context, gridSpan, RecyclerView.VERTICAL, false
                ).also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == CollectionAdapter.COLLECTION_PROGRESS_VIEW_POSITION) gridSpan else 1
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun showEmojiDetailsDialog(emoji: Emoji) {
        val emojiDetailsBinding = DialogEmojiDetailsBinding.inflate(layoutInflater)

        emojiDetailsBinding.apply {
            val emojiName = emoji.getName(resources)

            emojiDetailsName.text = emojiName
            emojiDetailsName.setTextColor(categoryDetails.color)
            emojiDetailsEmojiText.text = emoji.emoji
            emojiDetailsButtonBackground.setBackgroundColor(categoryLighterColor)
            emojiDetailsButtonCopy.setTextColor(categoryDetails.color)
            emojiDetailsButtonCopy.iconTint = ColorStateList.valueOf(categoryDetails.color)

            emojiDetailsButtonCopy.setOnClickListener {
                (it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
                    setPrimaryClip(ClipData.newPlainText(
                        "$Emoji \"${emojiName}\"", emoji.emoji
                    ))
                }

                // In Android 13 and higher, a standard visual confirmation is displayed when content
                // is added to the clipboard, so a Toast is unnecessary
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    Toast.makeText(
                        context, resources.getText(R.string.emoji_details_copy_toast_msg), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        Dialog(requireContext()).apply {
            setContentView(emojiDetailsBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }
}