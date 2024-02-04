package enzzom.hexemoji.data.repositories

import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.source.ChallengesDAO
import enzzom.hexemoji.models.EmojiCategory
import javax.inject.Inject

class ChallengesRepository @Inject constructor(
    private val challengesDAO: ChallengesDAO
) {

    /**
     * Replaces the challenges of a given category with new ones.
     * @return The list of challenges after the replacement
     */
    suspend fun replaceChallenges(
        category: EmojiCategory, oldChallenges: List<Challenge>, newChallenges: List<Challenge>?
    ): List<Challenge> {
        return challengesDAO.replaceChallenges(category, oldChallenges, newChallenges)
    }

    suspend fun getAllChallengesByCategory(): Map<EmojiCategory, List<Challenge>> {
        return challengesDAO.getAllGeneralChallengesByCategory()
    }
}