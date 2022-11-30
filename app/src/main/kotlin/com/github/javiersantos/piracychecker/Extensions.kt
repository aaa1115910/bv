package com.github.javiersantos.piracychecker

import android.content.Context
import androidx.fragment.app.Fragment
import com.github.javiersantos.piracychecker.callbacks.AllowCallback
import com.github.javiersantos.piracychecker.callbacks.DoNotAllowCallback
import com.github.javiersantos.piracychecker.callbacks.OnErrorCallback
import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallbacksDSL

fun Context.piracyChecker(builder: PiracyChecker.() -> Unit): PiracyChecker {
    val checker = PiracyChecker(this)
    checker.builder()
    return checker
}

fun Fragment.piracyChecker(builder: PiracyChecker.() -> Unit): PiracyChecker =
    activity?.piracyChecker(builder) ?: requireContext().piracyChecker(builder)

inline fun PiracyChecker.allow(crossinline allow: () -> Unit = {}) = apply {
    allowCallback(object : AllowCallback {
        override fun allow() = allow()
    })
}

inline fun PiracyChecker.doNotAllow(crossinline doNotAllow: () -> Unit = { }) =
    apply {
        doNotAllowCallback(object : DoNotAllowCallback {
            override fun doNotAllow() =
                doNotAllow()
        })
    }

inline fun PiracyChecker.onError(crossinline onError: () -> Unit = {}) = apply {
    onErrorCallback(object : OnErrorCallback {
        override fun onError() {
            super.onError()
            onError()
        }
    })
}

fun PiracyChecker.callback(callbacks: PiracyCheckerCallbacksDSL.() -> Unit) {
    PiracyCheckerCallbacksDSL(this).callbacks()
}