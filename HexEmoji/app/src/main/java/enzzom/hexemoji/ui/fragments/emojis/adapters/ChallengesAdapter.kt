package enzzom.hexemoji.ui.fragments.emojis.adapters

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.entities.TimedChallenge
import enzzom.hexemoji.databinding.ItemCardChallengeBinding

private const val PROGRESS_ANIM_DURATION_MS = 350L
private const val PROGRESS_ANIM_REPEAT_COUNT = 3

class ChallengesAdapter(
    private var challenges: List<Challenge>? = null,
    private var categoryColor: Int = 0,
    private val onCompletedChallengeClicked: (Challenge) -> Unit
) : RecyclerView.Adapter<ChallengesAdapter.ChallengeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ChallengeHolder(
            ItemCardChallengeBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChallengeHolder, position: Int) {
        challenges?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int = challenges?.size ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun replaceChallenges(newChallenges: List<Challenge>, newCategoryColor: Int) {
        challenges = newChallenges
        categoryColor = newCategoryColor
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearChallenges() {
        challenges = listOf()
        notifyDataSetChanged()
    }

    inner class ChallengeHolder(
        private val binding: ItemCardChallengeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var challenge: Challenge
        private var progressAnimationFinished = true

        init {
            binding.root.setOnClickListener {
                if (challenge.completed) {
                    onCompletedChallengeClicked(challenge)
                } else if (progressAnimationFinished) {
                    val warmingColor = ContextCompat.getColor(binding.root.context, R.color.warming_color)
                    val previousLabelColor = binding.challengeProgressLabel.currentTextColor

                    AnimatorSet().apply {
                        playTogether(
                            ObjectAnimator.ofFloat(binding.challengeProgress, "alpha", 1f, 0f).also {
                                it.repeatCount = PROGRESS_ANIM_REPEAT_COUNT
                            },
                            ObjectAnimator.ofFloat(binding.challengeProgressLabel, "alpha", 1f, 0f).also {
                                it.repeatCount = PROGRESS_ANIM_REPEAT_COUNT
                            },
                        )
                        duration = PROGRESS_ANIM_DURATION_MS
                        doOnStart {
                            binding.apply {
                                challengeProgress.setTextColor(warmingColor)
                                challengeProgressLabel.setTextColor(warmingColor)
                                progressAnimationFinished = false
                            }
                        }
                        doOnEnd {
                            binding.apply {
                                challengeProgress.setTextColor(categoryColor)
                                challengeProgressLabel.setTextColor(previousLabelColor)
                                challengeProgress.alpha = 1f
                                challengeProgressLabel.alpha = 1f
                                progressAnimationFinished = true
                            }
                        }
                    }.start()
                }
            }
        }

        fun bind(challenge: Challenge) {
            this.challenge = challenge

            binding.apply {
                challengeEmojiRewardBackground.setBackgroundColor(categoryColor)
                challengeEmojiReward.text = challenge.rewardEmoji
                challengeDescription.text = when (challenge) {
                    is GeneralChallenge -> getChallengeDescription(challenge, root.resources)
                    is TimedChallenge -> getChallengeDescription(challenge, root.resources)
                    else -> ""
                }
                challengeProgress.setTextColor(categoryColor)
                challengeProgress.text = root.resources.getString(
                    R.string.progress_ratio_template, challenge.completedGames, challenge.totalGames
                )
                challengeCompleted.buttonTintList = ColorStateList.valueOf(categoryColor)
                challengeCompleted.isChecked = challenge.completed

                challengeCard.setCardBackgroundColor(if (challenge.completed) {
                    ColorUtils.setAlphaComponent(categoryColor, root.resources.getInteger(
                        R.integer.challenge_completed_back_color_alpha
                    ))
                } else {
                    ContextCompat.getColor(root.context, R.color.surface_color)
                })
            }
        }
        private fun getChallengeDescription(challenge: GeneralChallenge, res: Resources): String {
            val boardConstraints = if (challenge.boardSize == null) "" else {
                res.getString(
                    R.string.challenge_template_board_constraint,
                    challenge.boardSize.getLabel()
                )
            }

            val categoryConstraints = if (challenge.constrainedToCategory) {
                res.getString(
                    R.string.challenge_template_category_constraint,
                    challenge.category.getTitle(res)
                )
            } else ""

            return res.getString(
                R.string.challenge_template_general,
                challenge.totalGames,
                if (challenge.consecutiveGames) res.getString(
                    R.string.challenge_template_consecutive_games_constraint
                ) else "",
                challenge.gameMode.getTitle(res),
                boardConstraints,
                categoryConstraints
            )
        }

        private fun getChallengeDescription(challenge: TimedChallenge, res: Resources): String {
            return res.getString(
                R.string.challenge_template_timed,
                challenge.totalGames, challenge.gameMode.getTitle(res), challenge.timeLimitInSeconds
            )
        }
    }
}