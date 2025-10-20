package dev.aaa1115910.bv.tv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.tv.screens.user.UpSpaceScreen
import dev.aaa1115910.bv.ui.theme.BVTheme
import java.lang.ref.WeakReference
import java.util.LinkedHashMap

class UpInfoActivity : ComponentActivity() {
    companion object {
        // 允许最多 N 个不同 mid 的页面共存
        private const val MAX_SCREENS = 2
    // LinkedHashMap 保持插入顺序：最早插入的条目位于 entrySet().iterator().next()
    private val activityByMid = LinkedHashMap<Long, WeakReference<UpInfoActivity>>()

        fun actionStart(context: Context, mid: Long, name: String, face: String) {
            if (mid <= 0) return
            context.startActivity(
                Intent(context, UpInfoActivity::class.java).apply {
                    putExtra("mid", mid)
                    putExtra("name", name)
                    putExtra("face", face)
                }
            )
        }
    }

    private var mid: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mid = intent.getLongExtra("mid", -1L)
        if (mid <= 0) {
            finish()
            return
        }

        synchronized(activityByMid) {
            // 清理失效引用
            val it = activityByMid.entries.iterator()
            while (it.hasNext()) {
                val entry = it.next()
                val act = entry.value.get()
                if (act == null || act.isFinishing) it.remove()
            }
            // 若已存在当前 mid，先关闭旧实例并移除，使重新插入刷新其“最近”位置
            activityByMid[mid]?.get()?.let { old ->
                if (old !== this && !old.isFinishing) old.finish()
            }
            activityByMid.remove(mid)
            activityByMid[mid] = WeakReference(this)

            // 超过最大不同 mid 数量：按插入顺序移除最早的非当前 mid
            while (activityByMid.size > MAX_SCREENS) {
                val iterator = activityByMid.entries.iterator()
                var removed = false
                while (iterator.hasNext()) {
                    val oldest = iterator.next()
                    if (oldest.key == mid) continue // 跳过当前，找真正最早的其它 mid
                    oldest.value.get()?.let { act ->
                        if (!act.isFinishing) act.finish()
                    }
                    iterator.remove()
                    removed = true
                    break
                }
                if (!removed) break // 只剩当前 mid
            }
        }

        setContent {
            BVTheme { UpSpaceScreen() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        synchronized(activityByMid) {
            val ref = activityByMid[mid]
            if (ref?.get() == this || ref?.get() == null) activityByMid.remove(mid)
            val it = activityByMid.entries.iterator()
            while (it.hasNext()) {
                val entry = it.next()
                val act = entry.value.get()
                if (act == null || act.isFinishing) it.remove()
            }
        }
    }
}
