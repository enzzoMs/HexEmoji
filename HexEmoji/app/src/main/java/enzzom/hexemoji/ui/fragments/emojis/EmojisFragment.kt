package enzzom.hexemoji.ui.fragments.emojis

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.DialogRefreshChallengesBinding
import enzzom.hexemoji.databinding.FragmentEmojisBinding
import enzzom.hexemoji.models.EmojiCategoryDetails
import enzzom.hexemoji.ui.fragments.emojis.adapters.ChallengesAdapter
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

        val allCategoryDetails = EmojiCategoryDetails.getAll(resources)

        binding.apply {
            collectionDetailsList.adapter = CollectionDetailsAdapter(
                categoryDetails = allCategoryDetails,
                getEmojiCountForCategory = emojisViewModel::getEmojiCountForCategory,
                getUnlockedCountForCategory = emojisViewModel::getUnlockedCountForCategory,
                onCollectionClicked = { category -> emojisViewModel.getCategoryEmojis(category)?.let {
                    (parentFragment?.parentFragment as MainFragment).navigateToCollectionScreen(category, it)
                }}
            )

            val challengesListAdapter = ChallengesAdapter().also {
                challengesList.adapter = it
            }

            TabLayoutMediator(collectionTabs, collectionDetailsList) { tab, position ->
                tab.setIcon(emojiCategoryIconsId[position])
            }.attach()

            collectionTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null && challengesLoading.visibility != View.VISIBLE) {
                        val selectedCategory = allCategoryDetails[tab.position]

                        val categoryChallenges = emojisViewModel.getChallengesForCategory(
                            selectedCategory.category
                        )

                        if (categoryChallenges != null) {
                            challengesListAdapter.replaceChallenges(categoryChallenges, selectedCategory.color)
                            noChallengesDescription.visibility = View.GONE
                            noChallengesIcon.visibility = View.GONE
                            refreshChallenges.visibility = View.VISIBLE
                        } else {
                            challengesListAdapter.clearChallenges()
                            noChallengesDescription.visibility = View.VISIBLE
                            noChallengesIcon.visibility = View.VISIBLE
                            refreshChallenges.visibility = View.INVISIBLE
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            refreshChallenges.setOnClickListener {
                val selectedTabPosition = collectionTabs.selectedTabPosition

                if (selectedTabPosition != -1) {
                    showRefreshChallengesDialog(onConfirmClicked = {
                        val selectedCategory = allCategoryDetails[selectedTabPosition]

                        if (emojisViewModel.refreshChallenges(selectedCategory.category)) {
                            emojisViewModel.getChallengesForCategory(selectedCategory.category)?.let {
                                challengesListAdapter.replaceChallenges(it, selectedCategory.color)
                            }
                        } else {
                            Snackbar.make(it, R.string.refresh_collection_error_msg, Snackbar.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            emojisViewModel.collectionsLoadingFinished.observe(viewLifecycleOwner) { finished ->
                if (finished) {
                    challengesLoading.visibility = View.GONE
                    collectionDetailsList.adapter?.notifyDataSetChanged()
                } else {
                    challengesLoading.visibility = View.VISIBLE
                    challengesListAdapter.clearChallenges()
                }
            }
        }

        return binding.root
    }

    private fun showRefreshChallengesDialog(onConfirmClicked: () -> Unit) {
        val dialog = Dialog(requireContext())

        val refreshChallengesBinding = DialogRefreshChallengesBinding.inflate(layoutInflater).apply {
            refreshChallengesButtonCancel.setOnClickListener {
                dialog.dismiss()
            }
            refreshChallengesButtonConfirm.setOnClickListener {
                dialog.dismiss()
                onConfirmClicked()
            }
        }

        dialog.apply {
            setContentView(refreshChallengesBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }
}