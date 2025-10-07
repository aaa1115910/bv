package dev.aaa1115910.bv.player.impl.exo

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.OkHttpUtil
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.util.formatHourMinSec

/**
 * 智能缓冲配置
 */
private data class BufferConfig(
    val minBufferMs: Int,
    val maxBufferMs: Int,
    val backBufferMs: Int,
    val targetBufferBytes: Int,
    val prioritizeTime: Boolean // 是否优先考虑时间阈值
)

@OptIn(UnstableApi::class)
class ExoMediaPlayer(
    private val context: Context,
    private val options: VideoPlayerOptions
) : AbstractVideoPlayer(), Player.Listener {
    var mPlayer: ExoPlayer? = null
    protected var mMediaSource: MediaSource? = null

    @OptIn(UnstableApi::class)
    private val dataSourceFactory =
        OkHttpDataSource.Factory(OkHttpUtil.generateCustomSslOkHttpClient(context)).apply {
            options.userAgent?.let { setUserAgent(it) }
            options.referer?.let { setDefaultRequestProperties(mapOf("referer" to it)) }
        }

    init {
        initPlayer()
    }

    @OptIn(UnstableApi::class)
    override fun initPlayer() {
        val renderersFactory = DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(
                when (options.enableFfmpegAudioRenderer) {
                    true -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
                    false -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
                }
            )
        }

        // 创建智能缓冲策略，根据设备性能和视频质量动态调整
        val bufferConfig = calculateSmartBufferConfig()
        val loadControl = DefaultLoadControl.Builder()
            // 动态设置缓冲区大小
            .setBufferDurationsMs(
                bufferConfig.minBufferMs, // 最小缓冲时间
                bufferConfig.maxBufferMs, // 最大缓冲时间
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, // 开始播放前的缓冲时间
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS // 重新缓冲后的播放缓冲
            )
            // 优先考虑时间阈值还是缓冲大小。true：优先考虑时间阈值
            .setPrioritizeTimeOverSizeThresholds(bufferConfig.prioritizeTime)
            // 根据系统内存计算缓冲区大小
            .setTargetBufferBytes(bufferConfig.targetBufferBytes)
            .setBackBuffer(bufferConfig.backBufferMs, false) // 动态回退缓冲
            .build()

        mPlayer = ExoPlayer
            .Builder(context)
            .setRenderersFactory(renderersFactory)
            .setLoadControl(loadControl)
            .setSeekForwardIncrementMs(1000 * 10)
            .setSeekBackIncrementMs(1000 * 10)
            .build()

        initListener()
    }

    private fun initListener() {
        mPlayer?.addListener(this)
    }

    @OptIn(UnstableApi::class)
    override fun setHeader(headers: Map<String, String>) {

    }

    @OptIn(UnstableApi::class)
    override fun playUrl(videoUrl: String?, audioUrl: String?) {
        val videoMediaSource = videoUrl?.let {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(it))
        }
        val audioMediaSource = audioUrl?.let {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(it))
        }

        val mediaSources = listOfNotNull(videoMediaSource, audioMediaSource)
        mMediaSource = MergingMediaSource(*mediaSources.toTypedArray())
    }

    @OptIn(UnstableApi::class)
    override fun prepare() {
        mPlayer?.setMediaSource(mMediaSource!!)
        mPlayer?.prepare()
    }

    override fun start() {
        mPlayer?.play()
    }

    override fun pause() {
        mPlayer?.pause()
    }

    override fun stop() {
        mPlayer?.stop()
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override val isPlaying: Boolean
        get() = mPlayer?.isPlaying == true

    override fun seekTo(time: Long) {
        mPlayer?.seekTo(time)
    }

    override fun release() {
        try {
            mPlayer?.release()
            mMediaSource = null
            mPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val currentPosition: Long
        get() = mPlayer?.currentPosition ?: 0
    override val duration: Long
        get() = mPlayer?.duration ?: 0
    override val bufferedPercentage: Int
        get() = mPlayer?.bufferedPercentage ?: 0

    override fun setOptions() {
        mPlayer?.playWhenReady = true
    }

    override var speed: Float
        get() = mPlayer?.playbackParameters?.speed ?: 1f
        set(value) {
            mPlayer?.setPlaybackSpeed(value)
        }
    override val tcpSpeed: Long
        get() = 0L

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> mPlayerEventListener?.onIdle()
            Player.STATE_BUFFERING -> mPlayerEventListener?.onBuffering()
            Player.STATE_READY -> mPlayerEventListener?.onReady()
            Player.STATE_ENDED -> mPlayerEventListener?.onEnd()
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            mPlayerEventListener?.onPlay()
        } else {
            mPlayerEventListener?.onPause()
        }
    }

    override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
        mPlayerEventListener?.onSeekBack(seekBackIncrementMs)
    }

    override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
        mPlayerEventListener?.onSeekForward(seekForwardIncrementMs)
    }

    override val debugInfo: String
        get() {
            return """
                player: ${androidx.media3.common.MediaLibraryInfo.VERSION_SLASHY}
                time: ${currentPosition.formatHourMinSec()} / ${duration.formatHourMinSec()}
                buffered: $bufferedPercentage%
                resolution: ${mPlayer?.videoSize?.width} x ${mPlayer?.videoSize?.height}
                audio: ${mPlayer?.audioFormat?.bitrate ?: 0} kbps
                video codec: ${mPlayer?.videoFormat?.sampleMimeType ?: "null"}
                audio codec: ${mPlayer?.audioFormat?.sampleMimeType ?: "null"} (${getAudioRendererName()})
            """.trimIndent()
        }

    private fun getAudioRendererName(): String {
        val rendererCount = mPlayer?.rendererCount ?: return "UnknownRenderer"
        for (i in 0 until rendererCount) {
            val renderer = mPlayer!!.getRenderer(i)
            if (renderer.trackType == C.TRACK_TYPE_AUDIO && renderer.state == Renderer.STATE_STARTED) {
                return renderer.name
            }
        }
        return "UnknownRenderer"
    }

    override val videoWidth: Int
        get() = mPlayer?.videoSize?.width ?: 0
    override val videoHeight: Int
        get() = mPlayer?.videoSize?.height ?: 0

    override fun onPlayerError(error: PlaybackException) {
        mPlayerEventListener?.onError(error)
    }

    /**
     * 计算智能缓冲配置
     * 根据设备性能、可用内存和预期视频质量动态调整缓冲策略
     */
    private fun calculateSmartBufferConfig(): BufferConfig {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        // 获取当前可用内存（以字节为单位）
        val availableMemory = memoryInfo.availMem
        val totalMemory = memoryInfo.totalMem
        val isLowRam = activityManager.isLowRamDevice

        // 根据设备性能等级调整策略
        val deviceTier = when {
            isLowRam || totalMemory < 3L * 1024 * 1024 * 1024 -> DeviceTier.LOW // 低端设备：小于3GB RAM
            totalMemory < 6L * 1024 * 1024 * 1024 -> DeviceTier.MID // 中端设备：3-6GB RAM
            else -> DeviceTier.HIGH // 高端设备：6GB+ RAM
        }
        return when (deviceTier) {
            DeviceTier.LOW -> BufferConfig(
                minBufferMs = 7000,   // 7秒最小缓冲
                maxBufferMs = 11000,  // 11秒最大缓冲
                backBufferMs = 0, // 0秒回退缓冲
                targetBufferBytes = calculateBufferSize(availableMemory, 0.08, 5, 50), // 8%内存，5-50MB
                prioritizeTime = false // 改为优先大小限制，严格控制内存使用
            )
            DeviceTier.MID -> BufferConfig(
                minBufferMs = 11000,  // 11秒最小缓冲
                maxBufferMs = 16000,  // 16秒最大缓冲
                backBufferMs = 0, // 0秒回退缓冲
                targetBufferBytes = calculateBufferSize(availableMemory, 0.13, 5, 150), // 13%内存，5-150MB
                prioritizeTime = false
            )
            DeviceTier.HIGH -> BufferConfig(
                minBufferMs = 12000,  // 12秒最小缓冲
                maxBufferMs = 22000,  // 22秒最大缓冲
                backBufferMs = 11000, // 11秒回退缓冲
                targetBufferBytes = calculateBufferSize(availableMemory, 0.18, 10, 300), // 18%内存，10-300MB
                prioritizeTime = false
            )
        }
    }

    /**
     * 设备性能等级
     */
    private enum class DeviceTier {
        LOW, MID, HIGH
    }

    /**
     * 计算缓冲区大小
     */
    private fun calculateBufferSize(
        availableMemory: Long,
        memoryRatio: Double,
        minMB: Int,
        maxMB: Int
    ): Int {
        val calculatedSize = (availableMemory * memoryRatio).toLong()
        val minSize = minMB * 1024 * 1024L
        val maxSize = maxMB * 1024 * 1024L

        return when {
            calculatedSize < minSize -> minSize.toInt()
            calculatedSize > maxSize -> maxSize.toInt()
            else -> calculatedSize.toInt()
        }
    }
}
