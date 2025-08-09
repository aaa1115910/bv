package dev.aaa1115910.bv.tv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.tv.screens.user.UpSpaceScreen
import dev.aaa1115910.bv.ui.theme.BVTheme
import java.lang.ref.WeakReference

class UpInfoActivity : ComponentActivity() {
    companion object {
        private var currentInstance: WeakReference<UpInfoActivity>? = null

        fun actionStart(context: Context, mid: Long, name: String, face: String) {
            // 先关闭旧的播放页面
            currentInstance?.get()?.finish()

            context.startActivity(
                Intent(context, UpInfoActivity::class.java).apply {
                    putExtra("mid", mid)
                    putExtra("name", name)
                    putExtra("face", face)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置当前实例为弱引用
        currentInstance = WeakReference(this)

        setContent {
            BVTheme {
                UpSpaceScreen()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        // 清除当前实例引用
        if (currentInstance?.get() == this) {
            currentInstance = null
        }
    }
}
