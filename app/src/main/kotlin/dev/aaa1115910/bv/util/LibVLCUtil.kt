package dev.aaa1115910.bv.util

import dev.aaa1115910.bv.BVApp
import java.io.File

object LibVLCUtil {
    fun existLibs(): Boolean {
        val fileNameList = listOf("libvlc.so", "libc++_shared.so", "libvlcjni.so")
        fileNameList.forEach { fileName ->
            val file = File(BVApp.context.filesDir.path + "/vlc_libs/$fileName")
            if (!file.exists()) return false
        }
        return true
    }
}