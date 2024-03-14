package enzzom.hexemoji.ui.fragments.statistics

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.FragmentStatisticsBinding
import enzzom.hexemoji.models.WeekDay
import enzzom.hexemoji.ui.custom.BarChartDataProvider
import enzzom.hexemoji.ui.fragments.statistics.model.StatisticsViewModel
import enzzom.hexemoji.utils.StringUtils

private const val DAILY_EMOJI_ANIM_DURATION_MS = 450L

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

            statisticsViewModel.dailyEmojiReward.let { emojiUnicode ->
                if (emojiUnicode == null) {
                    dailyEmojiCard.visibility = View.GONE

                } else if (statisticsViewModel.dailyEmojiUnlocked()) {
                    dailyEmojiCard.isClickable = false
                    dailyEmoji.visibility = View.GONE
                    dailyEmojiMessage.text = resources.getString(R.string.daily_emoji_message_already_unlocked)

                } else {
                    dailyEmoji.text = StringUtils.unescapeString(emojiUnicode)
                    dailyEmojiMessage.text = resources.getString(R.string.daily_emoji_message)
                }
            }

            dailyEmojiCard.setOnClickListener {
                statisticsViewModel.unlockDailyEmoji()

                dailyEmojiCard.isClickable = false

                ObjectAnimator.ofArgb(
                    dailyEmojiDivider,
                    "dividerColor",
                    ContextCompat.getColor(requireContext(), R.color.primary_color),
                    ContextCompat.getColor(requireContext(), R.color.accent_color),
                    ContextCompat.getColor(requireContext(), R.color.primary_color)
                ).apply {
                    duration = DAILY_EMOJI_ANIM_DURATION_MS
                    interpolator = LinearInterpolator()

                    doOnEnd {
                        dailyEmoji.visibility = View.GONE
                        dailyEmojiMessage.text = resources.getString(R.string.daily_emoji_message_already_unlocked)
                    }
                }.start()
            }
        }

        return binding.root
    }
}