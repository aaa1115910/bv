package com.github.javiersantos.piracychecker.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.util.Base64
import java.security.MessageDigest

val Context.apkSignatures: Array<String>
    get() = currentSignatures

@Suppress("DEPRECATION", "RemoveExplicitTypeArguments")
private val Context.currentSignatures: Array<String>
    get() {
        val actualSignatures = ArrayList<String>()
        val signatures: Array<Signature> = try {
            val packageInfo =
                packageManager.getPackageInfo(
                    packageName,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                        PackageManager.GET_SIGNING_CERTIFICATES
                    else PackageManager.GET_SIGNATURES
                )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (packageInfo.signingInfo.hasMultipleSigners())
                    packageInfo.signingInfo.apkContentsSigners
                else packageInfo.signingInfo.signingCertificateHistory
            } else packageInfo.signatures
        } catch (e: Exception) {
            arrayOf<Signature>()
        }
        signatures.forEach { signature ->
            val messageDigest = MessageDigest.getInstance("SHA")
            messageDigest.update(signature.toByteArray())
            runCatching {
                actualSignatures.add(
                    Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim()
                )
            }
        }
        return actualSignatures.filter { it.isNotEmpty() && it.isNotBlank() }.toTypedArray()
    }

private fun Context.verifySigningCertificate(appSignature: String?): Boolean =
    appSignature?.let { appSign -> currentSignatures.any { it == appSign } } ?: false

internal fun Context.verifySigningCertificates(appSignatures: Array<String>): Boolean {
    var validCount = 0
    appSignatures.forEach { if (verifySigningCertificate(it)) validCount += 1 }
    return validCount >= appSignatures.size
}
