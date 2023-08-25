package enzzom.hexemoji

import android.app.Application
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat

class HexEmojiApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val emojiConfiguration  = BundledEmojiCompatConfig(this)
        emojiConfiguration.setReplaceAll(true)
        EmojiCompat.init(emojiConfiguration)
    }
}