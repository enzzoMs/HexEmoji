package ems.hexemoji

import android.app.Application
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HexEmojiApp : Application() {

    override fun onCreate() {
        super.onCreate()

        BundledEmojiCompatConfig(this).let {
            it.setReplaceAll(true)
            EmojiCompat.init(it)
        }
    }
}