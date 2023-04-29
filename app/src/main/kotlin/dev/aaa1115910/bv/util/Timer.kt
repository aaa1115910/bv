package dev.aaa1115910.bv.util

import android.os.CountDownTimer
import mu.KotlinLogging
import java.util.Timer
import java.util.TimerTask

fun countDownTimer(
    millisInFuture: Long,
    countDownInterval: Long,
    tag: String,
    onTick: ((Long) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): CountDownTimer {
    val logger = KotlinLogging.logger { }
    val timer = object : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            logger.info { "[$tag] Count down tick: $millisUntilFinished" }
            onTick?.invoke(millisUntilFinished)
        }

        override fun onFinish() {
            logger.info { "[$tag] Count down finished" }
            onComplete?.invoke()
        }
    }
    timer.start()
    return timer
}

fun timeTask(
    delay: Long,
    period: Long,
    tag: String,
    onTick: (() -> Unit)?
): Timer {
    val logger = KotlinLogging.logger { }
    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            logger.info { "[$tag] Time task run" }
            onTick?.invoke()
        }
    }, delay, period)
    return timer
}