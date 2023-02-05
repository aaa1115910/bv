package dev.aaa1115910.bv.player.impl.vlc

import android.content.Context
import android.net.Uri
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.MediaPlayer.Event
import org.videolan.libvlc.interfaces.IMedia

class VlcMediaPlayer(
    private val context: Context
) : AbstractVideoPlayer(), MediaPlayer.EventListener {
    var mPlayer: MediaPlayer? = null
    private var mLibVLC: LibVLC? = null

    init {
        initPlayer()
    }

    override fun initPlayer() {
        val args = ArrayList<String>()
        //args.add("-vvv")
        args.add("--http-referrer=https://www.bilibili.com")
        mLibVLC = LibVLC(context, args)
        mLibVLC?.setUserAgent(
            "Chrome",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"
        )
        mPlayer = MediaPlayer(mLibVLC)

        initListener()
    }

    private fun initListener() {
        mPlayer?.setEventListener(this)
    }

    override fun setHeader(headers: Map<String, String>) {
        //TODO("Not yet implemented")
    }

    override fun playUrl(videoUrl: String?, audioUrl: String?) {
        val videoMedia = videoUrl?.let {
            Media(mLibVLC, Uri.parse(videoUrl)).apply {
                addOption(":http-referrer=https://www.bilibili.com")
                audioUrl?.let { addSlave(IMedia.Slave(IMedia.Slave.Type.Audio, 4, it)) }
            }
        }

        videoMedia?.let { media ->
            mPlayer?.media = media
            //audioUrl?.let {
            //    mPlayer?.addSlave(IMedia.Slave.Type.Audio, Uri.parse(it), true)
            //}
        }
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
