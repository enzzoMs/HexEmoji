package sarueh.hexemoji.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import sarueh.hexemoji.R
import sarueh.hexemoji.databinding.ViewBarChartBinding

private const val CLIP_DRAWABLE_LEVEL_FACTOR = 10_000

/**
 * A simple chart view that allows to determine a set of labels and display the associated data using bars.
 * This view requires a [BarChartDataProvider] to work properly, which should be set using the
 * [setDataProvider] method.
 */
class BarChartView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private val binding = ViewBarChartBinding.inflate(LayoutInflater.from(context), this)
    private var chartDataProvider: BarChartDataProvider? = null

    private var loading = false

    fun setDataProvider(dataProvider: BarChartDataProvider) {
        chartDataProvider = dataProvider

        updateChartHeader()
        updateBarLabels()

        if (!loading) {
            updateBarsValues()
            updateBars()
        }
    }

    fun setLoading(loading: Boolean) {
        this.loading = loading

        binding.apply {
            if (loading) {
                barsValuesLayout.visibility = View.INVISIBLE
                barsLayout.visibility = View.INVISIBLE
                chartLoading.visibility = View.VISIBLE
            } else {
                barsValuesLayout.visibility = View.VISIBLE
                barsLayout.visibility = View.VISIBLE
                chartLoading.visibility = View.INVISIBLE

                if (chartDataProvider != null) {
                    updateBarsValues()
                    updateBars()
                }
            }
        }
    }

    private fun updateChartHeader() {
        binding.chartTitle.text = chartDataProvider?.chartTitle
        binding.chartDataDescription.text = chartDataProvider?.chartDataDescription
    }

    private fun updateBarLabels() {
        binding.barsLabelsLayout.removeAllViews()

        chartDataProvider?.barsLabels?.forEach { label ->
            binding.barsLabelsLayout.addView(
                TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_CONSTRAINT, LayoutParams.WRAP_CONTENT, 1f
                    )
                    gravity = Gravity.CENTER
                    text = label
                    typeface = ResourcesCompat.getFont(context, R.font.lexend_semibold)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_subtitle2))
                }
            )
        }
    }

    private fun updateBarsValues() {
        binding.barsValuesLayout.removeAllViews()

        chartDataProvider?.barsLabels?.forEachIndexed { index, _ ->
            binding.barsValuesLayout.addView(
                TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_CONSTRAINT, LayoutParams.WRAP_CONTENT, 1f
                    )
                    gravity = Gravity.CENTER
                    text = chartDataProvider?.getBarValueForPosition(index).toString()
                    typeface = ResourcesCompat.getFont(context, R.font.lexend_semibold)
                    setTextColor(ContextCompat.getColor(context, R.color.accent_color))
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_subtitle2))
                }
            )
        }
    }

    private fun updateBars() {
        binding.apply {
            barsLayout.removeAllViews()

            chartDataProvider?.barsLabels?.forEachIndexed { index, _ ->
                val barView = View(context)

                barView.layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_PARENT, 1f
                ).apply {
                    val margin = resources.getDimension(R.dimen.bar_chart_bar_horizontal_margin).toInt()

                    marginEnd = margin
                    marginStart = margin
                }

                val barShape = ShapeAppearanceModel()
                    .toBuilder()
                    .setAllCorners(RoundedCornerTreatment())
                    .setAllCornerSizes(RelativeCornerSize(0.25f))
                    .build()

                val barDrawable = MaterialShapeDrawable(barShape).apply {
                    fillColor = ContextCompat.getColorStateList(context, R.color.accent_color)
                }

                val barClippedDrawable = ClipDrawable(barDrawable, Gravity.BOTTOM, ClipDrawable.VERTICAL).apply {
                    val barHeightPercentage = chartDataProvider!!.getBarValueForPosition(index) /
                            chartDataProvider!!.getMaxValue().toFloat()

                    level = (barHeightPercentage * CLIP_DRAWABLE_LEVEL_FACTOR).toInt()
                    colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
                }

                val barBackgroundDrawable = MaterialShapeDrawable(barShape).apply {
                    fillColor = ContextCompat.getColorStateList(context, R.color.unselected_icon_color).apply {
                        alpha = resources.getInteger(R.integer.bar_chart_bar_background_alpha)
                    }
                }

                barView.background = LayerDrawable(arrayOf(barBackgroundDrawable, barClippedDrawable))

                barsLayout.addView(barView)
            }
        }
    }
}