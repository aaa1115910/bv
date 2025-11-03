package dev.aaa1115910.bv.tv.manager

import java.util.concurrent.ConcurrentHashMap

/**
 * Application 级播放过的稿件 aid 缓存。
 * 用途：避免自动播放推荐时出现重复稿件；在退出播放器或应用重启时清空。
 */
object PlayedAidsCache {
    // 使用线程安全集合，保证多协程访问安全
    private val playedAids = ConcurrentHashMap.newKeySet<Long>()

    /** 标记已播放 */
    fun markPlayed(aid: Long) {
        if (aid > 0) playedAids.add(aid)
    }

    /** 是否已经播放过 */
    fun hasPlayed(aid: Long): Boolean = aid > 0 && playedAids.contains(aid)

    /** 返回所有已播放 aid 快照 */
    fun all(): Set<Long> = playedAids.toSet()

    /** 清空缓存 */
    fun clear() {
        playedAids.clear()
    }
}
