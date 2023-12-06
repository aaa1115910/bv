package dev.aaa1115910.bv.util

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.aaa1115910.bv.BuildConfig
import io.github.oshai.kotlinlogging.KLogger

fun KLogger.fInfo(msg: () -> Any?) {
    info(msg)
    Firebase.crashlytics.log("[Info] ${msg.toStringSafe()}")
}

fun KLogger.fWarn(msg: () -> Any?) {
    warn(msg)
    Firebase.crashlytics.log("[Warn] ${msg.toStringSafe()}")
}

fun KLogger.fDebug(msg: () -> Any?) {
    if (BuildConfig.DEBUG) {
        info(msg)
        Firebase.crashlytics.log("[Debug] ${msg.toStringSafe()}")
    }
}

fun KLogger.fError(msg: () -> Any?) {
    error(msg)
    Firebase.crashlytics.log("[Error] ${msg.toStringSafe()}")
}

fun KLogger.fException(throwable: Throwable, msg: () -> Any?) {
    warn("$msg: ${throwable.stackTraceToString()}")
    Firebase.crashlytics.log("[Exception] ${msg.toStringSafe()}: ${throwable.localizedMessage}")
    Firebase.crashlytics.recordException(throwable)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun (() -> Any?).toStringSafe(): String {
    return try {
        invoke().toString()
    } catch (e: Exception) {
        ErrorMessageProducer.getErrorLog(e)
    }
}

internal object ErrorMessageProducer {
    fun getErrorLog(e: Exception): String {
        if (System.getProperties().containsKey("kotlin-logging.throwOnMessageError")) {
            throw e
        } else {
            return "Log message invocation failed: $e"
        }
    }
}
