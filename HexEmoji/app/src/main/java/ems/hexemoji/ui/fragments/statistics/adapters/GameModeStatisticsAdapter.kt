package ems.hexemoji.ui.fragments.statistics.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ems.hexemoji.databinding.ItemCardGameModeStatisticBinding

class GameModeStatisticsAdapter(
    private var statisticsWithValues: Map<String, String>? = null
) : RecyclerView.Adapter<GameModeStatisticsAdapter.GameModeStatisticHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameModeStatisticHolder {
        val inflater = LayoutInflater.from(parent.context)

        return GameModeStatisticHolder(
            ItemCardGameModeStatisticBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GameModeStatisticHolder, position: Int) {
        statisticsWithValues?.let {
            val statistic = it.keys.elementAt(position)

            holder.bind(
                title = statistic,
                value = it[statistic]!!
            )
        }
    }

    override fun getItemCount(): Int = statisticsWithValues?.size ?: 0


    @SuppressLint("NotifyDataSetChanged")
    fun replaceStatistics(newStatistics: Map<String, String>) {
        statisticsWithValues = newStatistics
        notifyDataSetChanged()
    }

    inner class GameModeStatisticHolder(
        private val binding: ItemCardGameModeStatisticBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, value: String) {
            binding.apply {
                gameModeStatisticTitle.text = title
                gameModeStatisticValue.text = value
            }
        }
    }
}