package enzzom.hexemoji.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.models.EmojiCategory

@Dao
interface ChallengesDAO {

    @Query("UPDATE general_challenges SET completed_games = completed_games + 1 WHERE id IN (:challengesId)")
    suspend fun incrementChallengesCompletion(challengesId: List<Long>)

    @Query("UPDATE general_challenges SET completed_games = 0 WHERE id IN (:challengesId)")
    suspend fun resetChallengesCompletion(challengesId: List<Long>)

    @Query("SELECT * FROM general_challenges")
    suspend fun getGeneralChallenges(): List<GeneralChallenge>

    @Query("SELECT * FROM general_challenges WHERE category = :category")
    suspend fun getGeneralChallenges(category: EmojiCategory): List<GeneralChallenge>

    @MapInfo(keyColumn = "category")
    @Query("SELECT * FROM general_challenges")
    suspend fun getAllGeneralChallengesByCategory(): Map<EmojiCategory, List<GeneralChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<GeneralChallenge>)

    @Delete
    suspend fun deleteChallenges(challenges: List<GeneralChallenge>)

    @Transaction
    suspend fun replaceChallenges(
        category: EmojiCategory, oldChallenges: List<Challenge>, newChallenges: List<Challenge>?
    ): List<Challenge> {
        // TODO: fix this cast
        deleteChallenges(oldChallenges as List<GeneralChallenge>)

        if (newChallenges != null) {
            insertChallenges(newChallenges as List<GeneralChallenge>)
        }

        return getGeneralChallenges(category)
    }
}