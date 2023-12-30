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
import enzzom.hexemoji.ui.fragments.emojis.adapters.EmojiCollectionInfoAdapter
import enzzom.hexemoji.ui.fragments.emojis.model.EmojisViewModel

@AndroidEntryPoint
class EmojisFragment : Fragment() {

    private val emojisViewModel: EmojisViewModel by viewModels()
    private var binding: FragmentEmojisBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmojisBinding.inflate(inflater, container, false)

        return binding?.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emojiCategoryDetails = EmojiCategoryDetails.getAll(resources)

        val emojiCategoryIconsId = mutableListOf<Int>()

        resources.obtainTypedArray(R.array.emoji_category_icons).let {
            for (i in 0..it.length()) {
                emojiCategoryIconsId.add(it.getResourceId(i, R.drawable.ic_smiley_face_filled))
            }
            it.recycle()
        }

        binding?.apply {
            emojiCollectionInfoList.adapter = EmojiCollectionInfoAdapter(
                emojiCategoryDetails = emojiCategoryDetails,
                getEmojiCountForCategory = emojisViewModel::getEmojiCountForCategory,
                getUnlockedCountForCategory = emojisViewModel::getUnlockedCountForCategory
            )

            TabLayoutMediator(emojiCollectionTabs, emojiCollectionInfoList) { tab, position ->
                tab.setIcon(emojiCategoryIconsId[position])
            }.attach()

            emojisViewModel.loadingCategoriesInfo.observe(viewLifecycleOwner) { loading ->
                if (!loading) {
                    emojiCollectionInfoList.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}