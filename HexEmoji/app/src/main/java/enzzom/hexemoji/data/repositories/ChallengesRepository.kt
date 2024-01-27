package enzzom.hexemoji.data.repositories

import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.source.ChallengesDAO
import enzzom.hexemoji.models.EmojiCategory
import javax.inject.Inject

class ChallengesRepository @Inject constructor(
    private val challengesDAO: ChallengesDAO
) {

    suspend fun insertChallenges(challenges: List<Challenge>) {
        // TODO: change this cast
        challengesDAO.insertChallenges(challenges as List<GeneralChallenge>)
    }

    suspend fun deleteChallenges(challenges: List<Challenge>) {
        challengesDAO.deleteChallenges(challenges as List<GeneralChallenge>)
    }

    suspend fun getAllChallengesByCategory(): Map<EmojiCategory, List<Challenge>> {
        return challengesDAO.getAllGeneralChallengesByCategory()
    }

    suspend fun getAllChallengerForCategory(category: EmojiCategory): List<GeneralChallenge> {
        return challengesDAO.getGeneralChallengesForCategory(category)
    }
}