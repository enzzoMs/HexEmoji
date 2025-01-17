package ems.hexemoji.ui.custom

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import ems.hexemoji.R
import ems.hexemoji.databinding.ViewCountdownBinding
import kotlin.math.round

private const val MAX_PROGRESS = 100

private const val COUNTDOWN_INTERVAL = 200L
private const val ONE_SECOND_IN_MILLIS = 1000L

private const val END_ANIMATION_DURATION = 300L
private const val END_ANIMATION_DELAY = 50L
private const val END_ANIMATION_SCALE_FACTOR = 1.6f

private const val COUNTDOWN_FADE_ANIMATION_DURATION = 200L
private const val COUNTDOWN_FADE_ANIMATION_DELAY = 500L

class CountDownView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private val binding: ViewCountdownBinding

    private val initialValue: Int
    private val endText: String

    private val countdownTimer: CountDownTimer
    private var onFinished: MutableList<() -> Unit> = mutableListOf()

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CountdownView, 0, 0).apply {
            initialValue = getInt(R.styleable.CountdownView_initialValue, 0)
            endText = getString(R.styleable.CountdownView_endText) ?: ""
        }

        binding = ViewCountdownBinding.inflate(LayoutInflater.from(context), this).also {
            it.countdownCurrentValue.text = initialValue.toString()
        }

        countdownTimer = object : CountDownTimer(initialValue * ONE_SECOND_IN_MILLIS, COUNTDOWN_INTERVAL) {
            private var secondsLeft = initialValue

            override fun onTick(p0: Long) {
                val currentSecond = round(p0 / ONE_SECOND_IN_MILLIS.toFloat()).toInt()

                if (currentSecond != secondsLeft) {
                    secondsLeft = currentSecond
                    binding.countdownCurrentValue.text = currentSecond.toString()
                }
            }

            override fun onFinish() {
                binding.apply {
                    countdownProgressIndicator.visibility = View.GONE

                    countdownCurrentValue.apply {
                        text = endText
                        countdownCurrentValue.scaleX = 0f
                        countdownCurrentValue.scaleY = 0f
                    }

                    AnimatorSet().apply {
                        playTogether(
                            ObjectAnimator.ofPropertyValuesHolder(
                                countdownCurrentValue,
                                PropertyValuesHolder.ofFloat("scaleX", 1f),
                                PropertyValuesHolder.ofFloat("scaleY", 1f)
                            ),
                            ObjectAnimator.ofPropertyValuesHolder(
                                root,
                                PropertyValuesHolder.ofFloat("scaleX", END_ANIMATION_SCALE_FACTOR),
                                PropertyValuesHolder.ofFloat("scaleY", END_ANIMATION_SCALE_FACTOR)
                            )
                        )
                        duration = END_ANIMATION_DURATION
                        startDelay = END_ANIMATION_DELAY

                        doOnEnd {
                            animateCountdownFadeOut()
                        }
                    }.start()
                }
            }
        }
    }

    fun start() {
        reset()

        ObjectAnimator.ofInt(
            binding.countdownProgressIndicator, "progress", 0
        ).apply {
            duration = initialValue * ONE_SECOND_IN_MILLIS
            interpolator = LinearInterpolator()
        }.start()

        countdownTimer.start()
    }

    fun reset() {
        countdownTimer.cancel()

        binding.apply {
            countdownProgressIndicator.progress = MAX_PROGRESS
            countdownProgressIndicator.visibility = View.VISIBLE

            countdownCurrentValue.scaleX = 1f
            countdownCurrentValue.scaleY = 1f

            root.scaleX = 1f
            root.scaleY = 1f

            countdownCurrentValue.text = initialValue.toString()
        }
    }

    fun addOnCountdownFinished(onFinished: () -> Unit) {
        this.onFinished.add(onFinished)
    }

    private fun animateCountdownFadeOut() {
        ObjectAnimator.ofFloat(binding.root, "alpha", 0f).apply {
            duration = COUNTDOWN_FADE_ANIMATION_DURATION
            startDelay = COUNTDOWN_FADE_ANIMATION_DELAY

            doOnEnd {
                binding.root.visibility = View.GONE
                onFinished.forEach { it() }
            }
        }.start()
    }
}