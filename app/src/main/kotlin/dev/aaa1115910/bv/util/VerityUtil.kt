package dev.aaa1115910.bv.util

import android.content.Context

object VerityUtil {
    inline fun hackCheck(context: Context, hacked: () -> Unit) {
        //assets 内默认只有[images, webkit]这两个，如果数量超过 2 那就是加固所添加的文件
        //println(context.assets.list("")?.toList())
        if ((context.assets.list("")?.size ?: 0) > 3) hacked()
    }
}