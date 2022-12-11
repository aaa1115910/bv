package dev.aaa1115910.bv.util

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.aaa1115910.bv.BuildConfig
import mu.KLogger

fun KLogger.fInfo(msg: () -> Any?) {
    info(msg)
    Firebase.crashlytics.log("[Info] $msg")
}

fun KLogger.fWarn(msg: () -> Any?) {
    warn(msg)
    Firebase.crashlytics.log("[Warn] $msg")
}

fun KLogger.fDebug(msg: () -> Any?) {
    if (BuildConfig.DEBUG) {
        debug(msg)
        Firebase.crashlytics.log("[Debug] $msg")
    }
}

fun KLogger.fError(msg: () -> Any?) {
    error(msg)
    Firebase.crashlytics.log("[Error] $msg")
}

fun KLogger.fException(throwable: Throwable, msg: () -> Any?) {
    warn(throwable, msg)
    Firebase.crashlytics.log("[Exception] $msg: ${throwable.localizedMessage}")
    Firebase.crashlytics.recordException(throwable)
}