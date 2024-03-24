package enzzom.hexemoji.ui.fragments.emojis

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.databinding.DialogChallengeCompletedBinding
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
    private var showingDialog: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (parentFragment?.parentFragment as MainFragment).setToolbarTitle(
            resources.getString(R.string.page_title_page_emojis)
        )

        val binding = FragmentEmojisBinding.inflate(inflater, container, false)

        val allCategoryDetails = EmojiCategoryDetails.getAll(resources)
        var selectedCategoryIndex = 0

        binding.apply {
            val challengesListAdapter = ChallengesAdapter(
                onCompletedChallengeClicked = {
                    if (!showingDialog) {
                        val selectedCategory = allCategoryDetails[selectedCategoryIndex]
                        showChallengeCompletedDialog(
                            challenge = it,
                            categoryColor = selectedCategory.color,
                            onDialogDismissed = {
                                emojisViewModel.completeChallenge(it)
                                collectionDetailsList.adapter?.notifyItemChanged(selectedCategoryIndex)
                                emojisViewModel.loadChallengesForCategory(
                                    allCategoryDetails[selectedCategoryIndex].category
                                )
                            }
                        )
                    }
                }
            ).also {
                challengesList.adapter = it
            }

            val syncCollectionDetailsWithTabs = (
                collectionDetailsList.layoutManager as LinearLayoutManager
            ).orientation == RecyclerView.HORIZONTAL

            val snapHelper = PagerSnapHelper().also {
                if (syncCollectionDetailsWithTabs) {
                    it.attachToRecyclerView(collectionDetailsList)
                }
            }

            collectionDetailsList.apply {
                // Removing recycler view animations (mainly to prevent blink after 'notifyItemChanged')
                itemAnimator = null
                adapter = CollectionDetailsAdapter(
                    categoryDetails = allCategoryDetails,
                    getEmojiCountForCategory = emojisViewModel::getEmojiCountForCategory,
                    getUnlockedCountForCategory = emojisViewModel::getUnlockedCountForCategory,
                    onCollectionClicked = { category ->
                        if (!showingDialog) {
                            emojisViewModel.getCategoryEmojis(category)?.let {
                                (parentFragment?.parentFragment as MainFragment).navigateToCollectionScreen(category, it)
                            }
                        }
                    }
                )
                if (syncCollectionDetailsWithTabs) {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            val snapPosition = snapHelper.findSnapView(layoutManager)?.let {
                                layoutManager!!.getPosition(it)
                            }

                            if (snapPosition != null && snapPosition != selectedCategoryIndex) {
                                selectedCategoryIndex = snapPosition
                                collectionTabs.selectTab(collectionTabs.getTabAt(selectedCategoryIndex))
                            }
                        }
                    })
                }
            }

            val categoryIcons = resources.obtainTypedArray(R.array.emoji_category_icons)

            for (position in 0 until collectionDetailsList.adapter!!.itemCount) {
                collectionTabs.newTab().let { tab ->
                    collectionTabs.addTab(tab)
                    tab.setIcon(categoryIcons.getResourceId(position, R.drawable.ic_smiley_face_filled))
                }
            }
            categoryIcons.recycle()

            collectionTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null) {
                        if (tab.position != selectedCategoryIndex) {
                            selectedCategoryIndex = tab.position

                            if (syncCollectionDetailsWithTabs) {
                                collectionDetailsList.scrollToPosition(selectedCategoryIndex)
                            }
                        }

                        emojisViewModel.loadChallengesForCategory(
                            allCategoryDetails[selectedCategoryIndex].category
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            refreshChallenges.setOnClickListener {
                if (!showingDialog) {
                    showRefreshChallengesDialog(onConfirmClicked = {
                        val selectedCategory =
                            allCategoryDetails[selectedCategoryIndex].category

                        if (emojisViewModel.refreshChallenges(selectedCategory)) {
                            emojisViewModel.loadChallengesForCategory(selectedCategory)
                        } else {
                            Snackbar.make(
                                it, R.string.refresh_collection_error_msg, Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }

            emojisViewModel.collectionsLoadingFinished.observe(viewLifecycleOwner) { finished ->
                if (finished) {
                    collectionDetailsList.adapter?.notifyDataSetChanged()
                    emojisViewModel.loadChallengesForCategory(allCategoryDetails[selectedCategoryIndex].category)
                }
            }

            emojisViewModel.challengesLoading.observe(viewLifecycleOwner) { loading ->
                if (loading) {
                    challengesListAdapter.clearChallenges()
                    challengesLoading.visibility = View.VISIBLE
                    noChallengesDescription.visibility = View.INVISIBLE
                    noChallengesIcon.visibility = View.INVISIBLE
                    refreshChallenges.visibility = View.INVISIBLE
                } else {
                    challengesLoading.visibility = View.INVISIBLE
                }
            }

            emojisViewModel.currentChallenges.observe(viewLifecycleOwner) { challenges ->
                if (emojisViewModel.challengesLoading.value != true) {
                    if (challenges == null) {
                        noChallengesDescription.visibility = View.VISIBLE
                        noChallengesIcon.visibility = View.VISIBLE
                        refreshChallenges.visibility = View.INVISIBLE

                    } else {
                        noChallengesDescription.visibility = View.INVISIBLE
                        noChallengesIcon.visibility = View.INVISIBLE
                        refreshChallenges.visibility = View.VISIBLE

                        challengesListAdapter.replaceChallenges(
                            challenges, allCategoryDetails[selectedCategoryIndex].color
                        )
                    }
                }
            }
        }

        return binding.root
    }

    private fun showRefreshChallengesDialog(onConfirmClicked: () -> Unit) {
        showingDialog = true

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
            setOnDismissListener { showingDialog = false }
            setContentView(refreshChallengesBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun showChallengeCompletedDialog(
        challenge: Challenge, categoryColor: Int, onDialogDismissed: () -> Unit
    ) {
        showingDialog = true

        val dialog = Dialog(requireContext())

        val challengeCompletedBinding = DialogChallengeCompletedBinding.inflate(layoutInflater).apply {
            challengeCompletedDescription.text = resources.getString(
                R.string.challenge_completed_description_template, challenge.category.getTitle(resources)
            )
            challengeRewardEmoji.text = challenge.rewardEmoji

            challengeRewardEmojiName.setTextColor(categoryColor)
            challengeCompletedTitleBackground.setBackgroundColor(categoryColor)
            challengeCompletedButtonConfirm?.backgroundTintList = ColorStateList.valueOf(categoryColor)

            challengeRewardEmojiName.text = emojisViewModel.getEmojiByUnicode(
                challenge.rewardEmojiUnicode, challenge.category
            )?.getName(resources) ?: ""

            challengeCompletedButtonConfirm?.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.apply {
            setOnDismissListener {
                onDialogDismissed()
                showingDialog = false
            }
            setContentView(challengeCompletedBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }
}