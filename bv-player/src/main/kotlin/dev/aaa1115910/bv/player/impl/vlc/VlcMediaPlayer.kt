package dev.aaa1115910.bv.player.impl.vlc

import android.content.Context
import android.net.Uri
import android.util.Log
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.formatMinSec
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.MediaPlayer.Event
import org.videolan.libvlc.interfaces.IMedia

class VlcMediaPlayer(
    private val context: Context,
    private val options: VideoPlayerOptions
) : AbstractVideoPlayer(), MediaPlayer.EventListener {
    var mPlayer: MediaPlayer? = null
    private var mLibVLC: LibVLC? = null

    init {
        initPlayer()
        Log.i("BvVlcMediaPlayer", "LibVLC version: ${LibVLC.version()}")
    }

    override fun initPlayer() {
        val libVLCArgs = mutableListOf<String>()
        //libVLCArgs.add("-vvv")
        options.referer?.let { libVLCArgs.add("--http-referrer=$it") }
        mLibVLC = LibVLC(context, libVLCArgs)
        options.userAgent?.let { mLibVLC?.setUserAgent("BV", it) }
        mPlayer = MediaPlayer(mLibVLC)

        initListener()
    }

    private fun initListener() {
        mPlayer?.setEventListener(this)
    }

    override fun setHeader(headers: Map<String, String>) {

    }

    override fun playUrl(videoUrl: String?, audioUrl: String?) {
        val videoMedia = videoUrl?.let {
            Media(mLibVLC, Uri.parse(videoUrl)).apply {
                audioUrl?.let { addSlave(IMedia.Slave(IMedia.Slave.Type.Audio, 4, it)) }
                //setHWDecoderEnabled(true, true);
            }
        }

        videoMedia?.let { media -> mPlayer?.media = media }
    }

    override fun prepare() {
        mPlayer?.play()
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
        mPlayer?.time = time
    }

    override fun release() {
        mPlayer?.release()
        mLibVLC?.release()
    }

    private var buffed: Int = 0

    override val currentPosition: Long
        get() = mPlayer?.time ?: 0
    override val duration: Long
        get() = mPlayer?.media?.duration ?: 0
    override val bufferedPercentage: Int
        get() = buffed

    override fun setOptions() {
        //TODO("Not yet implemented")
    }

    override var speed: Float
        get() = mPlayer?.rate ?: 1f
        set(value) {
            mPlayer?.rate = value
        }
    override val tcpSpeed: Long
        get() = 0L

    override fun onEvent(event: Event) {
        when (event.type) {
            Event.Opening -> mPlayerEventListener?.onReady()
            Event.Buffering -> {
                buffed = event.buffering.toInt()
                mPlayerEventListener?.onBuffering()
            }

            Event.Playing -> mPlayerEventListener?.onPlay()
            Event.Paused -> mPlayerEventListener?.onPause()
            Event.EndReached -> mPlayerEventListener?.onEnd()
            // 在 LibVLC 中，更改播放进度时，不会再触发 Playing 事件，因此无法在恢复播放时继续播放弹幕，此时监听该事件来实现监听跳转进度后继续播放
            Event.PositionChanged -> mPlayerEventListener?.onPlay()
        }
    }

    override val debugInfo: String
        get() {
            return """
                player: LibVLC/${LibVLC.version()}
                time: ${currentPosition.formatMinSec()} / ${duration.formatMinSec()}
                buffered: $bufferedPercentage%
                resolution: ${mPlayer?.currentVideoTrack?.width} x ${mPlayer?.currentVideoTrack?.height}
                codec: ${mPlayer?.currentVideoTrack?.codec}
            """.trimIndent()
        }

    override val videoWidth: Int
        get() = mPlayer?.currentVideoTrack?.width ?: 0
    override val videoHeight: Int
        get() = mPlayer?.currentVideoTrack?.height ?: 0
}
