package enzzom.hexemoji.data.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import enzzom.hexemoji.models.EmojiCategory

@Entity(tableName = "emojis")
data class Emoji(
    @PrimaryKey val unicode: String,
    val name: String,
    val category: EmojiCategory,
    val unlocked: Boolean = false
) : Parcelable {

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
            writeString(name)
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
