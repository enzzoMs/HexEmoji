package ems.hexemoji.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ems.hexemoji.data.entities.Emoji
import ems.hexemoji.data.entities.GameStatistic
import ems.hexemoji.data.entities.GeneralChallenge
import ems.hexemoji.data.entities.LimitedMovesChallenge
import ems.hexemoji.data.entities.TimedChallenge
import ems.hexemoji.data.source.ChallengesDAO
import ems.hexemoji.data.source.EmojiDAO
import ems.hexemoji.data.source.StatisticsDAO

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