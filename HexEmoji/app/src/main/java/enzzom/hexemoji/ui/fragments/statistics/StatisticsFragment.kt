package enzzom.hexemoji.ui.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentStatisticsBinding
import enzzom.hexemoji.models.WeekDay
import enzzom.hexemoji.ui.custom.BarChartDataProvider
import enzzom.hexemoji.ui.fragments.statistics.model.StatisticsViewModel

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val statisticsViewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.apply {
            victoriesStatisticsChart.setLoading(true)

            statisticsViewModel.statisticsLoading.observe(viewLifecycleOwner) { loading ->
                victoriesStatisticsChart.setLoading(loading)
            }

            victoriesStatisticsChart.setDataProvider(object : BarChartDataProvider {

                override val chartTitle: String =  resources.getString(
                    R.string.weekly_victories_chart_title
                )

                override val chartDataDescription: String = statisticsViewModel.currentWeekDate

                override val barsLabels: List<String> = resources.getStringArray(
                    R.array.week_days_abbreviation
                ).toList()

                override fun getMaxValue(): Int = statisticsViewModel.getVictoriesCurrentInWeek() ?: 0

                override fun getBarValueForPosition(position: Int): Int {
                    return statisticsViewModel.getVictoriesInWeekDay(WeekDay.entries[position]) ?: 0
                }
            })
        }

        return binding.root
    }
}