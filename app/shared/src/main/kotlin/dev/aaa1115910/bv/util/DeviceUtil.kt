package dev.aaa1115910.bv.util

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import dev.aaa1115910.bv.BVApp

/**
 * 用于检测设备类型和特性的工具类
 */
object DeviceUtil {
    /**
     * 判断当前设备是否为TV设备
     * 
     * 使用多种方法进行判断:
     * 1. 通过UiModeManager检查当前UI模式是否为TV
     * 2. 检查设备是否声明支持Leanback特性
     * 3. 检查设备屏幕是否为TV类型
     * 
     * @return 如果是TV设备则返回true，否则返回false
     */
    fun isTvDevice(context: Context = BVApp.context): Boolean {
        // 方法1: 使用UiModeManager检查当前UI模式
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true
        }

        // 方法2: 检查设备是否声明支持Leanback特性
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            return true
        }

        // 方法3: 检查设备屏幕配置
        val configuration = context.resources.configuration
        if (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            // 大屏幕设备 + 没有触摸屏 可能是TV设备
            if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
                return true
            }
        }

        return false
    }
}
