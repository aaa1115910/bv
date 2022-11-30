package com.github.javiersantos.piracychecker

import android.app.Activity
import android.content.Context
import com.github.javiersantos.piracychecker.callbacks.AllowCallback
import com.github.javiersantos.piracychecker.callbacks.DoNotAllowCallback
import com.github.javiersantos.piracychecker.callbacks.OnErrorCallback
import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallback
import com.github.javiersantos.piracychecker.utils.verifySigningCertificates

class PiracyChecker(
    private var context: Context?
) {
    private var enableSigningCertificate: Boolean = false
    private var signatures: Array<String> = arrayOf()

    private var allowCallback: AllowCallback? = null
    private var doNotAllowCallback: DoNotAllowCallback? = null
    private var onErrorCallback: OnErrorCallback? = null

    fun enableSigningCertificates(vararg signatures: String): PiracyChecker {
        this.enableSigningCertificate = true
        this.signatures = arrayOf(*signatures)
        return this
    }

    fun enableSigningCertificates(signatures: List<String>): PiracyChecker {
        this.enableSigningCertificate = true
        this.signatures = signatures.toTypedArray()
        return this
    }

    fun allowCallback(allowCallback: AllowCallback): PiracyChecker {
        this.allowCallback = allowCallback
        return this
    }

    fun doNotAllowCallback(doNotAllowCallback: DoNotAllowCallback): PiracyChecker {
        this.doNotAllowCallback = doNotAllowCallback
        return this
    }

    fun onErrorCallback(errorCallback: OnErrorCallback): PiracyChecker {
        this.onErrorCallback = errorCallback
        return this
    }

    fun callback(callback: PiracyCheckerCallback): PiracyChecker {
        this.allowCallback = object : AllowCallback {
            override fun allow() {
                callback.allow()
            }
        }
        this.doNotAllowCallback = object : DoNotAllowCallback {
            override fun doNotAllow() {
                callback.doNotAllow()
            }
        }
        this.onErrorCallback = object : OnErrorCallback {
            override fun onError() {
                super.onError()
                callback.onError()
            }
        }
        return this
    }

    fun destroy() {
        context = null
    }

    fun start() {
        if (allowCallback == null && doNotAllowCallback == null) {
            callback(object : PiracyCheckerCallback() {
                override fun allow() {}

                override fun doNotAllow() {
                    if (context is Activity && (context as Activity).isFinishing) {
                        return
                    }
                }
            })
        }
        verify()
    }

    private fun verify() {
        // Library will check first the non-LVL methods since LVL is asynchronous and could take
        // some seconds to give a result
        if (!verifySigningCertificate()) {
            doNotAllowCallback?.doNotAllow()
        }
    }

    private fun verifySigningCertificate(): Boolean {
        return !enableSigningCertificate || (context?.verifySigningCertificates(signatures) == true)
    }
}