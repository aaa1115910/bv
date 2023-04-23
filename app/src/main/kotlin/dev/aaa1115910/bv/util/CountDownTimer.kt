package dev.aaa1115910.bv.util

import android.os.CountDownTimer
import mu.KotlinLogging

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
