package sarueh.hexemoji.models

import android.content.res.Resources
import sarueh.hexemoji.R

enum class EmojiCategory {
    PEOPLE_AND_EMOTIONS,
    ANIMALS_AND_NATURE,
    FOOD_AND_DRINK,
    ACTIVITIES,
    TRAVEL_AND_PLACES,
    OBJECTS,
    SYMBOLS,
    FLAGS,
    NUMBERS_AND_LETTERS;

    fun getTitle(res: Resources): String {
        return when(this) {
            PEOPLE_AND_EMOTIONS -> res.getString(R.string.emoji_category_title_people_emotions)
            ANIMALS_AND_NATURE -> res.getString(R.string.emoji_category_title_nature)
            FOOD_AND_DRINK -> res.getString(R.string.emoji_category_title_food)
            TRAVEL_AND_PLACES -> res.getString(R.string.emoji_category_title_places_travel)
            OBJECTS -> res.getString(R.string.emoji_category_title_objects)
            SYMBOLS -> res.getString(R.string.emoji_category_title_symbols)
            NUMBERS_AND_LETTERS -> res.getString(R.string.emoji_category_title_numbers_letters)
            FLAGS -> res.getString(R.string.emoji_category_title_flags)
            ACTIVITIES -> res.getString(R.string.emoji_category_title_activities)
        }
    }
}