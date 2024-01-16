package enzzom.hexemoji.data.repositories

import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun getBoolean(key: String, defaultValue: Boolean) = sharedPreferences.getBoolean(key, defaultValue)

    fun putBoolean(key: String, value: Boolean) {
        with (sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    companion object {
        const val PREFERENCE_KEY_SHOW_BOARD_TUTORIAL = "preference_show_board_tutorial"
    }
}