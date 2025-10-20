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
import java.util.HashMap
import kotlin.math.roundToInt

/**
 * 优化版弹幕文字渲染器（单行文本场景）
 *
 * 优化策略：
 * 1. Paint 状态缓存：避免重复设置 textSize / typeface / color / shadow
 * 2. 最近一次测量缓存：避免同一弹幕短时间内重复测量
 * 3. 单次绘制 Shadow Outline（可选）：减少 overdraw，低端机可关闭
 * 4. 静态对象缓存：Typeface 存放 companion object，所有实例共享
 * 5. 预计算 halfPadding：避免 draw 方法中重复计算
 * 6. 分支优化：合并相关条件判断，减少分支预测失败
 * 7. 内联函数：热路径方法标记 inline，消除函数调用开销
 * 8. 字体度量缓存：size+bold 组合数有限（约74种），缓存 fontMetrics
 */
class OptimizedTextRenderer(
    private val useShadowOutline: Boolean = true,
    private val padding: Float = 10f
) : DanmakuRenderer {

    // Height + baseline metrics cache - 使用轻量 data class 作为 key
    // 理论最大：(12-48 共37种) × 2(bold) = 74 种组合
    private data class Metrics(val height: Float, val ascent: Float, val descent: Float, val baselineOffset: Float)
    private data class MetricsKey(val size: Float, val bold: Boolean)
    private val metricsCache = HashMap<MetricsKey, Metrics>() // 使用默认容量，自动扩容


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
    private var currentTextSizeScale = -1f
    private var dynamicPadding: Float = padding // 缓存缩放后的 padding，只在 scale 变化时重算
    
    // 预计算的半 padding，避免重复除法运算
    private var halfPadding: Float = padding * 0.5f

    // 最近一次测量缓存：避免同一弹幕短时间内重复测量（支持内容相同但引用不同）
    private var lastText: String = ""
    private var lastSize: Float = -1f
    private var lastBold: Boolean = false
    private var lastWidth: Float = -1f

    override fun updatePaint(item: DanmakuItem, displayer: DanmakuDisplayer, config: DanmakuConfig) {
        val d = item.data
        // 限制 size 范围并按密度缩放
        val baseSize = d.textSize.coerceIn(12, 48).toFloat() * (displayer.density - 0.6f)
        val finalSize = baseSize * config.textSizeScale
        val bold = config.bold
        
        // 合并判断减少分支预测失败
        val sizeOrBoldChanged = currentSize != finalSize || currentBold != bold
        
        if (sizeOrBoldChanged) {
            val tf = if (bold) TYPEFACE_BOLD else TYPEFACE_NORMAL
            textPaint.textSize = finalSize
            strokePaint.textSize = finalSize
            textPaint.typeface = tf
            strokePaint.typeface = tf
            currentSize = finalSize
            currentBold = bold
        }

        // 只在 textSizeScale 变化时更新 padding 相关值
        if (currentTextSizeScale != config.textSizeScale) {
            val scale = config.textSizeScale
            dynamicPadding = padding * scale
            halfPadding = dynamicPadding * 0.5f
            currentTextSizeScale = scale
        }

        // 前景颜色优化 - 使用位或运算确保不透明
        val textColor = d.textColor or Color.BLACK
        val textColorChanged = currentTextColor != textColor
        if (textColorChanged) {
            textPaint.color = textColor
            currentTextColor = textColor
        }
        
        // 直接计算 outlineColor，减少中间变量
        val outlineColor = if (textColor == DARK_COLOR) Color.WHITE else Color.BLACK
        val outlineColorChanged = currentStrokeColor != outlineColor
        if (outlineColorChanged) {
            strokePaint.color = outlineColor
            currentStrokeColor = outlineColor
        }

        // 动态描边宽度：根据字体大小计算，限制在合理范围
        val strokeW = (finalSize / 14f).coerceIn(2f, 4.5f)
        val strokeWidthChanged = currentStrokeWidth != strokeW
        if (strokeWidthChanged) {
            strokePaint.strokeWidth = strokeW
            currentStrokeWidth = strokeW
        }

        // Shadow outline：useShadowOutline 初始化后不变，false 时无需每次 clear
        // 优化：只在相关属性变化时更新阴影
        if (useShadowOutline && (sizeOrBoldChanged || outlineColorChanged || strokeWidthChanged)) {
            textPaint.setShadowLayer(strokeW * 0.9f, 0f, 0f, outlineColor)
        }
    }

    override fun measure(item: DanmakuItem, displayer: DanmakuDisplayer, config: DanmakuConfig): Size {
        updatePaint(item, displayer, config)
        val data = item.data
        // 直接使用缓存的状态，避免重复访问 Paint 字段
        val width = fastWidthLookup(data.content, currentSize, currentBold)
        val metrics = obtainMetrics(currentSize, currentBold)
        val totalW = (width + dynamicPadding).roundToInt()
        val totalH = (metrics.height + dynamicPadding).roundToInt()
        return Size(totalW, totalH)
    }

    override fun draw(item: DanmakuItem, canvas: Canvas, displayer: DanmakuDisplayer, config: DanmakuConfig) {
        updatePaint(item, displayer, config)
        val d = item.data
        // 优化：使用缓存的状态和预计算的 halfPadding
        val metrics = obtainMetrics(currentSize, currentBold)
        val x = halfPadding
        val baseline = halfPadding + metrics.baselineOffset
        
        if (useShadowOutline) {
            // 单次绘制（shadow模拟描边）
            canvas.drawText(d.content, x, baseline, textPaint)
        } else {
            // 传统双重绘制
            canvas.drawText(d.content, x, baseline, strokePaint)
            canvas.drawText(d.content, x, baseline, textPaint)
        }
        
        // 优化：该条件通常为 false，放在最后减少分支预测失败影响
        if (d.danmakuStyle == DanmakuItemData.DANMAKU_STYLE_SELF_SEND) {
            // 自发弹幕边框 - 注意：strokePaint 默认就是 STROKE
            val savedStyle = strokePaint.style
            strokePaint.style = Paint.Style.STROKE
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), strokePaint)
            strokePaint.style = savedStyle
        }
    }

    // ---- Internal Helpers ----
    
    /**
     * 简洁高效的宽度查找：最近一次测量缓存 + 直接测量
     * Level 1: 检查是否与上次测量相同（内容相等比较，支持不同引用）
     * Level 2: 直接测量（measureText 本身很快）
     */
    private inline fun fastWidthLookup(text: String, size: Float, bold: Boolean): Float {
        // Level 1: 最近一次测量缓存（支持内容相同但引用不同）
        if (text == lastText && size == lastSize && bold == lastBold) {
            return lastWidth
        }
        
        // Level 2: 直接测量
        val measured = textPaint.measureText(text)
        updateLastLookup(text, size, bold, measured)
        return measured
    }
    
    /**
     * 更新最近查找记录 - inline 消除函数调用开销
     */
    private inline fun updateLastLookup(text: String, size: Float, bold: Boolean, width: Float) {
        lastText = text
        lastSize = size
        lastBold = bold
        lastWidth = width
    }
    
    /**
     * 获取字体度量信息 - 简单高效的 data class key 方案
     */
    private inline fun obtainMetrics(size: Float, bold: Boolean): Metrics {
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

    companion object {
        private val DARK_COLOR = Color.argb(255, 0x22, 0x22, 0x22)
        
        // 静态 Typeface 缓存：所有实例共享，节省内存
        private val TYPEFACE_NORMAL = Typeface.DEFAULT
        private val TYPEFACE_BOLD = Typeface.DEFAULT_BOLD
        
        /**
         * 创建高性能配置的渲染器实例
         */
        @JvmStatic
        fun createHighPerformance(): OptimizedTextRenderer {
            return OptimizedTextRenderer(
                useShadowOutline = true,
                padding = 10f
            )
        }
    }
}
