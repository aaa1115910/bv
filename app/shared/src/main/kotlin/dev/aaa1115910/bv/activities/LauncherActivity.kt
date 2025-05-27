package dev.aaa1115910.bv.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.aaa1115910.bv.util.DeviceUtil
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * 启动器活动
 * 
 * 这个活动是应用的入口点，它会根据设备类型路由到合适的主活动
 */
class LauncherActivity : ComponentActivity() {
    private val logger = KotlinLogging.logger { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        routeToCorrectActivity()
    }
    
    /**
     * 根据设备类型路由到正确的活动
     */
    private fun routeToCorrectActivity() {
        // 检测设备类型
        val isTvDevice = DeviceUtil.isTvDevice(this)
        
        val intent = if (isTvDevice) {
            logger.info { "Detected TV device, launching TV MainActivity" }
            Intent(this, Class.forName("dev.aaa1115910.bv.tv.activities.MainActivity"))
        } else {
            logger.info { "Detected mobile device, launching Mobile MainActivity" }
            Intent(this, Class.forName("dev.aaa1115910.bv.mobile.activities.MainActivity"))
        }
        
        // 传递原始Intent中的所有数据
        intent.putExtras(getIntent())
        
        // 启动相应的MainActivity
        startActivity(intent)
        
        // 关闭当前Activity
        finish()
    }
    
    companion object {
        /**
         * 启动LauncherActivity
         */
        fun actionStart(context: Context) {
            val intent = Intent(context, LauncherActivity::class.java)
            context.startActivity(intent)
        }
    }
}
