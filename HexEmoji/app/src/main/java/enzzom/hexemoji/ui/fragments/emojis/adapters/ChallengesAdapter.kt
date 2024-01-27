package enzzom.hexemoji.ui.fragments.emojis.adapters

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.databinding.ItemCardChallengeBinding

class ChallengesAdapter(
    private var challenges: List<Challenge>? = null,
    private var challengesCategoryColor: Int = 0
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
    fun replaceChallenges(newChallenges: List<Challenge>, categoryColor: Int) {
        challenges = newChallenges
        challengesCategoryColor = categoryColor
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

        fun bind(challenge: Challenge) {
            binding.apply {
                challengeEmojiRewardBackground.setBackgroundColor(challengesCategoryColor)
                challengeEmojiReward.text = challenge.rewardEmoji
                challengeDescription.text = when (challenge) {
                    is GeneralChallenge -> getChallengeDescription(challenge, root.resources)
                    else -> ""
                }
                challengeProgress.setTextColor(challengesCategoryColor)
                challengeProgress.text = root.resources.getString(
                    R.string.progress_ratio_template, challenge.completedGames, challenge.totalGames
                )
                challengeCompleted.isChecked = challenge.completed
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

            val hintsConstraint = if (challenge.hintsAllowed) {
                res.getString(R.string.challenge_template_hints_constraint)
            } else ""

            return res.getString(
                R.string.challenge_template_general,
                challenge.totalGames,
                if (challenge.consecutiveGames) res.getString(
                    R.string.challenge_template_consecutive_games_constraint
                ) else "",
                challenge.gameMode.getTitle(res),
                boardConstraints,
                categoryConstraints,
                hintsConstraint
            )
        }
    }
}