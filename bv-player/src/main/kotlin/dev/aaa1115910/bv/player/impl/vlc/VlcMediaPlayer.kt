package dev.aaa1115910.bv.player.impl.vlc

import android.content.Context
import android.net.Uri
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerOptions
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
    }

    override fun initPlayer() {
        val libVLCArgs = mutableListOf<String>()
        options.referer?.let{libVLCArgs.add("--http-referrer=$it")}
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

    override val currentPosition: Long
        get() = mPlayer?.time ?: 0
    override val duration: Long
        get() = mPlayer?.media?.duration ?: 0
    override val bufferedPercentage: Int
        get() = 0

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
            Event.Buffering -> {
                mPlayerEventListener?.onBuffering()
            }

            Event.Playing -> {
                mPlayerEventListener?.onPlay()
            }

            Event.EndReached -> {
                mPlayerEventListener?.onEnd()
            }
        }
    }
}
