package enzzom.hexemoji.models

import android.content.res.Resources
import enzzom.hexemoji.R

enum class GameMode {
    ZEN,
    AGAINST_THE_CLOCK,
    LIMITED_MOVES,
    SEQUENCE,
    SHUFFLED,
    FLOOD,
    CHAOS;

    companion object {
        fun getGameModeTitle(gameMode: GameMode, resources: Resources): String {
            return when(gameMode) {
                ZEN -> resources.getString(R.string.game_mode_title_zen)
                AGAINST_THE_CLOCK -> resources.getString(R.string.game_mode_title_against_clock)
                LIMITED_MOVES -> resources.getString(R.string.game_mode_title_limited_moves)
                SEQUENCE -> resources.getString(R.string.game_mode_title_sequence)
                SHUFFLED -> resources.getString(R.string.game_mode_title_shuffled)
                FLOOD -> resources.getString(R.string.game_mode_title_flood)
                CHAOS -> resources.getString(R.string.game_mode_title_chaos)
            }
        }
    }
}