package enzzom.hexemoji.data.source

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.models.EmojiCategory

@Dao
interface ChallengesDAO {

    @MapInfo(keyColumn = "category")
    @Query("SELECT * FROM general_challenges")
    suspend fun getAllGeneralChallengesByCategory(): Map<EmojiCategory, List<GeneralChallenge>>
}