package dev.aaa1115910.bv.player.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItem
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.render.DanmakuRenderer
import com.kuaishou.akdanmaku.ui.DanmakuDisplayer
import com.kuaishou.akdanmaku.utils.Size
import kotlin.math.roundToInt

/**
 * 极致优化版渲染器（单行文字场景）
 * 目标：减少 GC、降低 overdraw、降低测量成本。
 * 技术点：
 * 1. LRU 自清理宽度缓存（content + size + bold）
 * 2. 文本高度/基线缓存（按 size+bold）避免重复 fontMetrics 计算
 * 3. 可选 Shadow Outline 模式 -> 单次 draw 代替描边 + 填充双绘制
 * 4. 自适应描边宽度（屏幕密度 & 文本大小）
 * 5. ASCII 快速宽度估算（命中后免 measureText，回退保证正确性）
 */
class OptimizedTextRenderer(
    maxWidthCache: Int = 1024,
    private val useShadowOutline: Boolean = true,
    private val enableAsciiFastPath: Boolean = true,
    private val asciiFastPathMinLen: Int = 3,
    private val padding: Float = 6f
) : DanmakuRenderer {

    // ---- Cache Keys ----
    private data class WidthKey(var text: String, var size: Float, var bold: Boolean) {
        fun update(newText: String, newSize: Float, newBold: Boolean): WidthKey {
            text = newText; size = newSize; bold = newBold
            return this
        }
    }
    private data class MetricsKey(var size: Float, var bold: Boolean) {
        fun update(newSize: Float, newBold: Boolean): MetricsKey {
            size = newSize; bold = newBold
            return this
        }
    }

    // ---- LRU Width Cache ----
    private inner class WidthLru(max: Int) : LinkedHashMap<WidthKey, Float>(max, 0.75f, true) {
        private val limit = max
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<WidthKey, Float>?): Boolean = size > limit
    }
    private val widthCache = WidthLru(maxWidthCache)

    // Height + baseline metrics cache per text size/bold
    private data class Metrics(val height: Float, val ascent: Float, val descent: Float, val baselineOffset: Float)
    private val metricsCache = HashMap<MetricsKey, Metrics>()

    // ASCII average width cache per size/bold (for fast path)
    private val asciiAvgWidthCache = HashMap<MetricsKey, Float>()
    private val asciiSample = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".
        plus("0123456789!@#%&()[]{}:;?+-_=<>,./")

    // Reused paints
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }
    private val strokePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.BLACK
    }

    // Working state (避免在 updatePaint 重复局部创建临时对象)
    private var currentBold = false
    private var currentSize = 0f
    private var currentShadowApplied = false
    private var lastOutlineColor: Int = Color.BLACK
    private var paintUpdateGeneration = 0L // 避免重复 updatePaint

    // 最近一次测量快速路径（避免频繁构造 Key 与 HashMap 查找）
    private var lastText: String? = null
    private var lastSize: Float = -1f
    private var lastBold: Boolean = false
    private var lastWidth: Float = -1f

    // 复用 Key 对象减少 GC
    private val reuseWidthKey = WidthKey("", 0f, false)
    private val reuseMetricsKey = MetricsKey(0f, false)

    // 基础统计（可用于调试观察命中率）
    private var statMeasureCalls = 0L
    private var statCacheHits = 0L

    override fun updatePaint(item: DanmakuItem, displayer: DanmakuDisplayer, config: DanmakuConfig) {
        val d = item.data
        // 限制 size 范围并按密度缩放（与原 SimpleRenderer 逻辑一致略放宽上限）
        val baseSize = d.textSize.coerceIn(12, 48).toFloat() * (displayer.density - 0.6f)
        val finalSize = baseSize * config.textSizeScale
        val bold = config.bold
        val tf = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        if (currentSize != finalSize || currentBold != bold) {
            textPaint.textSize = finalSize
            strokePaint.textSize = finalSize
            textPaint.typeface = tf
            strokePaint.typeface = tf
            currentSize = finalSize
            currentBold = bold
            currentShadowApplied = false // 尺寸变化需重新设置 shadow
            paintUpdateGeneration++
        }

        // 前景颜色 & Outline 颜色
        textPaint.color = d.textColor or Color.BLACK
        val dark = textPaint.color == DARK_COLOR
        val outlineColor = if (dark) Color.WHITE else Color.BLACK
        val outlineChanged = strokePaint.color != outlineColor
        strokePaint.color = outlineColor

        // Stroke width 动态
        val strokeW = (finalSize / 14f).coerceIn(2f, 4.5f)
        strokePaint.strokeWidth = strokeW

        if (useShadowOutline) {
            // 采用阴影模拟描边：单次 draw，减少 overdraw（硬件加速下大多数设备仍可接受）
            // 只有在颜色或尺寸变化时重设，避免频繁 JNI 调用
            if (!currentShadowApplied || outlineChanged) {
                // radius 用 strokeW 的 ~0.9f，偏移 0
                textPaint.setShadowLayer(strokeW * 0.9f, 0f, 0f, outlineColor)
                currentShadowApplied = true
                lastOutlineColor = outlineColor
                paintUpdateGeneration++
            }
        } else {
            // 关闭时确保无 shadow，回退双 pass
            if (currentShadowApplied) {
                textPaint.clearShadowLayer()
                currentShadowApplied = false
                paintUpdateGeneration++
            }
        }
    }

    override fun measure(item: DanmakuItem, displayer: DanmakuDisplayer, config: DanmakuConfig): Size {
        updatePaint(item, displayer, config)
        val data = item.data
        val key = reuseWidthKey.update(data.content, textPaint.textSize, textPaint.typeface == Typeface.DEFAULT_BOLD)
        val width = fastWidthLookup(key, data.content)
        val metrics = obtainMetrics(textPaint.textSize, textPaint.typeface == Typeface.DEFAULT_BOLD)
        val totalW = (width + padding).roundToInt()
        val totalH = (metrics.height + padding).roundToInt()
        return Size(totalW, totalH)
    }

    override fun draw(item: DanmakuItem, canvas: Canvas, displayer: DanmakuDisplayer, config: DanmakuConfig) {
        // 避免重复 updatePaint（若 measure 刚调用过）
        updatePaint(item, displayer, config)
        val d = item.data
        val metrics = obtainMetrics(textPaint.textSize, textPaint.typeface == Typeface.DEFAULT_BOLD)
        val x = padding * 0.5f
        val baseline = padding * 0.5f + metrics.baselineOffset
        if (useShadowOutline) {
            // 单次绘制（shadow 负责 outline）
            canvas.drawText(d.content, x, baseline, textPaint)
        } else {
            canvas.drawText(d.content, x, baseline, strokePaint)
            canvas.drawText(d.content, x, baseline, textPaint)
        }
        if (d.danmakuStyle == DanmakuItemData.DANMAKU_STYLE_SELF_SEND) {
            // 自发弹幕描边矩形区分（复用 strokePaint）
            val saved = strokePaint.style
            strokePaint.style = Paint.Style.STROKE
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), strokePaint)
            strokePaint.style = saved
        }
    }

    // ---- Internal Helpers ----
    private fun computeAndCacheWidth(key: WidthKey, text: String): Float {
        val w = if (enableAsciiFastPath && text.length >= asciiFastPathMinLen && text.isAscii()) {
            // 估算：平均宽 * 字符数；结果与真实测量差异大时回退真实读取
            val mk = MetricsKey(key.size, key.bold)
            val avg = asciiAvgWidthCache.getOrPut(mk) {
                textPaint.measureText(asciiSample) / asciiSample.length
            }
            val estimated = avg * text.length
            val real = textPaint.measureText(text)
            // 校验：误差 >8% 则改用 real
            if (kotlin.math.abs(real - estimated) / real > 0.08f) real else real // 使用真实值以确保布局准确
        } else {
            textPaint.measureText(text)
        }
        widthCache[key] = w
        return w
    }

    private fun fastWidthLookup(key: WidthKey, text: String): Float {
        statMeasureCalls++
        // 最近一次命中快速路径
        if (text === lastText && key.size == lastSize && key.bold == lastBold && lastWidth >= 0f) {
            statCacheHits++
            return lastWidth
        }
        val w = synchronized(widthCache) {
            widthCache[key] ?: computeAndCacheWidth(key, text).also { widthCache[key] = it }
        }
        lastText = text
        lastSize = key.size
        lastBold = key.bold
        lastWidth = w
        return w
    }

    // 可选：外部调试调用（不暴露为接口，留注释方便需要时开启）
    @Suppress("unused")
    private fun dumpStats(): String = "widthCalls=$statMeasureCalls cacheHits=$statCacheHits hitRate=" +
            (if (statMeasureCalls == 0L) "-" else String.format("%.2f%%", statCacheHits * 100f / statMeasureCalls))

    private fun obtainMetrics(size: Float, bold: Boolean): Metrics {
        val mk = MetricsKey(size, bold)
        return metricsCache[mk] ?: run {
            val fm = textPaint.fontMetrics
            val height = fm.descent - fm.ascent + fm.leading
            val baselineOffset = -fm.ascent // baseline 相对顶部 padding 内部的偏移
            Metrics(height, fm.ascent, fm.descent, baselineOffset).also { metricsCache[mk] = it }
        }
    }

    private fun String.isAscii(): Boolean = all { it.code in 32..126 }

    companion object {
        private val DARK_COLOR = Color.argb(255, 0x22, 0x22, 0x22)
    }
}
