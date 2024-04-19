package sarueh.hexemoji.utils

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.floor

private const val ONE_SECOND_IN_MILLIS = 1000L

class CountdownManager(
    val startTimeMs: Long,
    private val onCountdownFinished: () -> Unit = {}
) {
    private var remainingTimeMs: Long = startTimeMs

    private val _remainingSeconds = MutableLiveData(startTimeMs / ONE_SECOND_IN_MILLIS)
    val remainingSeconds: LiveData<Long> = _remainingSeconds

    private var countdownTimer: CountDownTimer? = null
    private var timerPaused: Boolean = false

    fun startTimer() {
        countdownTimer = getTimer(startTimeMs).start()
        timerPaused = false
    }

    fun pauseTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
        timerPaused = true
    }

    fun resumeTimer() {
        countdownTimer?.cancel()
        timerPaused = false
        countdownTimer = getTimer(remainingTimeMs).start()
    }

    fun isTimerPaused(): Boolean = timerPaused

    private fun getTimer(millisInFuture: Long): CountDownTimer = object : CountDownTimer(millisInFuture, ONE_SECOND_IN_MILLIS) {
        override fun onTick(millisUntilFinished: Long) {
            remainingTimeMs = millisUntilFinished

            val secondsUntilFinished  = floor(millisUntilFinished / ONE_SECOND_IN_MILLIS.toFloat()).toLong()

            if (secondsUntilFinished == 0L) {
                onCountdownFinished()
            }

            _remainingSeconds.value = secondsUntilFinished
        }

        override fun onFinish() {}
    }
}