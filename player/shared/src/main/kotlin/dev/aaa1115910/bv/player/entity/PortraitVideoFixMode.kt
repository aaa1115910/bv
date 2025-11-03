package dev.aaa1115910.bv.player.entity

import android.content.Context
import dev.aaa1115910.bv.player.shared.R

/**
 * 竖屏视频修复模式。
 * - None: 不做任何处理
 * - LimitResolution1080P: 播放竖屏视频时自动限制分辨率不高于1080P
 * - UseTextureView: 强制使用 TextureView 渲染竖屏视频
 */
enum class PortraitVideoFixMode(val value: Int) {
    None(0),
    LimitResolution1080P(1),
    UseTextureView(2);

    fun displayName(context: Context): String = when (this) {
        None -> context.getString(R.string.pvf_mode_none)
        LimitResolution1080P -> context.getString(R.string.pvf_mode_limit_1080p)
        UseTextureView -> context.getString(R.string.pvf_mode_use_texture_view)
    }

    companion object {
        fun fromValue(value: Int): PortraitVideoFixMode = entries.find { it.value == value } ?: None
    }
}
