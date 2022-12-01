package com.github.javiersantos.piracychecker.callbacks

interface AllowCallback {
    fun allow()
}

interface DoNotAllowCallback {
    fun doNotAllow()
}

interface OnErrorCallback {
    fun onError() {}
}

abstract class PiracyCheckerCallback : AllowCallback,
    DoNotAllowCallback,
    OnErrorCallback