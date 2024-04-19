package sarueh.hexemoji.models

import android.content.res.Resources
import sarueh.hexemoji.R

enum class GameMode {
    ZEN,
    AGAINST_THE_CLOCK,
    LIMITED_MOVES,
    SEQUENCE,
    SHUFFLED,
    CHAOS;

    fun getTitle(res: Resources): String {
        return when(this) {
            ZEN -> res.getString(R.string.game_mode_title_zen)
            AGAINST_THE_CLOCK -> res.getString(R.string.game_mode_title_against_clock)
            LIMITED_MOVES -> res.getString(R.string.game_mode_title_limited_moves)
            SEQUENCE -> res.getString(R.string.game_mode_title_sequence)
            SHUFFLED -> res.getString(R.string.game_mode_title_shuffled)
            CHAOS -> res.getString(R.string.game_mode_title_chaos)
        }
    }
}