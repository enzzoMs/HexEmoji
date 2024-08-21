package ems.hexemoji.ui.custom

interface BarChartDataProvider {

    val chartTitle: String?

    val chartDataDescription: String?

    val barsLabels: List<String>

    fun getMaxValue(): Int

    fun getBarValueForPosition(position: Int): Int
}