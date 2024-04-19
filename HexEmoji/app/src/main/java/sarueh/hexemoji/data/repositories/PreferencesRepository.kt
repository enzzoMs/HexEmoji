package sarueh.hexemoji.data.repositories

import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun getBoolean(key: String, defaultValue: Boolean) = sharedPreferences.getBoolean(key, defaultValue)

    fun getString(key: String, defaultValue: String) = sharedPreferences.getString(key, defaultValue)

    fun getInt(key: String, defaultValue: Int) = sharedPreferences.getInt(key, defaultValue)

    fun putBoolean(key: String, value: Boolean) {
        with (sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun putString(key: String, value: String) {
        with (sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun putInt(key: String, value: Int) {
        with (sharedPreferences.edit()) {
            putInt(key, value)
            apply()
        }
    }

    companion object {
        const val PREFERENCE_KEY_SHOW_BOARD_TUTORIAL = "preference_show_board_tutorial"
        const val PREFERENCE_KEY_NEXT_DAILY_EMOJI = "preference_next_daily_emoji"
        const val PREFERENCE_KEY_PREVIOUS_DAILY_EMOJI_DAY = "preference_previous_daily_emoji_day"
        const val PREFERENCE_KEY_GRANTED_PURCHASED_PRODUCTS = "preference_granted_purchased_products"
    }
}