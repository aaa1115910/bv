package dev.aaa1115910.bv.util

import dev.aaa1115910.bv.BVApp
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogCatcherUtil {
    private val logger = KotlinLogging.logger("LogCatcher")
    const val LOG_DIR = "crash_logs"
    private const val MANUAL_LOG_PREFIX = "logs_manual"
    private const val CRASH_LOG_PREFIX = "logs_crash"
    private const val MAX_LOG_COUNT = 10
    var manualFiles: List<File> = emptyList()
    var crashFiles: List<File> = emptyList()

    fun installLogCatcher() {
        runCatching {
            Runtime.getRuntime().exec("logcat -c")
            logger.info { "clear logcat" }
        }
        val originHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            logger.error(exception) { "======== UncaughtException ========" }
            logLogcat()
            originHandler?.uncaughtException(thread, exception)
        }
        clearOldLogFiles()
    }

    fun logLogcat(manual: Boolean = false) {
        runCatching {
            val process = Runtime.getRuntime().exec("logcat -t 10000 -v threadtime")
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val logDir = File(BVApp.context.filesDir, LOG_DIR)
            if (!logDir.exists()) logDir.mkdir()

            val logFile = File(logDir, createFilename(manual))
            logFile.createNewFile()
            logger.info { "Log file: $logFile" }

            with(logFile.writer()) {
                writeDeviceInfo()
                writeAppInfo()
                appendLine("======== Logs ========")
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    appendLine(line)
                }
                flush()
                close()
                reader.close()
            }
        }.onFailure {
            logger.error(it) { "write log to file failed" }
        }
    }

    private fun OutputStreamWriter.writeDeviceInfo() {
        val info = BVApp.context.packageManager.getPackageInfo(BVApp.context.packageName, 0)
        appendLine("======== Device info ========")
        appendLine("App Version: ${info.versionName} (${info.versionCode})")
        appendLine("Android Version: ${android.os.Build.VERSION.RELEASE} (${android.os.Build.VERSION.SDK_INT})")
        appendLine("Device: ${android.os.Build.DEVICE}")
        appendLine("Model: ${android.os.Build.MODEL}")
        appendLine("Manufacturer: ${android.os.Build.MANUFACTURER}")
        appendLine("Brand: ${android.os.Build.BRAND}")
        appendLine("Product: ${android.os.Build.PRODUCT}")
        appendLine("Type: ${android.os.Build.TYPE}")
    }

    private fun OutputStreamWriter.writeAppInfo() {
        appendLine("======== App Prefs ========")
        appendLine("Login: ${Prefs.isLogin}")
        appendLine("Incognito Mode: ${Prefs.incognitoMode}")
        appendLine("Api Type: ${Prefs.apiType.name}")
        appendLine("Default Resolution: ${Prefs.defaultQuality}")
        appendLine("Default Codec: ${Prefs.defaultVideoCodec.name}")
        appendLine("Default Audio: ${Prefs.defaultAudio.name}")
        appendLine("Enabled Proxy: ${Prefs.enableProxy}")
    }

    private fun createFilename(manual: Boolean): String {
        var filename = ""
        filename += if (manual) MANUAL_LOG_PREFIX else CRASH_LOG_PREFIX
        val date = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())
        filename += "_$date.log"
        return filename
    }

    fun updateLogFiles() {
        val files = File(BVApp.context.filesDir, LOG_DIR).listFiles()
        manualFiles = files
            ?.filter { it.name.startsWith(MANUAL_LOG_PREFIX) }
            ?.sortedBy { it.lastModified() }
            ?: emptyList()
        crashFiles = files
            ?.filter { it.name.startsWith(CRASH_LOG_PREFIX) }
            ?.sortedBy { it.lastModified() }
            ?: emptyList()
    }

    private fun clearOldLogFiles() {
        updateLogFiles()

        if (manualFiles.size > MAX_LOG_COUNT) {
            manualFiles.take(manualFiles.size - MAX_LOG_COUNT).forEach { it.delete() }
        }
        if (crashFiles.size > MAX_LOG_COUNT) {
            crashFiles.take(crashFiles.size - MAX_LOG_COUNT).forEach { it.delete() }
        }
    }
}