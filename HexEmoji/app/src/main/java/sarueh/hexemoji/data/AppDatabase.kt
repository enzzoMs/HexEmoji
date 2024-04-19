package sarueh.hexemoji.data

import androidx.room.Database
import androidx.room.RoomDatabase
import sarueh.hexemoji.data.entities.Emoji
import sarueh.hexemoji.data.entities.GameStatistic
import sarueh.hexemoji.data.entities.GeneralChallenge
import sarueh.hexemoji.data.entities.LimitedMovesChallenge
import sarueh.hexemoji.data.entities.TimedChallenge
import sarueh.hexemoji.data.source.ChallengesDAO
import sarueh.hexemoji.data.source.EmojiDAO
import sarueh.hexemoji.data.source.StatisticsDAO

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