package dev.aaa1115910.bv.player.renderer

import com.kuaishou.akdanmaku.render.DanmakuRenderer
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.core.math.MathUtils.clamp
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItem
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ui.DanmakuDisplayer
import com.kuaishou.akdanmaku.utils.Size
import java.util.HashMap
import kotlin.math.roundToInt

/**
 * 一个默认的，实现了简单只绘制文字和描边的弹幕渲染器
 *
 * @author Xana
 */
open class SimpleRenderer : DanmakuRenderer {

    // 缓存当前的 Paint 配置，避免重复设置（使用原始类型避免对象创建）
    private var cachedTextSize: Float = -1f
    private var cachedTextColor: Int = 0
    private var cachedTextSizeScale: Float = -1f
    private var cachedBold: Boolean = false
    private var cachedDensity: Float = -1f

    private val textPaint = TextPaint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val strokePaint = TextPaint().apply {
        textSize = textPaint.textSize
        color = Color.BLACK
        strokeWidth = 3f
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }
    private val debugPaint by lazy {
        Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = 6f
        }
    }
    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = 6f
    }

    override fun updatePaint(
        item: DanmakuItem,
        displayer: DanmakuDisplayer,
        config: DanmakuConfig
    ) {
        val danmakuItemData = item.data
        val textSize = clamp(danmakuItemData.textSize.toFloat(), 12f, 30f)
        val textColor = danmakuItemData.textColor or Color.argb(255, 0, 0, 0)

        // 直接比较缓存值，避免创建对象
        if (textSize == cachedTextSize &&
            textColor == cachedTextColor &&
            config.textSizeScale == cachedTextSizeScale &&
            config.bold == cachedBold &&
            displayer.density == cachedDensity
        ) {
            return
        }

        // 更新缓存
        cachedTextSize = textSize
        cachedTextColor = textColor
        cachedTextSizeScale = config.textSizeScale
        cachedBold = config.bold
        cachedDensity = displayer.density

        // update textPaint
        val finalTextSize = textSize * (displayer.density - 0.6f) * config.textSizeScale
        textPaint.color = textColor
        textPaint.textSize = finalTextSize
        textPaint.typeface = if (config.bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        // update strokePaint
        strokePaint.textSize = finalTextSize
        strokePaint.typeface = textPaint.typeface
        strokePaint.color = if (textColor == DEFAULT_DARK_COLOR) Color.WHITE else Color.BLACK
    }

    override fun measure(
        item: DanmakuItem,
        displayer: DanmakuDisplayer,
        config: DanmakuConfig
    ): Size {
        updatePaint(item, displayer, config)
        val danmakuItemData = item.data
        val textWidth = textPaint.measureText(danmakuItemData.content)
        val textHeight = getCacheHeight(textPaint)
        val canvasPadding = (CANVAS_PADDING * config.textSizeScale).roundToInt()
        return Size(textWidth.roundToInt() + canvasPadding, textHeight.roundToInt() + canvasPadding)
    }

    override fun draw(
        item: DanmakuItem,
        canvas: Canvas,
        displayer: DanmakuDisplayer,
        config: DanmakuConfig
    ) {
        updatePaint(item, displayer, config)
        val danmakuItemData = item.data
        val canvasPadding = CANVAS_PADDING * config.textSizeScale * 0.5f
        val x = canvasPadding
        val y = canvasPadding - textPaint.ascent()
        canvas.drawText(danmakuItemData.content, x, y, strokePaint)
        canvas.drawText(danmakuItemData.content, x, y, textPaint)
        if (danmakuItemData.danmakuStyle == DanmakuItemData.DANMAKU_STYLE_SELF_SEND) {
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), borderPaint)
        }
    }

    companion object {
        private val DEFAULT_DARK_COLOR: Int = Color.argb(255, 0x22, 0x22, 0x22)

        private const val CANVAS_PADDING: Int = 10

        private val sTextHeightCache: MutableMap<Float, Float> = HashMap()

        private fun getCacheHeight(paint: Paint): Float {
            val textSize = paint.textSize
            return sTextHeightCache[textSize] ?: let {
                val fontMetrics = paint.fontMetrics
                val textHeight = fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading
                sTextHeightCache[textSize] = textHeight
                textHeight
            }
        }
    }
}
