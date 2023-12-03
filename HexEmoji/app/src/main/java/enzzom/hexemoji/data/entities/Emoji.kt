package enzzom.hexemoji.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emojis")
data class Emoji(
    @PrimaryKey val unicode: String,
    val name: String,
    val category: String,
    val unlocked: Boolean = false
)
