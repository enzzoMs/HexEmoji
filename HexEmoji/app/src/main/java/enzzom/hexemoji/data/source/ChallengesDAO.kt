package enzzom.hexemoji.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import enzzom.hexemoji.data.entities.Challenge
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.entities.LimitedMovesChallenge
import enzzom.hexemoji.data.entities.TimedChallenge
import enzzom.hexemoji.models.EmojiCategory

@Dao
interface ChallengesDAO {

    @Transaction
    suspend fun insertChallenges(challenges: List<Challenge>) {
        insertGeneralChallenges(challenges.filterIsInstance<GeneralChallenge>())
        insertTimedChallenges(challenges.filterIsInstance<TimedChallenge>())
        insertLimitedMovesChallenges(challenges.filterIsInstance<LimitedMovesChallenge>())
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneralChallenges(challenges: List<GeneralChallenge>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimedChallenges(challenges: List<TimedChallenge>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLimitedMovesChallenges(challenges: List<LimitedMovesChallenge>)

    @Transaction
    suspend fun deleteChallenges(challenges: List<Challenge>) {
        deleteGeneralChallenges(challenges.filterIsInstance<GeneralChallenge>())
        deleteTimedChallenges(challenges.filterIsInstance<TimedChallenge>())
        deleteLimitedMovesChallenges(challenges.filterIsInstance<LimitedMovesChallenge>())
    }

    @Delete
    suspend fun deleteGeneralChallenges(challenges: List<GeneralChallenge>)

    @Delete
    suspend fun deleteTimedChallenges(challenges: List<TimedChallenge>)

    @Delete
    suspend fun deleteLimitedMovesChallenges(challenges: List<LimitedMovesChallenge>)

    @Transaction
    suspend fun getAllChallenges(): List<Challenge> {
        return getAllGeneralChallenges() + getAllTimedChallenges() + getAllLimitedMovesChallenges()
    }

    @Transaction
    suspend fun getAllChallenges(category: EmojiCategory): List<Challenge> {
        val allChallenges = getAllChallenges()

        return allChallenges.filter { it.category == category }
    }

    @Transaction
    suspend fun getAllChallengesByCategory(): Map<EmojiCategory, List<Challenge>> {
        val allChallenges = getAllChallenges()

        return allChallenges.groupBy { it.category }
    }

    @Query("SELECT * FROM general_challenges")
    suspend fun getAllGeneralChallenges(): List<GeneralChallenge>

    @Query("SELECT * FROM timed_challenges")
    suspend fun getAllTimedChallenges(): List<TimedChallenge>

    @Query("SELECT * FROM limited_moves_challenges")
    suspend fun getAllLimitedMovesChallenges(): List<LimitedMovesChallenge>

    @Transaction
    suspend fun incrementChallengesCompletion(challenges: List<Challenge>) {
        incrementGeneralChallengesCompletion(challenges.filterIsInstance<GeneralChallenge>().map { it.id })
        incrementTimedChallengesCompletion(challenges.filterIsInstance<TimedChallenge>().map { it.id })
        incrementLimitedMovesChallengesCompletion(challenges.filterIsInstance<LimitedMovesChallenge>().map { it.id })
    }

    @Query("UPDATE general_challenges SET completed_games = completed_games + 1 WHERE id IN (:challengesId)")
    suspend fun incrementGeneralChallengesCompletion(challengesId: List<Long>)

    @Query("UPDATE timed_challenges SET completed_games = completed_games + 1 WHERE id IN (:challengesId)")
    suspend fun incrementTimedChallengesCompletion(challengesId: List<Long>)

    @Query("UPDATE limited_moves_challenges SET completed_games = completed_games + 1 WHERE id IN (:challengesId)")
    suspend fun incrementLimitedMovesChallengesCompletion(challengesId: List<Long>)

    @Transaction
    suspend fun resetChallengesCompletion(challenges: List<Challenge>) {
        resetGeneralChallengesCompletion(challenges.filterIsInstance<GeneralChallenge>().map { it.id })
        resetTimedChallengesCompletion(challenges.filterIsInstance<TimedChallenge>().map { it.id })
        resetLimitedMovesChallengesCompletion(challenges.filterIsInstance<LimitedMovesChallenge>().map { it.id })
    }

    @Query("UPDATE general_challenges SET completed_games = 0 WHERE id IN (:challengesId)")
    suspend fun resetGeneralChallengesCompletion(challengesId: List<Long>)

    @Query("UPDATE timed_challenges SET completed_games = 0 WHERE id IN (:challengesId)")
    suspend fun resetTimedChallengesCompletion(challengesId: List<Long>)

    @Query("UPDATE limited_moves_challenges SET completed_games = 0 WHERE id IN (:challengesId)")
    suspend fun resetLimitedMovesChallengesCompletion(challengesId: List<Long>)

    @Transaction
    suspend fun replaceChallenges(
        category: EmojiCategory, oldChallenges: List<Challenge>, newChallenges: List<Challenge>?
    ): List<Challenge> {
        deleteChallenges(oldChallenges)

        if (newChallenges != null) {
            insertChallenges(newChallenges)
        }

        return getAllChallenges(category)
    }
}