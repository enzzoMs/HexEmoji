package sarueh.hexemoji.ui.fragments.statistics

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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import dagger.hilt.android.AndroidEntryPoint
import sarueh.hexemoji.R
import sarueh.hexemoji.databinding.FragmentStatisticsBinding
import sarueh.hexemoji.models.GameMode
import sarueh.hexemoji.models.WeekDay
import sarueh.hexemoji.ui.custom.BarChartDataProvider
import sarueh.hexemoji.ui.fragments.main.MainFragment
import sarueh.hexemoji.ui.fragments.statistics.adapters.GameModeStatisticsAdapter
import sarueh.hexemoji.ui.fragments.statistics.model.StatisticsViewModel
import sarueh.hexemoji.utils.StringUtils

private const val DAILY_EMOJI_ANIM_DURATION_MS = 450L

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val statisticsViewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (parentFragment?.parentFragment as MainFragment).setToolbarTitle(
            resources.getString(R.string.page_title_page_statistics)
        )

        val binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.apply {
            victoriesStatisticsChart.setLoading(true)

            statisticsViewModel.statisticsLoading.observe(viewLifecycleOwner) { loading ->
                victoriesStatisticsChart.setLoading(loading)

                if (!loading) {
                    val selectedIndex = statisticsGameModeTabs.selectedTabPosition

                    (gameModeStatisticsList.adapter as GameModeStatisticsAdapter).replaceStatistics(
                        if (selectedIndex == 0) {
                            getAllStatisticsWithValues()
                        } else {
                            getStatisticsWithValues(GameMode.entries[selectedIndex - 1])
                        }
                    )

                    dailyEmojiLoading.visibility = View.GONE
                    dailyEmoji.visibility = View.VISIBLE

                    statisticsViewModel.dailyEmojiReward.let { emojiUnicode ->
                        if (emojiUnicode == null) {
                            dailyEmojiCard.visibility = View.GONE

                        } else if (statisticsViewModel.dailyEmojiUnlocked()) {
                            dailyEmoji.visibility = View.GONE
                            dailyEmojiMessage.text = resources.getString(R.string.daily_emoji_message_already_unlocked)

                        } else {
                            dailyEmoji.text = StringUtils.unescapeString(emojiUnicode)
                            dailyEmojiMessage.text = resources.getString(R.string.daily_emoji_message)
                            dailyEmojiCard.isClickable = true
                        }
                    }
                }
            }

            victoriesStatisticsChart.setDataProvider(object : BarChartDataProvider {

                override val chartTitle: String = resources.getString(
                    R.string.weekly_victories_chart_title
                )

                override val chartDataDescription: String = statisticsViewModel.currentWeekDate

                override val barsLabels: List<String> = resources.getStringArray(
                    R.array.week_days_abbreviation
                ).toList()

                override fun getMaxValue(): Int = statisticsViewModel.getVictoriesInCurrentWeek() ?: 0

                override fun getBarValueForPosition(position: Int): Int {
                    return statisticsViewModel.getVictoriesInWeekDay(WeekDay.entries[position]) ?: 0
                }
            })

            dailyEmojiCard.setOnClickListener {
                statisticsViewModel.unlockDailyEmoji()

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

            dailyEmojiCard.isClickable = false

            statisticsGameModeTabs.addTab(
                statisticsGameModeTabs.newTab().apply {
                    text = resources.getString(R.string.statistics_game_modes_all)
                }
            )

            GameMode.entries.forEach { gameMode ->
                statisticsGameModeTabs.newTab().let { tab ->
                    tab.text = gameMode.getTitle(resources)
                    statisticsGameModeTabs.addTab(tab)
                }
            }

            statisticsGameModeTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: Tab?) {
                    if (tab != null && statisticsViewModel.statisticsLoading.value == false) {
                        val selectedIndex = tab.position

                        (gameModeStatisticsList.adapter as GameModeStatisticsAdapter).replaceStatistics(
                            if (selectedIndex == 0) {
                                getAllStatisticsWithValues()
                            } else {
                                getStatisticsWithValues(GameMode.entries[selectedIndex - 1])
                            }
                        )
                    }
                }

                override fun onTabUnselected(tab: Tab?) {}
                override fun onTabReselected(tab: Tab?) {}
            })

            gameModeStatisticsList.adapter = GameModeStatisticsAdapter()
        }

        return binding.root
    }

    private fun getStatisticsWithValues(gameMode: GameMode): Map<String, String> {
        return mapOf(
            resources.getString(R.string.game_mode_statistic_total_games)
                    to statisticsViewModel.getGameModeTotalGames(gameMode).toString(),

            resources.getString(R.string.game_mode_statistic_victory_percentage)
                to (statisticsViewModel.getGameModeVictoryPercentage(gameMode)?.times(100)?.toInt()).toString() + "%",

            resources.getString(R.string.game_mode_statistic_pairs_found)
                    to statisticsViewModel.getGameModePairsFound(gameMode).toString(),

            resources.getString(R.string.game_mode_statistic_favorite_board)
                    to (statisticsViewModel.getGameModeFavoriteBoard(gameMode)?.getLabel() ?: "--").toString()
        )
    }

    private fun getAllStatisticsWithValues(): Map<String, String> {
        return mapOf(
            resources.getString(R.string.game_mode_statistic_total_games)
                    to GameMode.entries.fold(0) { sum, gameMode ->
                        sum + (statisticsViewModel.getGameModeTotalGames(gameMode) ?: 0)
                    }.toString(),

            resources.getString(R.string.game_mode_statistic_victory_percentage)
                    to (statisticsViewModel.getGeneralVictoryPercentage()?.times(100)?.toInt()).toString() + "%",

            resources.getString(R.string.game_mode_statistic_pairs_found)
                    to GameMode.entries.fold(0) { sum, gameMode ->
                        sum + (statisticsViewModel.getGameModePairsFound(gameMode) ?: 0)
                    }.toString(),

            resources.getString(R.string.game_mode_statistic_favorite_board)
                    to (statisticsViewModel.getGeneralFavoriteBoard()?.getLabel() ?: "--").toString()
        )
    }
}