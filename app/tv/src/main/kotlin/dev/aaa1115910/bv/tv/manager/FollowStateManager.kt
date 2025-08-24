package dev.aaa1115910.bv.tv.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 关注状态管理器，用于在不同页面间同步用户关注状态
 * 避免重复调用API获取关注状态
 */
object FollowStateManager {
    // 存储用户关注状态的Map，key为用户mid，value为关注状态
    private val _followStateMap = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val followStateMap: StateFlow<Map<Long, Boolean>> = _followStateMap.asStateFlow()
    
    /**
     * 获取指定用户的关注状态
     * @param mid 用户mid
     * @return 关注状态，null表示未知状态（需要调用API获取）
     */
    fun getFollowState(mid: Long): Boolean? {
        return _followStateMap.value[mid]
    }
    
    /**
     * 更新用户关注状态
     * @param mid 用户mid
     * @param isFollowing 是否关注
     */
    fun updateFollowState(mid: Long, isFollowing: Boolean) {
        _followStateMap.value = _followStateMap.value.toMutableMap().apply {
            this[mid] = isFollowing
        }
    }
    
    /**
     * 移除指定用户的关注状态缓存
     * @param mid 用户mid
     */
    fun removeFollowState(mid: Long) {
        _followStateMap.value = _followStateMap.value.toMutableMap().apply {
            remove(mid)
        }
    }
    
    /**
     * 清空所有关注状态缓存
     */
    fun clearAll() {
        _followStateMap.value = emptyMap()
    }
}
