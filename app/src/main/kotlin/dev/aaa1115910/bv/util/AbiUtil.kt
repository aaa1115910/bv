package dev.aaa1115910.bv.util

import dev.aaa1115910.bv.BVApp
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object AbiUtil {
    fun getApkSupportedAbiSet(): Set<String> {
        val apkPath = BVApp.context.applicationInfo.sourceDir
        val apkFile = File(apkPath)

        var elementName: String

        val abiSet = mutableSetOf<String>()
        var zipFile: ZipFile? = null

        runCatching {
            zipFile = ZipFile(apkFile)
            val entries = zipFile!!.entries()
            var entry: ZipEntry

            while (entries.hasMoreElements()) {
                entry = entries.nextElement()
                if (entry.isDirectory) continue
                elementName = entry.name

                if (elementName.startsWith("lib/")) {
                    elementName = elementName.removePrefix("lib/")
                    when {
                        elementName.startsWith("arm64-v8a/") -> abiSet.add("arm64-v8a")
                        elementName.startsWith("armeabi-v7a/") -> abiSet.add("armeabi-v7a")
                        elementName.startsWith("x86_64/") -> abiSet.add("x86_64")
                        elementName.startsWith("x86/") -> abiSet.add("x86")
                    }
                }
            }
        }

        zipFile?.close()
        return abiSet
    }
}