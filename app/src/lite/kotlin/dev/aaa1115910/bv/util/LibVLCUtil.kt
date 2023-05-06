package dev.aaa1115910.bv.util

import android.content.Context
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.entity.PlayerType
import mu.KotlinLogging
import org.videolan.libvlc.LibVLC
import java.io.File

object LibVLCUtil {
    private val logger = KotlinLogging.logger { }

    fun existLibs(): Boolean {
        val fileNameList = listOf("libvlc.so", "libc++_shared.so", "libvlcjni.so")
        fileNameList.forEach { fileName ->
            val file = File(BVApp.context.filesDir.path + "/vlc_libs/$fileName")
            if (!file.exists()) return false
        }
        return true
    }

    fun getVersion(): String = LibVLC.version()

    fun clearLibs() {
        val libsDir = File(BVApp.context.filesDir.path + "/vlc_libs")
        libsDir.deleteRecursively()
    }

    fun loadLibraries(context: Context) {
        LibVLC.loadLibraries(context)
    }


    fun checkLibVLC(context: Context) {
        val clearLibs = {
            Prefs.playerType = PlayerType.Media3
            clearLibs()
        }

        logger.fInfo { "Current play type: ${Prefs.playerType.name}" }
        if (Prefs.playerType == PlayerType.LibVLC) {
            if (!existLibs()) {
                logger.fInfo { "LibVLC libs not exist" }
                clearLibs()
                "LibVLC 文件不存在，播放器切换回 Media3".toast(context)
                return
            }
            runCatching {
                loadLibraries(context)
            }.onFailure {
                Prefs.playerType = PlayerType.Media3
                logger.fWarn { "Load LibVLC failed: ${it.stackTraceToString()}" }
                "加载 LibVLC 失败，播放器切换回 Media3".toast(context)
            }
            runCatching {
                val localVersion = getVersion()
                val requiredVersion = dev.aaa1115910.bv.player.BuildConfig.libVLCVersion
                if (!localVersion.startsWith(requiredVersion)) {
                    clearLibs()
                    logger.fWarn { "Local LibVLC version $localVersion is not match required version $requiredVersion" }
                    "LibVLC 版本不匹配，已移除 LibVLC 相关文件".toast(context)
                }
            }.onFailure {
                Prefs.playerType = PlayerType.Media3
                logger.fWarn { "Check LibVLC version failed: ${it.stackTraceToString()}" }
                "检查本地 LibVLC 版本失败，播放器切换回 Media3".toast(context)
            }
        }
    }
}