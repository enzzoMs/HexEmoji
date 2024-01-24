package enzzom.hexemoji.data.repositories

import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.source.ChallengesDAO
import enzzom.hexemoji.models.EmojiCategory
import javax.inject.Inject

class ChallengesRepository @Inject constructor(
    private val challengesDAO: ChallengesDAO
) {

    suspend fun getAllChallengesByCategory(): Map<EmojiCategory, List<Challenge>> {
        return challengesDAO.getAllGeneralChallengesByCategory()
    }
}