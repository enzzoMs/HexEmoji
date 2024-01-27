package enzzom.hexemoji.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.models.EmojiCategory

@Dao
interface ChallengesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<GeneralChallenge>)

    @Delete
    suspend fun deleteChallenges(challenges: List<GeneralChallenge>)

    @MapInfo(keyColumn = "category")
    @Query("SELECT * FROM general_challenges")
    suspend fun getAllGeneralChallengesByCategory(): Map<EmojiCategory, List<GeneralChallenge>>

    @Query("SELECT * FROM general_challenges WHERE category = :category")
    suspend fun getGeneralChallengesForCategory(category: EmojiCategory): List<GeneralChallenge>
}