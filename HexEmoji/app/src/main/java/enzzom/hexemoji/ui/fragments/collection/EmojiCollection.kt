package enzzom.hexemoji.ui.fragments.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentEmojiCollectionBinding
import enzzom.hexemoji.models.EmojiCategoryDetails
import enzzom.hexemoji.ui.fragments.collection.adapters.CollectionAdapter

class EmojiCollection : Fragment() {

    private val args: EmojiCollectionArgs by navArgs()
    private var binding: FragmentEmojiCollectionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEmojiCollectionBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryDetails = EmojiCategoryDetails.getAll(resources).find { it.category == args.category }!!

        activity?.window?.statusBarColor = categoryDetails.color

        binding?.apply {
            val categoryColorLighter = ColorUtils.blendARGB(
                categoryDetails.color,
                ContextCompat.getColor(requireContext(), R.color.surface_color),
                resources.getInteger(R.integer.emoji_collection_category_color_blend_percentage) / 100f
            )

            collectionToolbar.apply {
                setNavigationOnClickListener { findNavController().popBackStack() }

                title = categoryDetails.title
                setBackgroundColor(categoryColorLighter)
                setTitleTextColor(categoryDetails.color)
                setNavigationIconTint(categoryDetails.color)
            }

            collectionDescription.text = categoryDetails.description
            collectionDescriptionBackground.setBackgroundColor(categoryColorLighter)

            collectionEmojiList.apply {
                adapter = CollectionAdapter(
                    args.categoryEmojis.toList(), categoryDetails.color, categoryColorLighter
                )
                setHasFixedSize(true)

                val gridSpan = resources.getInteger(R.integer.emoji_collection_grid_span)

                layoutManager = GridLayoutManager(
                    context, gridSpan, RecyclerView.VERTICAL, false
                ).also {
                    it.spanSizeLookup = object : SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == CollectionAdapter.COLLECTION_PROGRESS_VIEW_POSITION) gridSpan else 1
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        binding = null

        super.onDestroy()
    }
}