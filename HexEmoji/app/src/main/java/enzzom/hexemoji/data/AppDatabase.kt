package enzzom.hexemoji.data

import androidx.room.Database
import androidx.room.RoomDatabase
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.data.entities.GameStatistic
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.entities.LimitedMovesChallenge
import enzzom.hexemoji.data.entities.TimedChallenge
import enzzom.hexemoji.data.source.ChallengesDAO
import enzzom.hexemoji.data.source.EmojiDAO
import enzzom.hexemoji.data.source.StatisticsDAO

@Database(entities = [
    Emoji::class, GameStatistic::class,
    GeneralChallenge::class,
    TimedChallenge::class,
    LimitedMovesChallenge::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emojiDAO(): EmojiDAO

    abstract fun statisticsDAO(): StatisticsDAO

    abstract fun challengesDAO(): ChallengesDAO
}