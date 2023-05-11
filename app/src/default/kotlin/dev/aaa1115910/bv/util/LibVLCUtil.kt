package dev.aaa1115910.bv.util

import android.content.Context
import org.videolan.libvlc.LibVLC

object LibVLCUtil {
    fun existLibs(): Boolean = true
    fun getVersion(): String = LibVLC.version()
    fun clearLibs() {}
    fun loadLibraries(context: Context) {}
    fun checkLibVLC(context: Context) {}
}