package dev.aaa1115910.bv.player.entity

import kotlinx.coroutines.flow.StateFlow

interface SponsorBlockManager {
    val skipToMillis: StateFlow<Long?>
    val isUserDraggingSeekBar: Boolean
    
    fun checkAndTriggerAutoSkip(currentPositionMillis: Long, durationMillis: Long, isPlaying: Boolean)
    fun onUserDragSeekBarStart()
    fun onUserDragSeekBarStop(finalSeekPosition: Long, durationMillis: Long, isPlaying: Boolean)
    fun consumeSkipEvent()
    fun hideSkipToast()
    fun cancelSkip()
    fun triggerManualSkip()
} 