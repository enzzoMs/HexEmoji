package enzzom.hexemoji.ui.fragments.play

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentEmojisSelectionBinding
import enzzom.hexemoji.models.EmojiCategoryDetails
import enzzom.hexemoji.ui.fragments.main.MainFragment
import enzzom.hexemoji.ui.fragments.play.adapters.EmojiCategoryAdapter
import enzzom.hexemoji.ui.fragments.play.model.PlayViewModel

class EmojisSelectionFragment : Fragment() {

    private val playViewModel: PlayViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEmojisSelectionBinding.inflate(inflater, container, false)

        (parentFragment?.parentFragment as MainFragment).apply {
            setToolbarTitle(playViewModel.getGameModeTitle(resources))
            showBackArrow(true)
            showNavigationViews(false)
        }

        val onCheckBoxClicked = { checked: Boolean ->
            if (checked) {
                playViewModel.selectAllCategories()
            } else {
                playViewModel.clearCategoriesSelection()
            }

            binding.emojiCategoriesList.adapter?.notifyDataSetChanged()
        }

        binding.allEmojisCheckBox?.apply {
            playViewModel.hasSelectedAllCategories.observe(viewLifecycleOwner) { isChecked = it }

            setOnClickListener{ onCheckBoxClicked(isChecked) }
        }

        binding.apply {
            emojiCategoriesList.apply {
                // Removing the recycler view animations (mainly to prevent blink after 'notifyItemChanged')
                itemAnimator = null
                setHasFixedSize(true)

                val useHeaderViews = layoutManager is GridLayoutManager

                adapter = EmojiCategoryAdapter(
                    emojiCategoryDetails = EmojiCategoryDetails.getAll(resources),
                    onCategoryClicked = {
                        playViewModel.toggleCategorySelection(it)

                        if (useHeaderViews) {
                            adapter?.notifyItemChanged(EmojiCategoryAdapter.CHECKBOX_VIEW_POSITION)
                        }
                    },
                    isCategorySelected = { playViewModel.isCategorySelected(it) },
                    useHeaderViews = useHeaderViews,
                    pageDescription = resources.getString(R.string.page_description_emojis_selection),
                    checkboxName = resources.getString(R.string.all_emojis_checkbox),
                    isCheckboxChecked = { playViewModel.hasSelectedAllCategories.value!! },
                    onCheckboxClicked = { onCheckBoxClicked(it) }
                )

                if (layoutManager is GridLayoutManager) {
                    (layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

                        override fun getSpanSize(position: Int): Int {
                            return if (
                                position == EmojiCategoryAdapter.PAGE_DESCRIPTION_VIEW_POSITION ||
                                position == EmojiCategoryAdapter.CHECKBOX_VIEW_POSITION
                            ) {
                                resources.getInteger(R.integer.emoji_categories_grid_span)
                            } else 1
                        }
                    }
                }
            }

            buttonContinue.setOnClickListener { navigateToBoardSelection() }

            playViewModel.hasSelectedAnyCategory.observe(viewLifecycleOwner) {
                binding.buttonContinue.isEnabled = it
            }
        }

        // Manually configuring the behavior of the back button due to an unknown bug that
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

        return binding.root
    }

    private fun navigateToBoardSelection() {
        playViewModel.clearBoardSizeSelection()
        findNavController().navigate(R.id.action_emojis_selection_to_board_selection)
    }
}