package enzzom.hexemoji.data

import androidx.room.Database
import androidx.room.RoomDatabase
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.data.entities.GeneralChallenge
import enzzom.hexemoji.data.entities.TimedChallenge
import enzzom.hexemoji.data.source.ChallengesDAO
import enzzom.hexemoji.data.source.EmojiDAO

@Database(entities = [
    Emoji::class, GeneralChallenge::class, TimedChallenge::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emojiDAO(): EmojiDAO

    abstract fun challengesDAO(): ChallengesDAO
}