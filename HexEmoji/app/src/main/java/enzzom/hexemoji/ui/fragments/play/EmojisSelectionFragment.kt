package enzzom.hexemoji.ui.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentEmojisSelectionBinding
import enzzom.hexemoji.models.EmojiCategoryCard
import enzzom.hexemoji.ui.fragments.main.MainFragment
import enzzom.hexemoji.ui.fragments.play.adapters.EmojiCategoryAdapter
import enzzom.hexemoji.ui.fragments.play.models.PlayViewModel

class EmojisSelectionFragment : Fragment() {

    private val playViewModel: PlayViewModel by activityViewModels()
    private var binding: FragmentEmojisSelectionBinding? = null
    private lateinit var emojiCategoryCards: List<EmojiCategoryCard>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmojisSelectionBinding.inflate(inflater, container, false)

        val mainFragment = parentFragment?.parentFragment as MainFragment
        mainFragment.apply {
            setToolbarTitle(playViewModel.getGameModeTitle(resources))
            showBackArrow(true)
            showNavigationViews(false)
        }

        playViewModel.hasSelectedAnyCategory.observe(viewLifecycleOwner) {
            binding?.buttonContinue?.isEnabled = it
        }

        playViewModel.hasSelectedAllCategories.observe(viewLifecycleOwner) {
            binding?.allEmojisCheckBox?.isChecked = it
        }

        emojiCategoryCards = playViewModel.getEmojiCategoryCards(resources)

        binding?.apply {
            // Removing the recycler view animations (mainly to prevent blink after 'notifyItemChanged')
            emojiCategoriesList.itemAnimator = null

            buttonContinue.setOnClickListener { navigateToBoardSelection() }

            allEmojisCheckBox.setOnClickListener {
                if (allEmojisCheckBox.isChecked) {
                    playViewModel.selectAllEmojiCategories()
                } else {
                    playViewModel.clearEmojiCategoriesSelection()
                }

                emojiCategoriesList.adapter?.notifyItemRangeChanged(0, emojiCategoryCards.size)
            }
        }

        // Manually configuring the behavior of the back button due to an error that
        // caused a synchronization issue between what was shown on the screen and the actual
        // current destination tracked by the NavController.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_emojis_selection_to_game_modes)
                }
            }
        )

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

    private fun navigateToBoardSelection() {
        playViewModel.clearBoardSizeSelection()
        findNavController().navigate(R.id.action_emojis_selection_to_board_selection)
    }
}