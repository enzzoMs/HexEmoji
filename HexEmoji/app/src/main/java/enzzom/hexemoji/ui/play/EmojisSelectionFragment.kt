package enzzom.hexemoji.ui.play

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentEmojisSelectionBinding
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.models.EmojiCategoryCard
import enzzom.hexemoji.ui.main.MainFragment
import enzzom.hexemoji.ui.play.adapters.EmojiCategoryAdapter
import enzzom.hexemoji.ui.play.models.PlayViewModel

class EmojisSelectionFragment : Fragment() {

    private val playViewModel: PlayViewModel by activityViewModels()
    private var binding: FragmentEmojisSelectionBinding? = null
    private lateinit var emojiCategoryCards: List<EmojiCategoryCard>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmojisSelectionBinding.inflate(inflater, container, false)

        emojiCategoryCards = getEmojiCategoryCards()

        val mainFragment = parentFragment?.parentFragment as MainFragment
        mainFragment.setToolbarTitle(playViewModel.getGameModeTitle())

        playViewModel.hasSelectedAnyCategory.observe(viewLifecycleOwner) {
            binding!!.buttonContinue.isEnabled = it
        }

        playViewModel.hasSelectedAllCategories.observe(viewLifecycleOwner) {
            binding!!.allEmojisCheckBox.isChecked = it
        }

        binding!!.apply {
            // Removing the recycler view animations (mainly to prevent blink after 'notifyItemChanged')
            emojiCategoriesList.itemAnimator = null

            allEmojisCheckBox.setOnClickListener {
                if (allEmojisCheckBox.isChecked) {
                    playViewModel.selectAllEmojiCategories()
                } else {
                    playViewModel.clearEmojiCategoriesSelection()
                }

                emojiCategoriesList.adapter?.notifyItemRangeChanged(0, emojiCategoryCards.size)
            }
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.emojiCategoriesList?.apply {
            setHasFixedSize(true)
            adapter = EmojiCategoryAdapter(
                emojiCategoryCards = emojiCategoryCards,
                onEmojiCategoryClicked = { playViewModel.toggleEmojiCategorySelection(it) },
                isCategorySelected = { playViewModel.isEmojiCategorySelected(it) }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

    private fun getEmojiCategoryCards(): List<EmojiCategoryCard> {
        val emojiCategoryCards = mutableListOf<EmojiCategoryCard>()

        val categoryTitles = resources.getStringArray(R.array.emoji_category_titles)
        val categoryTitleColors = resources.getIntArray(R.array.emoji_category_title_color)
        val categoryDescriptions = resources.getStringArray(R.array.emoji_category_descriptions)
        val categoryImage = resources.obtainTypedArray(R.array.emoji_category_images)

        EmojiCategory.values().forEachIndexed { index, emojiCategory ->
            emojiCategoryCards.add(
                EmojiCategoryCard(
                    emojiCategory,
                    categoryTitles[index],
                    categoryTitleColors[index],
                    categoryDescriptions[index],
                    categoryImage.getResourceId(index, R.drawable.emoji_category_example_people_emotions)
                )
            )
        }

        categoryImage.recycle()

        return emojiCategoryCards.toList()
    }
}