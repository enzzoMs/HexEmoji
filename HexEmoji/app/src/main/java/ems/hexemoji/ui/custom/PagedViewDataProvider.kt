package ems.hexemoji.ui.custom

interface PagedViewDataProvider {

    fun getTitle(position: Int): String?

    fun getDescription(position : Int): String?

    fun getDrawableId(position: Int): Int?

    fun getTotalItems(): Int
}