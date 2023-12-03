package enzzom.hexemoji.ui.custom

interface GameTutorialDataProvider {

    fun getDescription(position : Int): String

    fun getDrawableId(position: Int): Int

    fun getTotalItems(): Int
}