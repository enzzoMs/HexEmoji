package enzzom.hexemoji.data

import androidx.room.Database
import androidx.room.RoomDatabase
import enzzom.hexemoji.data.entities.Emoji
import enzzom.hexemoji.data.source.EmojiDAO

@Database(entities = [Emoji::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emojiDAO(): EmojiDAO
}