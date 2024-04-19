package sarueh.hexemoji.data.repositories

import sarueh.hexemoji.data.entities.Challenge
import sarueh.hexemoji.data.source.ChallengesDAO
import sarueh.hexemoji.models.EmojiCategory
import javax.inject.Inject

class ChallengesRepository @Inject constructor(
    private val challengesDAO: ChallengesDAO
) {

    suspend fun getAllChallenges(): List<Challenge> {
        return challengesDAO.getAllChallenges()
    }

    suspend fun getAllChallengesByCategory(): Map<EmojiCategory, List<Challenge>> {
        return challengesDAO.getAllChallengesByCategory()
    }

    suspend fun incrementChallengesCompletion(challenges: List<Challenge>) {
        challengesDAO.incrementChallengesCompletion(challenges)
    }

    suspend fun resetChallengesCompletion(challenges: List<Challenge>) {
        challengesDAO.resetChallengesCompletion(challenges)
    }

    /**
     * Replaces the challenges of a given category with new ones.
     * @return The complete list of challenges after the replacement
     */
    suspend fun replaceChallenges(
        category: EmojiCategory, oldChallenges: List<Challenge>, newChallenges: List<Challenge>?
    ): List<Challenge> {
        return challengesDAO.replaceChallenges(category, oldChallenges, newChallenges)
    }
}