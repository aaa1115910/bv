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
import java.util.LinkedHashMap
import java.util.HashMap
import kotlin.math.roundToInt

/**
 * 实用优化版弹幕文字渲染器（单行文本场景）
 *
 * 设计原则：
 * - 优先“稳定 + 可读 + 真正收益”而非激进/复杂的“理论优化”。
 * - 针对弹幕文本长度短、重复率有限的特征做轻量缓存，而不是过度工程化。
 *
 * 优化：
 * 1. Paint 状态缓存：避免重复设置 textSize / typeface / color / shadow。
 * 2. FontMetrics 缓存：按 (size,bold) 维度缓存高度与 baseline。
 * 3. 轻量 LRU 宽度缓存：LinkedHashMap(accessOrder=true) 自动淘汰最旧项。
 * 4. 单次绘制 Shadow Outline（可选）：减少 overdraw；低端机可关闭回退双 pass。
 * 5. 最近一次请求快速路径：相同 (内容,size,bold) 直接返回宽度。
 *
 * 可选策略：skipCacheBelowLength，可在极短文本场景直接 measureText()（默认关闭）。
 */
class OptimizedTextRenderer(
    maxWidthCache: Int = 512,
    private val useShadowOutline: Boolean = true,
    private val padding: Float = 6f,
    private val enablePerformanceMonitor: Boolean = false,
    private val skipCacheBelowLength: Int = 0 // 可选：对极短文本直接测量（0 表示不跳过）
) : DanmakuRenderer {

    /**
     * 轻量 LRU：LinkedHashMap(accessOrder = true) + 数据类 Key，简单、可靠、可读性高。
     */
    private data class WidthKey(val text: String, val size: Float, val bold: Boolean)

    private val widthCache: LinkedHashMap<WidthKey, Float> = object : LinkedHashMap<WidthKey, Float>(maxWidthCache, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<WidthKey, Float>?): Boolean = size > maxWidthCache
    }

    // Height + baseline metrics cache per text size/bold
    private data class Metrics(val height: Float, val ascent: Float, val descent: Float, val baselineOffset: Float)
    private data class MetricsKey(val size: Float, val bold: Boolean)
    private val metricsCache = HashMap<MetricsKey, Metrics>()


    // Reused paints with state caching
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }
    private val strokePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.BLACK
    }

    // Paint状态缓存 (避免重复JNI调用)
    private var currentBold = false
    private var currentSize = 0f
    private var currentTextColor = Color.WHITE
    private var currentStrokeColor = Color.BLACK
    private var currentStrokeWidth = 3f
    private var currentShadowApplied = false

    // 超快速路径：最近一次完全相同的测量请求（避免频繁构造 Key 与 HashMap 查找）
    private var lastTextHash: Int = 0
    private var lastSize: Float = -1f
    private var lastBold: Boolean = false
    private var lastWidth: Float = -1f

    // 性能监控（简化）
    private var statMeasureCalls = 0L
    private var statCacheHits = 0L
    private var lastPerformanceReport = System.currentTimeMillis()

    override fun updatePaint(item: DanmakuItem, displayer: DanmakuDisplayer, config: DanmakuConfig) {
        val d = item.data
        // 限制 size 范围并按密度缩放
        val baseSize = d.textSize.coerceIn(12, 48).toFloat() * (displayer.density - 0.6f)
        val finalSize = baseSize * config.textSizeScale
        val bold = config.bold
        
        // 只有在必要时才更新Paint属性
        var needsUpdate = false
        
        if (currentSize != finalSize || currentBold != bold) {
            val tf = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            textPaint.textSize = finalSize
            strokePaint.textSize = finalSize
            textPaint.typeface = tf
            strokePaint.typeface = tf
            currentSize = finalSize
            currentBold = bold
            currentShadowApplied = false
            needsUpdate = true
        }

        // 前景颜色优化 - 避免重复设置
        val textColor = d.textColor or Color.BLACK
        if (currentTextColor != textColor) {
            textPaint.color = textColor
            currentTextColor = textColor
            needsUpdate = true
        }
        
        val dark = textColor == DARK_COLOR
        val outlineColor = if (dark) Color.WHITE else Color.BLACK
        if (currentStrokeColor != outlineColor) {
            strokePaint.color = outlineColor
            currentStrokeColor = outlineColor
            needsUpdate = true
        }

        // 动态描边宽度优化
        val strokeW = (finalSize / 14f).coerceIn(2f, 4.5f)
        if (currentStrokeWidth != strokeW) {
            strokePaint.strokeWidth = strokeW
            currentStrokeWidth = strokeW
            needsUpdate = true
        }

        // Shadow outline 优化
        if (useShadowOutline) {
            if (!currentShadowApplied || needsUpdate) {
                textPaint.setShadowLayer(strokeW * 0.9f, 0f, 0f, outlineColor)
                currentShadowApplied = true
                needsUpdate = true
            }
        } else if (currentShadowApplied) {
            textPaint.clearShadowLayer()
            currentShadowApplied = false
            needsUpdate = true
        }
    }

    override fun measure(item: DanmakuItem, displayer: DanmakuDisplayer, config: DanmakuConfig): Size {
        updatePaint(item, displayer, config)
        val data = item.data
        val textSize = textPaint.textSize
        val bold = textPaint.typeface == Typeface.DEFAULT_BOLD
        
    val width = fastWidthLookup(data.content, textSize, bold)
        val metrics = obtainMetrics(textSize, bold)
        val totalW = (width + padding).roundToInt()
        val totalH = (metrics.height + padding).roundToInt()
        return Size(totalW, totalH)
    }

    override fun draw(item: DanmakuItem, canvas: Canvas, displayer: DanmakuDisplayer, config: DanmakuConfig) {
        // 只有在必要时才调用updatePaint（通常measure已经调用过）
        updatePaint(item, displayer, config)
        val d = item.data
        val metrics = obtainMetrics(textPaint.textSize, textPaint.typeface == Typeface.DEFAULT_BOLD)
        val x = padding * 0.5f
        val baseline = padding * 0.5f + metrics.baselineOffset
        
        if (useShadowOutline) {
            // 单次绘制（shadow模拟描边）
            canvas.drawText(d.content, x, baseline, textPaint)
        } else {
            // 传统双重绘制
            canvas.drawText(d.content, x, baseline, strokePaint)
            canvas.drawText(d.content, x, baseline, textPaint)
        }
        
        if (d.danmakuStyle == DanmakuItemData.DANMAKU_STYLE_SELF_SEND) {
            // 自发弹幕边框
            val savedStyle = strokePaint.style
            strokePaint.style = Paint.Style.STROKE
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), strokePaint)
            strokePaint.style = savedStyle
        }
    }

    // ---- Internal Helpers ----
    
    /**
     * 简化的宽度查找：保持简洁高效
     * 1. 快速路径：完全相同的文本+参数（避免hash和缓存查找）
     * 2. 缓存路径：无锁缓存查找
     * 3. 直接测量：measureText本身很快，不需要复杂的预计算
     */
    private fun fastWidthLookup(text: String, size: Float, bold: Boolean): Float {
        statMeasureCalls++
        reportPerformanceIfNeeded()
        
        // 快速路径：完全相同的请求
        val textHash = text.hashCode()
        if (textHash == lastTextHash && size == lastSize && bold == lastBold && lastWidth >= 0f) {
            return lastWidth
        }
        if (skipCacheBelowLength > 0 && text.length < skipCacheBelowLength) {
            val w = textPaint.measureText(text)
            updateLastLookup(textHash, size, bold, w)
            return w
        }
        val key = WidthKey(text, size, bold)
        widthCache[key]?.let { cached ->
            statCacheHits++
            updateLastLookup(textHash, size, bold, cached)
            return cached
        }
        val measured = textPaint.measureText(text)
        widthCache[key] = measured
        updateLastLookup(textHash, size, bold, measured)
        return measured
    }
    
    private fun updateLastLookup(textHash: Int, size: Float, bold: Boolean, width: Float) {
        lastTextHash = textHash
        lastSize = size
        lastBold = bold
        lastWidth = width
    }
    
    private fun obtainMetrics(size: Float, bold: Boolean): Metrics {
        val key = MetricsKey(size, bold)
        return metricsCache[key] ?: run {
            val fm = textPaint.fontMetrics
            val height = fm.descent - fm.ascent + fm.leading
            val baselineOffset = -fm.ascent
            val m = Metrics(height, fm.ascent, fm.descent, baselineOffset)
            metricsCache[key] = m
            m
        }
    }
    
    /**
     * 性能统计报告（简化版）
     */
    @Suppress("unused")
    private fun dumpPerformanceStats(): String {
        val hitRate = if (statMeasureCalls == 0L) 0f else 
            statCacheHits * 100f / statMeasureCalls
        return "MeasureCalls:$statMeasureCalls CacheHits:$statCacheHits " +
                "HitRate:${String.format("%.1f%%", hitRate)}"
    }
    
    /**
     * 定期输出性能报告（如果启用）
     */
    private fun reportPerformanceIfNeeded() {
        if (!enablePerformanceMonitor) return
        val now = System.currentTimeMillis()
        if (now - lastPerformanceReport > 10000) { // 每10秒报告一次
            println("[OptimizedTextRenderer] ${dumpPerformanceStats()}")
            lastPerformanceReport = now
        }
    }

    companion object {
        private val DARK_COLOR = Color.argb(255, 0x22, 0x22, 0x22)
        
        /**
         * 创建高性能配置的渲染器实例（简化版）
         */
        @JvmStatic
        fun createHighPerformance(): OptimizedTextRenderer {
            return OptimizedTextRenderer(
                maxWidthCache = 2048,
                useShadowOutline = true,
                padding = 6f,
                enablePerformanceMonitor = false
            )
        }
        
        /**
         * 创建调试模式的渲染器实例（带性能监控）
         */
        @JvmStatic
        fun createWithMonitoring(): OptimizedTextRenderer {
            return OptimizedTextRenderer(
                maxWidthCache = 1024,
                useShadowOutline = true,
                padding = 6f,
                enablePerformanceMonitor = true
            )
        }
    }
}
