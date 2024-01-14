package enzzom.hexemoji.data.entities

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import enzzom.hexemoji.R
import enzzom.hexemoji.models.EmojiCategory
import enzzom.hexemoji.utils.StringUtils

@Entity(tableName = "emojis")
data class Emoji(
    @PrimaryKey val unicode: String,
    val identifier: String,
    val category: EmojiCategory,
    val unlocked: Boolean = false
) : Parcelable {

    @Ignore
    val emoji = StringUtils.unescapeString(unicode)

    /**
     * Returns the localized name of the emoji from Resources. Each emoji is stored in the database with a
     * 'identifier' field. This method uses the 'identifier' to fetch the name of the emoji from
     * string resources, expecting it to be defined as: 'emoji_name_$identifier'.
     */
    @SuppressLint("DiscouragedApi")
    fun getName(resources: Resources): String {
        return resources.run {
            getString(
                getIdentifier(
                    getString(R.string.emoji_name_identifier_prefix) + "_$identifier",
                    "string", "enzzom.hexemoji"
                )
            )
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        EmojiCategory.valueOf(parcel.readString()!!),
        parcel.readInt() == 1
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(unicode)
            writeString(identifier)
            writeString(category.name)
            writeInt(if (unlocked) 1 else 0)
        }
    }

    companion object CREATOR : Parcelable.Creator<Emoji> {
        override fun createFromParcel(parcel: Parcel): Emoji {
            return Emoji(parcel)
        }

        override fun newArray(size: Int): Array<Emoji?> {
            return arrayOfNulls(size)
        }
    }

}
