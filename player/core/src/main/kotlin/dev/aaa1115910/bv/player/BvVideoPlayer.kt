package dev.aaa1115910.bv.player

import android.graphics.Matrix
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.bv.player.impl.exo.ExoMediaPlayer
import io.github.oshai.kotlinlogging.KotlinLogging.logger

@OptIn(UnstableApi::class)
@Composable
fun BvVideoPlayer(
    modifier: Modifier = Modifier,
    videoPlayer: AbstractVideoPlayer,
    playerListener: VideoPlayerListener,
    rotationDegrees: Float = 0f, // 新增参数，视频旋转角度
    danmakuPlayer: DanmakuPlayer? = null,
    forceUseTextureView: Boolean = false,
) {
    val logger = logger("BvVideoPlayer")
    val context = LocalContext.current
    val density = LocalDensity.current
    val screenHeight = with(density) { context.resources.displayMetrics.heightPixels.toFloat() }
    val screenWidth = with(density) { context.resources.displayMetrics.widthPixels.toFloat() }


    DisposableEffect(Unit) {
        videoPlayer.setPlayerEventListener(playerListener)

        onDispose {
            videoPlayer.release()
        }
    }

    when (videoPlayer) {
        is ExoMediaPlayer -> {
            var textureView: TextureView? by remember { mutableStateOf(null) }
            var surfaceView: SurfaceView? by remember { mutableStateOf(null) }
            var lastRotationDegrees by remember { mutableFloatStateOf(rotationDegrees) }

            fun clearVideoView() {
                surfaceView?.let {
                    videoPlayer.mPlayer?.clearVideoSurfaceView(it)
                }
                textureView?.let {
                    videoPlayer.mPlayer?.clearVideoTextureView(it)
                }
            }

            if (forceUseTextureView || rotationDegrees != 0f) {
                fun applyTextureTransform(tv: TextureView?, degreesRaw: Float) {
                    tv ?: return
                    if (rotationDegrees != lastRotationDegrees) {
                        val time = videoPlayer.currentPosition
                        videoPlayer.stop()
                        tv.postDelayed({
                            val viewWidth = tv.width.toFloat()
                            val viewHeight = tv.height.toFloat()

                            val pivotX = viewWidth / 2f
                            val pivotY = viewHeight / 2f
                            val matrix = Matrix().apply {
                                // 旋转
                                setRotate(rotationDegrees, pivotX, pivotY)

                                if (rotationDegrees == 90f || rotationDegrees == -90f) {
                                    // 缩放（使内容适配反转后的宽高比）
                                    val scale = minOf(
                                        screenHeight / viewWidth,
                                        screenWidth / viewHeight
                                    )
                                    // 以中心缩放，需先将缩放偏移到中心
                                    postScale(scale, scale, pivotX, pivotY)
                                }
                            }
                            tv.setTransform(matrix)

                            videoPlayer.mPlayer?.setVideoTextureView(tv)
                            videoPlayer.prepare()
                            videoPlayer.seekTo(time)
                            danmakuPlayer?.seekTo(time)
                            danmakuPlayer?.pause()
                            videoPlayer.start()

                            lastRotationDegrees = rotationDegrees
                        }, 80)
                    }
                }

                AndroidView(
                    modifier = modifier.fillMaxSize(),
                    factory = { ctx ->
                        clearVideoView()
                        TextureView(ctx).also { tv ->
                            textureView = tv
                            videoPlayer.mPlayer?.setVideoTextureView(tv)
                            // 切换到 TextureView 当帧即尝试应用旋转
                            applyTextureTransform(tv, rotationDegrees)

                            logger.info { "Current view type is TextureView" }
                        }
                    },
                    update = { tv ->
                        applyTextureTransform(tv, rotationDegrees)
                    }
                )
            } else {
                // SurfaceView 渲染
                AndroidView(
                    modifier = modifier.fillMaxSize(),
                    factory = { ctx ->
                        clearVideoView()
                        SurfaceView(ctx).also { sv ->
                            surfaceView = sv
                            videoPlayer.mPlayer?.setVideoSurfaceView(sv)

                            logger.info { "Current view type is SurfaceView" }
                        }
                    }
                )
            }

            DisposableEffect(videoPlayer) {
                onDispose {
                    clearVideoView()
                    textureView = null
                    surfaceView = null
                }
            }
        }
    }
}