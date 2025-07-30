package dev.aaa1115910.bv.tv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.tv.screens.VideoInfoScreen
import dev.aaa1115910.bv.ui.theme.BVTheme
import java.lang.ref.WeakReference
import java.util.LinkedList

class VideoInfoActivity : ComponentActivity() {
    companion object {
        private const val MAX_VIDEO_INFO_SCREENS = 3
        // 使用WeakReference防止内存泄漏，避免持有已销毁Activity的强引用
        private val activityQueue = LinkedList<WeakReference<VideoInfoActivity>>()

        fun actionStart(
            context: Context,
            aid: Long,
            cid: Long? = null,
            fromSeason: Boolean = false,
            fromPlayer: Boolean = false,
            proxyArea: ProxyArea = ProxyArea.MainLand
        ) {
            context.startActivity(
                Intent(context, VideoInfoActivity::class.java).apply {
                    putExtra("aid", aid)
                    putExtra("cid", cid)
                    putExtra("fromSeason", fromSeason)
                    putExtra("fromPlayer", fromPlayer)
                    putExtra("proxy_area", proxyArea.ordinal)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 将当前活动加入队列
        synchronized(activityQueue) {
            // 清理队列中的无效引用 - 这步是必要的
            // 1. 确保队列大小计算准确，防止误判是否达到了MAX_VIDEO_INFO_SCREENS
            // 2. 处理可能未正常触发onDestroy的情况（如系统回收、应用崩溃等）
            // 3. 防止队列中累积无效引用导致内存泄漏
            val iterator = activityQueue.iterator()
            while (iterator.hasNext()) {
                val activityRef = iterator.next()
                val activity = activityRef.get()
                if (activity == null || activity.isFinishing) {
                    iterator.remove()
                }
            }

            // 添加当前活动到队列
            activityQueue.add(WeakReference(this))

            // 如果队列超过了最大限制，关闭最早的活动
            if (activityQueue.size > MAX_VIDEO_INFO_SCREENS) {
                // 移除最早的活动引用
                val oldestActivityRef = activityQueue.removeFirst()
                val oldestActivity = oldestActivityRef.get()
                // 确保在主线程调用finish()
                oldestActivity?.runOnUiThread {
                    oldestActivity.finish()
                }
            }
        }

        setContent {
            BVTheme(
                forceDark = true
            ) {
                VideoInfoScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // 当活动被销毁时，从队列中移除该Activity的引用
        synchronized(activityQueue) {
            val iterator = activityQueue.iterator()
            while (iterator.hasNext()) {
                val ref = iterator.next()
                if (ref.get() == this || ref.get() == null) {
                    iterator.remove()
                }
            }
        }
    }
}
