package enzzom.hexemoji.ui.fragments.emojis

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentEmojisBinding
import enzzom.hexemoji.models.EmojiCategoryDetails
import enzzom.hexemoji.ui.fragments.emojis.adapters.CollectionDetailsAdapter
import enzzom.hexemoji.ui.fragments.emojis.model.EmojisViewModel
import enzzom.hexemoji.ui.fragments.main.MainFragment

@AndroidEntryPoint
class EmojisFragment : Fragment() {

    private val emojisViewModel: EmojisViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEmojisBinding.inflate(inflater, container, false)

        val emojiCategoryIconsId = mutableListOf<Int>()

        resources.obtainTypedArray(R.array.emoji_category_icons).let {
            for (i in 0..it.length()) {
                emojiCategoryIconsId.add(it.getResourceId(i, R.drawable.ic_smiley_face_filled))
            }
            it.recycle()
        }

        val emojiCategoryDetails = EmojiCategoryDetails.getAll(resources)

        binding.apply {
            emojiCollectionDetailsList.adapter = CollectionDetailsAdapter(
                emojiCategoryDetails = emojiCategoryDetails,
                getEmojiCountForCategory = emojisViewModel::getEmojiCountForCategory,
                getUnlockedCountForCategory = emojisViewModel::getUnlockedCountForCategory,
                onCollectionClicked = { category -> emojisViewModel.getCategoryEmojis(category)?.let {
                    (parentFragment?.parentFragment as MainFragment).navigateToCollectionScreen(category, it)
                }}
            )

            TabLayoutMediator(emojiCollectionTabs, emojiCollectionDetailsList) { tab, position ->
                tab.setIcon(emojiCategoryIconsId[position])
            }.attach()

            emojisViewModel.loadingCategoriesInfo.observe(viewLifecycleOwner) { loading ->
                if (!loading) {
                    emojiCollectionDetailsList.adapter?.notifyDataSetChanged()
                }
            }
        }

        return binding.root
    }
}