package dev.aaa1115910.bv.player

interface VideoPlayerListener {
    /** 异常 */
    fun onError(error: Exception)

    /**
     * 准备
     */
    fun onReady()

    /** 播放 */
    fun onPlay()

    /** 暂停 */
    fun onPause()

    /** 缓冲中 */
    fun onBuffering()

    /** 播放结束 */
    fun onEnd()

    /** 空闲，例如播放前 */
    fun onIdle()

    /** 后退 */
    fun onSeekBack(seekBackIncrementMs: Long)

    /** 前进 */
    fun onSeekForward(seekForwardIncrementMs: Long)

}