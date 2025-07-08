package dev.aaa1115910.bv.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.PlayData
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskSegment
import dev.aaa1115910.biliapi.entity.video.HeartbeatVideoType
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.biliapi.entity.video.SubtitleAiStatus
import dev.aaa1115910.biliapi.entity.video.SubtitleAiType
import dev.aaa1115910.biliapi.entity.video.SubtitleType
import dev.aaa1115910.biliapi.entity.video.VideoShot
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.repositories.VideoPlayRepository
import dev.aaa1115910.bilisubtitle.SubtitleParser
import dev.aaa1115910.bilisubtitle.entity.SubtitleItem
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.entity.sponsorblock.SegmentItem
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockActionType
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockCategories
import dev.aaa1115910.bv.network.api.SponsorBlockHttpApi
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.entity.RequestState
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.SkipToastType
import dev.aaa1115910.bv.player.entity.SponsorBlockManager
import dev.aaa1115910.bv.player.entity.VideoAspectRatio
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.swapListWithMainContext
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import java.net.URI

@KoinViewModel
class VideoPlayerV3ViewModel(
    private val videoInfoRepository: VideoInfoRepository,
    private val videoPlayRepository: VideoPlayRepository
) : ViewModel(), SponsorBlockManager {
    private val logger = KotlinLogging.logger { }

    // User preferences for SponsorBlock actions
    // Initialized in init block or when settings are loaded/changed
    var sponsorBlockUserActions: Map<String, SponsorBlockActionType> by mutableStateOf(emptyMap())
        private set

    var videoPlayer: AbstractVideoPlayer? by mutableStateOf(null)
    var danmakuPlayer: DanmakuPlayer? by mutableStateOf(null)
    var show by mutableStateOf(false)

    var loadState by mutableStateOf(RequestState.Ready)
    var errorMessage by mutableStateOf("")

    private var playData: PlayData? by mutableStateOf(null)
    var danmakuData = mutableStateListOf<DanmakuItemData>()
    val danmakuMasks = mutableStateListOf<DanmakuMaskSegment>()
    var videoShot: VideoShot? by mutableStateOf(null)

    // SponsorBlock related properties
    val sponsorBlockSegments = mutableStateListOf<SegmentItem>()
    var isFetchingSponsorBlockData by mutableStateOf(false)
    var sponsorBlockErrorMessage by mutableStateOf<String?>(null) // For UI to observe

    // Skip toast related properties
    var showSkipToast by mutableStateOf(false)
        private set
    var skipToastMessage by mutableStateOf("")
        private set
    var skipToastSegment by mutableStateOf<SegmentItem?>(null)
        private set
    var skipToastType by mutableStateOf(SkipToastType.AUTO_SKIP)
        private set

    // Result toast related properties
    var showResultToast by mutableStateOf(false)
        private set
    var resultToastMessage by mutableStateOf("")
        private set

    private val _skipToMillis = MutableStateFlow<Long?>(null)
    override val skipToMillis: StateFlow<Long?> = _skipToMillis.asStateFlow()

    private var lastSkippedSegmentUuidForAutoSkip: String? = null
    private var isCurrentlyAutoSeeking = false
    private var skipToastJob: kotlinx.coroutines.Job? = null
    private var resultToastJob: kotlinx.coroutines.Job? = null
    private var currentSkipJobSegmentUuid: String? = null // Track which segment the current job is for
    private val userCancelledSegmentUuids = mutableSetOf<String>() // Track segments that user manually cancelled
    override var isUserDraggingSeekBar by mutableStateOf(false) // New state for user drag
        private set

    init {
        loadSponsorBlockUserPreferences()
    }

    private fun loadSponsorBlockUserPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            val actions = Prefs.sponsorBlockUserActions
            withContext(Dispatchers.Main) {
                sponsorBlockUserActions = actions
                // logger.fInfo { "Loaded SponsorBlock user preferences: $actions" }
            }
        }
    }

    /**
     * Reload SponsorBlock preferences (call when settings change)
     */
    fun reloadSponsorBlockPreferences() {
        loadSponsorBlockUserPreferences()
    }

    /**
     * Determines the action to be taken for a given segment based on user preferences.
     */
    fun getActionForSponsorBlockSegment(segment: SegmentItem): SponsorBlockActionType {
        return sponsorBlockUserActions[segment.category]
            ?: SponsorBlockCategories.DEFAULT_ACTIONS[segment.category]
            ?: SponsorBlockActionType.DO_NOTHING
    }

    /**
     * Called by the UI after a skip event has been processed.
     */
    override fun consumeSkipEvent() {
        _skipToMillis.value = null
        // Allow isCurrentlyAutoSeeking to be reset, maybe after a small delay or by player state
        // For now, reset it directly, assuming seekTo is instantaneous for this logic.
        // A more robust solution might involve player feedback.
        viewModelScope.launch {
            delay(500) // Small delay to let player settle after seek
            isCurrentlyAutoSeeking = false
        }
    }

    /**
     * Checks if an auto-skip should be performed based on the current playback state.
     * This method should be called when playback position or state changes.
     *
     * @param currentPositionMillis Current playback position in milliseconds.
     * @param durationMillis Total video duration in milliseconds.
     * @param isPlaying Current playback state.
     */
    override fun checkAndTriggerAutoSkip(currentPositionMillis: Long, durationMillis: Long, isPlaying: Boolean) {
        if (!isPlaying || !Prefs.enableSponsorBlock || isCurrentlyAutoSeeking || isUserDraggingSeekBar || sponsorBlockSegments.isEmpty() || durationMillis <= 0) {
            // logger.fInfo { "SponsorBlock: AutoSkip check skipped (isPlaying=$isPlaying, enabled=${Prefs.enableSponsorBlock}, isAutoSeeking=$isCurrentlyAutoSeeking, isUserDragging=$isUserDraggingSeekBar, segments=${sponsorBlockSegments.size}, duration=$durationMillis)" }
            return
        }

        // Reset lastSkippedSegmentUuidForAutoSkip if current position is outside of that segment,
        // allowing it to be skipped again if the user seeks back into it.
        lastSkippedSegmentUuidForAutoSkip?.let { uuid ->
            val lastSkippedSegment = sponsorBlockSegments.find { it.uuid == uuid }
            if (lastSkippedSegment != null &&
                (currentPositionMillis < lastSkippedSegment.startTimeMillis ||
                        currentPositionMillis >= lastSkippedSegment.endTimeMillis - 200)
            ) {
                // If we are no longer actively within the bounds of the segment we just skipped (minus a small buffer for seek settling)
                val isInsideAnotherSkippableSegment = sponsorBlockSegments.any { seg ->
                    seg.uuid != uuid && // Not the one we just skipped
                            currentPositionMillis > seg.startTimeMillis && currentPositionMillis < seg.endTimeMillis &&
                            getActionForSponsorBlockSegment(seg) == SponsorBlockActionType.AUTO_SKIP && seg.actionType == "skip"
                }
                if (!isInsideAnotherSkippableSegment) {
                    lastSkippedSegmentUuidForAutoSkip = null
                }
            }
        }

        // User cancellation remains active for the entire segment duration
        // No automatic clearing based on position - user's cancel decision is respected

        // Check for auto-skip segments
        for (segment in sponsorBlockSegments) {
            val action = getActionForSponsorBlockSegment(segment)
            if (action == SponsorBlockActionType.AUTO_SKIP && segment.actionType == "skip") {
                // Check if we're within 5 seconds before the segment starts or already in the segment
                val timeBeforeSegment = segment.startTimeMillis - currentPositionMillis
                val isApproachingSegment = timeBeforeSegment in 0..5000 // 5 seconds before
                val isInSegment =
                    currentPositionMillis > segment.startTimeMillis && currentPositionMillis < segment.endTimeMillis

                if ((isApproachingSegment || isInSegment) && lastSkippedSegmentUuidForAutoSkip != segment.uuid) {
                    // Check if user has cancelled this segment (no time limit - respect user's decision)
                    if (userCancelledSegmentUuids.contains(segment.uuid)) {
                         logger.fInfo { "checkAndTriggerAutoSkip: User has cancelled segment ${segment.uuid}, skipping auto-skip" }
                        continue // Skip this segment, user doesn't want it to be auto-skipped
                    }

                     logger.fInfo { "checkAndTriggerAutoSkip: Triggering auto-skip for segment ${segment.category} (${segment.uuid}) to ${segment.endTimeMillis}ms" }
                     logger.fInfo { "checkAndTriggerAutoSkip: Current skipToastJob active: ${skipToastJob?.isActive}, showSkipToast: $showSkipToast" }

                    lastSkippedSegmentUuidForAutoSkip = segment.uuid
                    isCurrentlyAutoSeeking = true

                    // Show skip toast and trigger delayed skip
                    val delayMs =
                        if (isApproachingSegment) timeBeforeSegment + 2000 else 2000 // Wait until segment start + 2s, or 2s if already in segment
                    showSkipToastAndDelayedSkip(segment, delayMs, SkipToastType.AUTO_SKIP)
                    break // Process one skip at a time
                }
            }
        }

        // Check for manual-skip segments
        checkAndShowManualSkipToast(currentPositionMillis)
    }

    /**
     * Checks if a manual skip toast should be shown
     */
    private fun checkAndShowManualSkipToast(currentPositionMillis: Long) {
        // Don't show manual skip toast if SponsorBlock is disabled
        if (!Prefs.enableSponsorBlock) {
            return
        }

        // Don't show manual skip toast if auto skip toast is already showing
        if (showSkipToast && skipToastType == SkipToastType.AUTO_SKIP) {
            return
        }

        for (segment in sponsorBlockSegments) {
            val action = getActionForSponsorBlockSegment(segment)
            if (action == SponsorBlockActionType.MANUAL_SKIP && segment.actionType == "skip") {
                // Check if we're within 5 seconds before the segment starts or already in the segment
                val timeBeforeSegment = segment.startTimeMillis - currentPositionMillis
                val isApproachingSegment = timeBeforeSegment in 0..5000 // 5 seconds before
                val isInSegment =
                    currentPositionMillis > segment.startTimeMillis && currentPositionMillis < segment.endTimeMillis

                if (isApproachingSegment || isInSegment) {
                    // Only show manual skip toast if it's not already showing for this segment
                    if (!showSkipToast || skipToastType != SkipToastType.MANUAL_SKIP || skipToastSegment?.uuid != segment.uuid) {
                        // Show manual skip toast (no auto skip, just notification)
                        showManualSkipToast(segment)
                    }
                    break // Process one segment at a time
                }
            }
        }
    }

    /**
     * Shows manual skip toast
     */
    private fun showManualSkipToast(segment: SegmentItem) {
        // Cancel any existing skip toast job
        val oldJob = skipToastJob
         val oldJobId = oldJob?.toString()
        oldJob?.cancel()
         logger.fInfo { "showManualSkipToast: Cancelled old job: $oldJobId" }

        // Show manual skip toast
        showSkipToast = true
        skipToastMessage = SponsorBlockCategories.getDisplayName(segment.category) // 只传递类别名称
        skipToastSegment = segment
        skipToastType = SkipToastType.MANUAL_SKIP

        // Manual skip toast doesn't auto-hide, it stays until user leaves the segment
        skipToastJob = viewModelScope.launch {
             val jobId = coroutineContext[kotlinx.coroutines.Job]?.toString()
             logger.fInfo { "showManualSkipToast: Created new manual skip job: $jobId for segment ${segment.category}" }

            // Check periodically if user is still in the segment
            while (showSkipToast && skipToastType == SkipToastType.MANUAL_SKIP) {
                delay(1000)
                val currentPos = videoPlayer?.currentPosition ?: 0
                val isStillInSegment = currentPos > segment.startTimeMillis && currentPos < segment.endTimeMillis
                val isStillApproaching = (segment.startTimeMillis - currentPos) in 0..5000

                if (!isStillInSegment && !isStillApproaching) {
                     logger.fInfo { "showManualSkipToast: Job $jobId completing - user left segment" }
                    hideSkipToast()
                    break
                }
            }
             logger.fInfo { "showManualSkipToast: Job $jobId completed" }
        }

         val newJobId = skipToastJob?.toString()
         logger.fInfo { "showManualSkipToast: Final job ID: $newJobId" }
    }

    /**
     * Shows skip toast and triggers delayed skip for auto-skip
     */
    private fun showSkipToastAndDelayedSkip(segment: SegmentItem, delayMs: Long, toastType: SkipToastType) {
        // Check if we already have a job running for this segment
        if (currentSkipJobSegmentUuid == segment.uuid && skipToastJob?.isActive == true) {
             val existingJobId = skipToastJob?.toString()
             logger.fInfo { "showSkipToastAndDelayedSkip: Job $existingJobId already running for segment ${segment.uuid}, skipping creation" }
            return
        }

        // Cancel any existing skip toast job
        val oldJob = skipToastJob
         val oldJobId = oldJob?.toString()
        oldJob?.cancel()
         logger.fInfo { "showSkipToastAndDelayedSkip: Cancelled old job: $oldJobId, creating new job for ${segment.category} with delay ${delayMs}ms" }

        // Show skip toast
        showSkipToast = true
        skipToastMessage = SponsorBlockCategories.getDisplayName(segment.category) // 只传递类别名称
        skipToastSegment = segment
        skipToastType = toastType

        if (toastType == SkipToastType.AUTO_SKIP) {
            // Schedule delayed skip for auto-skip
            currentSkipJobSegmentUuid = segment.uuid // Track which segment this job is for
            skipToastJob = viewModelScope.launch {
                 val jobId = coroutineContext[kotlinx.coroutines.Job]?.toString()
                 logger.fInfo { "skipToastJob: Created job $jobId - Starting delay of ${delayMs}ms for segment ${segment.category} (${segment.uuid})" }

                try {
                    delay(delayMs)
                     logger.fInfo { "skipToastJob: Job $jobId - Delay completed, executing skip for ${segment.category}" }

                    // Hide toast and perform skip
                    hideSkipToast()

                    // Perform the actual skip
                    _skipToMillis.value = segment.endTimeMillis

                    // Show result toast
                    showResultToast("已跳过${SponsorBlockCategories.getDisplayName(segment.category)}")

                    // Clear the segment UUID since job is complete
                    currentSkipJobSegmentUuid = null
                     logger.fInfo { "skipToastJob: Job $jobId completed successfully" }

                } catch (e: kotlinx.coroutines.CancellationException) {
                     logger.fInfo { "skipToastJob: Job $jobId was cancelled" }
                    throw e
                } catch (e: Exception) {
                    logger.fException(e) { "skipToastJob: Job failed with exception: ${e.message}" }
                    throw e
                }
            }

             val newJobId = skipToastJob?.toString()
             logger.fInfo { "showSkipToastAndDelayedSkip: Created new skipToastJob: $newJobId (active: ${skipToastJob?.isActive})" }
        }
    }

    /**
     * Triggers manual skip for the current segment
     */
    override fun triggerManualSkip() {
        skipToastSegment?.let { segment ->
            if (skipToastType == SkipToastType.MANUAL_SKIP) {
                val currentPosition = videoPlayer?.currentPosition ?: 0
                val isInSegment = currentPosition > segment.startTimeMillis && currentPosition < segment.endTimeMillis
                val isApproachingSegment = (segment.startTimeMillis - currentPosition) in 0..5000

                if (isInSegment) {
                    // User is already in the segment, skip immediately
                     logger.fInfo { "triggerManualSkip: User in segment, skipping immediately to ${segment.endTimeMillis}ms" }
                    hideSkipToast()
                    _skipToMillis.value = segment.endTimeMillis
                    showResultToast("已跳过${SponsorBlockCategories.getDisplayName(segment.category)}")
                } else if (isApproachingSegment) {
                    // User is approaching the segment, schedule delayed skip
                    val delayMs = segment.startTimeMillis - currentPosition + 500 // Wait until segment start + 0.5s
                     logger.fInfo { "triggerManualSkip: User approaching segment, scheduling skip in ${delayMs}ms" }

                    // Cancel any existing skip toast job and create a new one for manual skip
                    skipToastJob?.cancel()
                    currentSkipJobSegmentUuid = segment.uuid

                    // Update toast message to show it's scheduled and keep toast visible
                    skipToastMessage = SponsorBlockCategories.getDisplayName(segment.category) // 延迟跳过时传递类别名称
                    showSkipToast = true // Ensure toast remains visible
                    skipToastSegment = segment
                    skipToastType = SkipToastType.MANUAL_SKIP

                    skipToastJob = viewModelScope.launch {
                         val jobId = coroutineContext[kotlinx.coroutines.Job]?.toString()
                         logger.fInfo { "triggerManualSkip: Created job $jobId - Starting delay of ${delayMs}ms for manual skip" }

                        try {
                            delay(delayMs)
                             logger.fInfo { "triggerManualSkip: Job $jobId - Delay completed, executing manual skip" }

                            // Hide toast and perform skip
                            hideSkipToast()
                            _skipToMillis.value = segment.endTimeMillis
                            showResultToast("已跳过${SponsorBlockCategories.getDisplayName(segment.category)}")

                            currentSkipJobSegmentUuid = null
                             logger.fInfo { "triggerManualSkip: Job $jobId completed successfully" }

                        } catch (e: kotlinx.coroutines.CancellationException) {
                             logger.fInfo { "triggerManualSkip: Job $jobId was cancelled" }
                            throw e
                        } catch (e: Exception) {
                            logger.fException(e) { "triggerManualSkip: Job failed with exception: ${e.message}" }
                            throw e
                        }
                    }
                } else {
                    // User is not in the right position, just hide the toast
                     logger.fInfo { "triggerManualSkip: User not in correct position for segment, hiding toast" }
                    hideSkipToast()
                }
            }
        }
    }

    /**
     * Shows result toast
     */
    private fun showResultToast(message: String) {
        resultToastJob?.cancel()

        showResultToast = true
        resultToastMessage = message

        resultToastJob = viewModelScope.launch {
            delay(2000) // Show for 2 seconds
            showResultToast = false
            resultToastMessage = ""
        }
    }

    /**
     * Cancels the pending skip and hides the skip toast
     */
    override fun cancelSkip() {
        val jobToCancel = skipToastJob
         val jobId = jobToCancel?.toString()
         logger.fInfo { "cancelSkip: Starting - skipToastJob: $jobId (active: ${jobToCancel?.isActive}), type: $skipToastType" }

        // val cancelResult = jobToCancel?.cancel()
        // logger.fInfo { "cancelSkip: Job $jobId cancel result: $cancelResult, job was active: ${jobToCancel?.isActive}" }

        val wasAutoSkip = skipToastType == SkipToastType.AUTO_SKIP

        // Record which segment user cancelled
        skipToastSegment?.let { segment ->
            userCancelledSegmentUuids.add(segment.uuid)
             logger.fInfo { "cancelSkip: Recorded user cancellation for segment ${segment.uuid}. Total cancelled segments: ${userCancelledSegmentUuids.size}" }
        }

        hideSkipToast()
        isCurrentlyAutoSeeking = false

        // Don't clear lastSkippedSegmentUuidForAutoSkip immediately to prevent re-triggering
        // It will be cleared when user leaves the segment

        // Clear the current job segment UUID
        currentSkipJobSegmentUuid = null

        if (wasAutoSkip) {
            showResultToast("已取消跳过")
             logger.fInfo { "cancelSkip: Showed '已取消跳过' result toast" }
        }

         logger.fInfo { "cancelSkip: Completed successfully for job $jobId" }
    }

    /**
     * Hides the skip toast (can be called if user manually skips or seeks)
     */
    override fun hideSkipToast() {
        val jobToCancel = skipToastJob
         val jobId = jobToCancel?.toString()
         logger.fInfo { "hideSkipToast: Cancelling job $jobId (active: ${jobToCancel?.isActive})" }

        jobToCancel?.cancel()
        showSkipToast = false
        skipToastMessage = ""
        skipToastSegment = null
        skipToastType = SkipToastType.AUTO_SKIP
        currentSkipJobSegmentUuid = null // Clear the segment UUID

         logger.fInfo { "hideSkipToast: Completed, cancelled job $jobId" }
    }

    override fun onUserDragSeekBarStart() {
        // logger.fInfo { "SponsorBlock: User started dragging seekbar" }
        isUserDraggingSeekBar = true
        // Optionally, could cancel any pending auto-skip intent here, though checkAndTriggerAutoSkip already checks isUserDraggingSeekBar
    }

    override fun onUserDragSeekBarStop(finalSeekPosition: Long, durationMillis: Long, isPlaying: Boolean) {
        // logger.fInfo { "SponsorBlock: User stopped dragging seekbar at $finalSeekPosition" }
        isUserDraggingSeekBar = false
        // After user finishes seeking, immediately check if the new position falls into a skippable segment
        // This is important if the user seeks into a segment that should be skipped.
        checkAndTriggerAutoSkip(finalSeekPosition, durationMillis, isPlaying)
    }

    var availableQuality = mutableStateListOf<Resolution>()
    var availableVideoCodec = mutableStateListOf<VideoCodec>()
    var availableSubtitle = mutableStateListOf<Subtitle>()
    var availableAudio = mutableStateListOf<Audio>()
    val availableVideoList get() = videoInfoRepository.videoList

    var currentVideoHeight by mutableIntStateOf(0)
    var currentVideoWidth by mutableIntStateOf(0)

    var currentQuality by mutableStateOf(Prefs.defaultQuality)
    var currentVideoCodec by mutableStateOf(Prefs.defaultVideoCodec)
    var currentPlaySpeed by mutableFloatStateOf(Prefs.defaultPlaySpeed)
    var currentVideoAspectRatio by mutableStateOf(VideoAspectRatio.Default)
    var currentAudio by mutableStateOf(Prefs.defaultAudio)
    var currentDanmakuScale by mutableFloatStateOf(Prefs.defaultDanmakuScale)
    var currentDanmakuOpacity by mutableFloatStateOf(Prefs.defaultDanmakuOpacity)
    var currentDanmakuEnabled by mutableStateOf(Prefs.defaultDanmakuEnabled)
    val currentDanmakuTypes = mutableStateListOf<DanmakuType>().apply {
        addAll(Prefs.defaultDanmakuTypes)
    }
    var currentDanmakuArea by mutableFloatStateOf(Prefs.defaultDanmakuArea)
    var currentDanmakuMask by mutableStateOf(Prefs.defaultDanmakuMask)
    var currentSubtitleId by mutableLongStateOf(-1L)
    var currentSubtitleData = mutableStateListOf<SubtitleItem>()
    var currentSubtitleFontSize by mutableStateOf(Prefs.defaultSubtitleFontSize)
    var currentSubtitleBackgroundOpacity by mutableFloatStateOf(Prefs.defaultSubtitleBackgroundOpacity)
    var currentSubtitleBottomPadding by mutableStateOf(Prefs.defaultSubtitleBottomPadding)

    var title by mutableStateOf("")
    var partTitle by mutableStateOf("")
    var lastPlayed by mutableIntStateOf(0)
    var fromSeason by mutableStateOf(false)
    var subType by mutableIntStateOf(0)
    var epid by mutableIntStateOf(0)
    var seasonId by mutableIntStateOf(0)
    var isVerticalVideo by mutableStateOf(false)
    var proxyArea by mutableStateOf(ProxyArea.MainLand)

    var needPay by mutableStateOf(false)

    var logs by mutableStateOf("")
    var lastChangedLog by mutableLongStateOf(System.currentTimeMillis())
    var showBuffering by mutableStateOf(false)

    var playerIconIdle by mutableStateOf("")
    var playerIconMoving by mutableStateOf("")

    private var currentAid = 0L
    var currentCid by mutableLongStateOf(0L)
    private var currentEpid = 0
    private var currentBvid: String? = null

    private suspend fun releaseDanmakuPlayer() = withContext(Dispatchers.Main) {
        danmakuPlayer?.release()
    }

    suspend fun initDanmakuPlayer() = withContext(Dispatchers.Main) {
        danmakuPlayer = DanmakuPlayer(SimpleRenderer())
    }

    fun loadPlayUrl(
        avid: Long,
        cid: Long,
        bvid: String? = null,
        epid: Int? = null,
        seasonId: Int? = null,
        continuePlayNext: Boolean = false
    ) {
        currentAid = avid
        currentCid = cid
        currentEpid = epid ?: 0
        currentBvid = bvid
        epid?.let { this.epid = it }
        seasonId?.let { this.seasonId = it }
        viewModelScope.launch(Dispatchers.Default) {
            addLogs("加载视频中")
            releaseDanmakuPlayer()
            initDanmakuPlayer()
            addLogs("初始化弹幕引擎")
            if (epid != null || seasonId != null) {
                addLogs("av$avid，cid:$cid, epid:$epid, seasonId:$seasonId")
            } else {
                addLogs("av$avid，cid:$cid")
            }

            // Clear previous SponsorBlock segments and user cancellation state
            withContext(Dispatchers.Main) {
                sponsorBlockSegments.clear()
                // Clear user cancellation state when loading new video
                userCancelledSegmentUuids.clear()
            }

            val lastPlayEnabledSubtitle = currentSubtitleId != -1L
            if (lastPlayEnabledSubtitle) {
                logger.info { "Subtitle is enabled, next video will enable subtitle automatic" }
            }

            // Launch SponsorBlock data fetching concurrently
            launch {
                loadSponsorBlockData(cid)
            }

            updateSubtitle()
            loadPlayUrl(avid, cid, epid ?: 0, preferApi = Prefs.apiType, proxyArea = proxyArea)
            addLogs("加载弹幕中")
            loadDanmaku(cid)
            updateDanmakuMask()

            updateVideoShot()

            //如果是继续播放下一集，且之前开启了字幕，就会自动加载第一条字幕，主要用于观看番剧时自动加载字幕
            if (continuePlayNext) {
                if (lastPlayEnabledSubtitle) enableFirstSubtitle()
            }
        }
    }

    private suspend fun loadPlayUrl(
        avid: Long,
        cid: Long,
        epid: Int = 0,
        preferApi: ApiType = Prefs.apiType,
        proxyArea: ProxyArea = ProxyArea.MainLand
    ) {
        logger.fInfo { "Load play url: [av=$avid, cid=$cid, preferApi=$preferApi, proxyArea=$proxyArea]" }
        withContext(Dispatchers.Main) { loadState = RequestState.Ready }
        logger.fInfo { "Set request state: ready" }
        logger.fInfo { "fromSeason: $fromSeason" }
        runCatching {
            val playData = if (fromSeason) {
                videoPlayRepository.getPgcPlayData(
                    aid = avid,
                    cid = cid,
                    epid = epid,
                    preferCodec = Prefs.defaultVideoCodec.toBiliApiCodeType(),
                    preferApiType = Prefs.apiType,
                    enableProxy = proxyArea != ProxyArea.MainLand,
                    proxyArea = when (proxyArea) {
                        ProxyArea.MainLand -> ""
                        ProxyArea.HongKong -> "hk"
                        ProxyArea.TaiWan -> "tw"
                    }
                )
            } else {
                videoPlayRepository.getPlayData(
                    aid = avid,
                    cid = cid,
                    preferApiType = Prefs.apiType
                )
            }

            //检查是否需要购买，如果未购买，则正片返回的dash为null，非正片例如可以免费观看的预告片等则会返回数据，此时不做提示
            withContext(Dispatchers.Main) { needPay = playData.needPay }
            if (needPay) return@runCatching

            withContext(Dispatchers.Main) { this@VideoPlayerV3ViewModel.playData = playData }
            logger.fInfo { "Load play data response success" }
            //logger.info { "Play data: $playData" }

            //读取清晰度
            val resolutionList = mutableListOf<Resolution>()
            playData.dashVideos.forEach {
                Resolution.fromCode(it.quality)?.let { resolution ->
                    if (!resolutionList.contains(resolution)) resolutionList.add(resolution)
                }
            }

            logger.fInfo { "Video available resolution: $resolutionList" }
            availableQuality.swapListWithMainContext(resolutionList)

            //读取音频
            val audioList = mutableListOf<Audio>()
            playData.dashAudios.forEach {
                Audio.fromCode(it.codecId)?.let { audio ->
                    if (!audioList.contains(audio)) audioList.add(audio)
                }
            }
            playData.dolby?.let {
                Audio.fromCode(it.codecId)?.let { audio ->
                    audioList.add(audio)
                }
            }
            playData.flac?.let {
                Audio.fromCode(it.codecId)?.let { audio ->
                    audioList.add(audio)
                }
            }

            logger.fInfo { "Video available audio: $audioList" }
            availableAudio.swapListWithMainContext(audioList)

            //先确认最终所选清晰度
            val existDefaultResolution =
                availableQuality.find { it == Prefs.defaultQuality } != null

            if (!existDefaultResolution) {
                val tempList = resolutionList.sortedByDescending { it.code }
                val currentQuality = tempList.firstOrNull { it.code < Prefs.defaultQuality.code }
                    ?: tempList.last()
                withContext(Dispatchers.Main) {
                    this@VideoPlayerV3ViewModel.currentQuality = currentQuality
                }
            }

            //确认最终所选音质
            val existDefaultAudio = availableAudio.contains(Prefs.defaultAudio)
            if (!existDefaultAudio) {
                val currentAudio = when {
                    Prefs.defaultAudio == Audio.ADolbyAtoms && availableAudio.contains(Audio.AHiRes) -> Audio.AHiRes
                    Prefs.defaultAudio == Audio.AHiRes && availableAudio.contains(Audio.ADolbyAtoms) -> Audio.ADolbyAtoms
                    availableAudio.contains(Audio.A192K) -> Audio.A192K
                    availableAudio.contains(Audio.A132K) -> Audio.A132K
                    availableAudio.contains(Audio.A64K) -> Audio.A64K
                    else -> availableAudio.first()
                }
                withContext(Dispatchers.Main) {
                    this@VideoPlayerV3ViewModel.currentAudio = currentAudio
                }
            }

            //再确认最终所选视频编码
            updateAvailableCodec()

            playQuality(qn = currentQuality.code, codec = currentVideoCodec)

        }.onFailure {
            addLogs("加载视频地址失败：${it.localizedMessage}")
            errorMessage = it.localizedMessage ?: "Unknown error"
            loadState = RequestState.Failed
            logger.fException(it) { "Load video failed" }
        }.onSuccess {
            addLogs("加载视频地址成功")
            loadState = RequestState.Success
            logger.fInfo { "Load play url success" }
        }
    }

    private suspend fun updateAvailableCodec() {
        if (Prefs.apiType == ApiType.App && playData!!.codec.isEmpty()) {
            // 纠正当前实际播放的编码
            val videoItem = playData!!.dashVideos
                .find { it.quality == currentQuality.code }
                ?: playData!!.dashVideos.first()
            withContext(Dispatchers.Main) {
                currentVideoCodec = VideoCodec.fromCodecId(videoItem.codecId)
            }
            return
        }

        val supportedCodec = playData!!.codec
        val codecList =
            supportedCodec[currentQuality.code]!!.mapNotNull { VideoCodec.fromCodecString(it) }

        availableVideoCodec.swapListWithMainContext(codecList)
        logger.fInfo { "Video available codec: ${availableVideoCodec.toList()}" }

        logger.fInfo { "Default codec: $currentVideoCodec" }
        val currentVideoCodec = if (codecList.contains(Prefs.defaultVideoCodec)) {
            Prefs.defaultVideoCodec
        } else {
            codecList.minByOrNull { it.ordinal }!!
        }
        withContext(Dispatchers.Main) {
            this@VideoPlayerV3ViewModel.currentVideoCodec = currentVideoCodec
        }
        logger.fInfo { "Select codec: $currentVideoCodec" }
    }

    suspend fun playQuality(
        qn: Resolution = currentQuality,
        codec: VideoCodec = currentVideoCodec,
        audio: Audio = currentAudio
    ) {
        if (qn != currentQuality) {
            // 更新清晰度后需要先设置清晰度再更新编码列表
            withContext(Dispatchers.Main) { currentQuality = qn }
            updateAvailableCodec()
            playQuality(qn.code, currentVideoCodec, audio)
        } else {
            playQuality(qn.code, codec, audio)
        }
    }

    private suspend fun playQuality(
        qn: Int = currentQuality.code,
        codec: VideoCodec = currentVideoCodec,
        audio: Audio = currentAudio
    ) {
        logger.fInfo { "Select resolution: $qn, codec: $codec, audio: $audio" }
        addLogs(
            "播放清晰度：${availableQuality.firstOrNull { it.code == qn }}, " +
                    "视频编码：${codec.getDisplayName(BVApp.context)}"
        )

        val videoItem = playData!!.dashVideos.find {
            when (Prefs.apiType) {
                ApiType.Web -> it.quality == qn && it.codecs!!.startsWith(codec.prefix)
                ApiType.App -> {
                    if (playData!!.codec.isEmpty()) it.quality == qn
                    else it.quality == qn && it.codecs!!.startsWith(codec.prefix)
                }
            }
        }
        var videoUrl = videoItem?.baseUrl ?: playData!!.dashVideos.first().baseUrl
        val videoUrls = mutableListOf<String?>()
        videoUrls.add(videoItem?.baseUrl)
        videoUrls.addAll(videoItem?.backUrl ?: emptyList())

        val audioItem = playData!!.dashAudios.find { it.codecId == audio.code }
            ?: playData!!.dolby.takeIf { it?.codecId == audio.code }
            ?: playData!!.flac.takeIf { it?.codecId == audio.code }
            ?: playData!!.dashAudios.minByOrNull { it.codecId }
        var audioUrl = audioItem?.baseUrl ?: playData!!.dashAudios.first().baseUrl
        val audioUrls = mutableListOf<String?>()
        audioUrls.add(audioItem?.baseUrl)
        audioUrls.addAll(audioItem?.backUrl ?: emptyList())

        logger.fInfo { "all video hosts: ${videoUrls.map { with(URI(it)) { "$scheme://$authority" } }}" }
        logger.fInfo { "all audio hosts: ${audioUrls.map { with(URI(it)) { "$scheme://$authority" } }}" }

        //replace cdn
        if (Prefs.enableProxy && proxyArea != ProxyArea.MainLand) {
            videoUrl = videoUrl.replaceUrlDomainWithAliCdn()
            audioUrl = audioUrl.replaceUrlDomainWithAliCdn()
        } else {
            // 如果未通过网络代理获得播放地址，才判断是否应该替换为官方 cdn
            videoUrl = selectOfficialCdnUrl(videoUrls.filterNotNull())
            audioUrl = selectOfficialCdnUrl(audioUrls.filterNotNull())
        }

        addLogs("video host: ${with(URI(videoUrl)) { "$scheme://$authority" }}")
        addLogs("audio host: ${with(URI(audioUrl)) { "$scheme://$authority" }}")

        logger.fInfo { "Select audio: $audioItem" }
        addLogs("音频编码：${(Audio.fromCode(audioItem?.codecId ?: 0))?.getDisplayName(BVApp.context) ?: "未知"}")

        withContext(Dispatchers.Main) {
            currentVideoHeight = videoItem?.height ?: 0
            currentVideoWidth = videoItem?.width ?: 0
            logger.info { "Video url: $videoUrl" }
            logger.info { "Audio url: $audioUrl" }
            videoPlayer!!.playUrl(videoUrl, audioUrl)
            videoPlayer!!.prepare()
            showBuffering = true
        }
    }

    suspend fun loadDanmaku(cid: Long) {
        runCatching {
            val danmakuXmlData = BiliHttpApi.getDanmakuXml(cid = cid, sessData = Prefs.sessData)

            val danmakuItemDataList = danmakuXmlData.data.map {
                DanmakuItemData(
                    danmakuId = it.dmid,
                    position = (it.time * 1000).toLong(),
                    content = it.text,
                    mode = when (it.type) {
                        4 -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                        5 -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                        else -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    },
                    textSize = it.size,
                    textColor = Color(it.color).toArgb()
                )
            }
            danmakuData.swapListWithMainContext(danmakuItemDataList)
            danmakuPlayer?.updateData(danmakuData)
        }.onFailure {
            addLogs("加载弹幕失败：${it.localizedMessage}")
            logger.fWarn { "Load danmaku filed: ${it.stackTraceToString()}" }
        }.onSuccess {
            addLogs("已加载 ${danmakuData.size} 条弹幕")
            logger.fInfo { "Load danmaku success, size=${danmakuData.size}" }
        }
    }

    private suspend fun updateSubtitle() {
        currentSubtitleId = -1
        currentSubtitleData.clear()

        runCatching {
            val subtitleData = videoPlayRepository.getSubtitle(
                aid = currentAid,
                cid = currentCid,
                preferApiType = Prefs.apiType
            )
            withContext(Dispatchers.Main) {
                availableSubtitle.clear()
                availableSubtitle.add(
                    Subtitle(
                        id = -1,
                        lang = "",
                        langDoc = "关闭",
                        url = "",
                        type = SubtitleType.CC,
                        aiType = SubtitleAiType.Normal,
                        aiStatus = SubtitleAiStatus.None
                    )
                )
                availableSubtitle.addAll(subtitleData)
                availableSubtitle.sortBy { it.id }
            }
            addLogs("获取到 ${subtitleData.size} 条字幕: ${subtitleData.map { it.langDoc }}")
            logger.fInfo { "Update subtitle size: ${subtitleData.size}" }
        }.onFailure {
            addLogs("获取字幕失败：${it.localizedMessage}")
            logger.fWarn { "Update subtitle failed: ${it.stackTraceToString()}" }
        }
    }

    private fun enableFirstSubtitle() {
        runCatching {
            logger.info { "Load first subtitle" }
            logger.info { "availableSubtitle: ${availableSubtitle.toList()}" }
            loadSubtitle(
                availableSubtitle
                    .firstOrNull { it.id != -1L }?.id
                    ?: throw IllegalStateException("No available subtitle")
            )
        }.onFailure {
            logger.error { "Load first subtitle failed: ${it.stackTraceToString()}" }
        }
    }

    private suspend fun addLogs(text: String) {
        logger.fInfo { text }
        val lines = logs.lines().toMutableList()
        lines.add(text)
        while (lines.size > 8) {
            lines.removeAt(0)
        }
        var newTip = ""
        lines.forEach {
            newTip += if (newTip == "") it else "\n$it"
        }
        withContext(Dispatchers.Main) {
            logs = newTip
            lastChangedLog = System.currentTimeMillis()
        }
    }

    suspend fun uploadHistory(time: Int) {
        runCatching {
            if (!fromSeason) {
                logger.info { "Send heartbeat: [avid=$currentAid, cid=$currentCid, time=$time]" }
                videoPlayRepository.sendHeartbeat(
                    aid = currentAid,
                    cid = currentCid,
                    time = time,
                    preferApiType = Prefs.apiType
                )
            } else {
                logger.info { "Send heartbeat: [avid=$currentAid, cid=$currentCid, epid=$epid, sid=$seasonId, time=$time]" }
                videoPlayRepository.sendHeartbeat(
                    aid = currentAid,
                    cid = currentCid,
                    time = time,
                    type = HeartbeatVideoType.Season,
                    subType = subType,
                    epid = epid,
                    seasonId = seasonId,
                    preferApiType = Prefs.apiType
                )
            }
        }.onSuccess {
            logger.info { "Send heartbeat success" }
        }.onFailure {
            logger.warn { "Send heartbeat failed: ${it.stackTraceToString()}" }
        }
    }

    fun loadSubtitle(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if (id == -1L) {
                withContext(Dispatchers.Main) {
                    currentSubtitleData.clear()
                    currentSubtitleId = -1
                }
                return@launch
            }
            var subtitleName = ""
            runCatching {
                val subtitle = availableSubtitle.find { it.id == id } ?: return@runCatching
                subtitleName = subtitle.langDoc
                logger.info { "Subtitle url: ${subtitle.url}" }
                val client = HttpClient(OkHttp)
                val responseText = client.get(subtitle.url).bodyAsText()
                val subtitleData = SubtitleParser.fromBccString(responseText)
                withContext(Dispatchers.Main) {
                    currentSubtitleId = id
                    currentSubtitleData.swapList(subtitleData)
                }
            }.onFailure {
                logger.fInfo { "Load subtitle failed: ${it.stackTraceToString()}" }
                addLogs("加载字幕 $subtitleName 失败: ${it.localizedMessage}")
            }.onSuccess {
                logger.fInfo { "Load subtitle $subtitleName success" }
                addLogs("加载字幕 $subtitleName 成功，数量: ${currentSubtitleData.size}")
            }
        }
    }

    private fun String.replaceUrlDomainWithAliCdn(): String {
        val replaceDomainKeywords = listOf(
            "mirroraliov",
            "mirrorakam"
        )
        if (replaceDomainKeywords.none { this.contains(it) }) return this

        return Uri.parse(this)
            .buildUpon()
            .authority("upos-sz-mirrorali.bilivideo.com")
            .build()
            .toString()
    }

    private fun selectOfficialCdnUrl(urls: List<String>): String {
        if (!Prefs.preferOfficialCdn) {
            logger.fInfo { "doesn't need to filter official cdn url, select the first url" }
            return urls.first()
        }
        val filteredUrls = urls
            .filter { !it.contains(".mcdn.bilivideo.") }
            .filter { !it.contains(".szbdyd.com") }
            .filter {
                !Regex("^(https?://)?(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d{1,5})?)(/[a-zA-Z0-9_./-]*)?(\\?.*)?$")
                    .matches(it)
            }
        if (filteredUrls.isEmpty()) {
            logger.fInfo { "doesn't find any official cdn url, select the first url" }
            return urls.first()
        } else {
            logger.fInfo { "filtered official cdn urls: $filteredUrls" }
            return filteredUrls.first()
        }
    }

    private suspend fun updateDanmakuMask() {
        runCatching {
            val masks = videoPlayRepository.getDanmakuMask(
                aid = currentAid,
                cid = currentCid,
                preferApiType = Prefs.apiType
            )
            danmakuMasks.swapListWithMainContext(masks)
            logger.fInfo { "Load danmaku mask size: ${danmakuMasks.size}" }
        }.onFailure {
            logger.fWarn { "Load danmaku mask failed: ${it.stackTraceToString()}" }
        }
    }

    private suspend fun updateVideoShot() {
        withContext(Dispatchers.Main) { videoShot = null }
        runCatching {
            val videoShot = videoPlayRepository.getVideoShot(
                aid = currentAid,
                cid = currentCid,
                preferApiType = Prefs.apiType
            )
            withContext(Dispatchers.Main) { this@VideoPlayerV3ViewModel.videoShot = videoShot }
            logger.fInfo { "Load video shot success" }
        }.onFailure {
            logger.fWarn { "Load video shot failed: ${it.stackTraceToString()}" }
        }
    }

    private suspend fun loadSponsorBlockData(cid: Long) {
        // TODO: Read user preferences to see if SponsorBlock is enabled (e.g. from Prefs)
        if (!Prefs.enableSponsorBlock) {
            addLogs("SponsorBlock: 功能未启用")
            // logger.fInfo { "SponsorBlock: Feature disabled in settings" }
            return
        }

        val bvid = currentBvid
        if (bvid == null) {
            addLogs("SponsorBlock: 获取 BVID 失败，无法获取片段数据")
            logger.fWarn { "SponsorBlock: BVID is null, cannot fetch segments." }
            return
        }

        addLogs("SponsorBlock: 获取片段数据 (BVID: $bvid, CID: $cid)...")
        // logger.fInfo { "SponsorBlock: Fetching segments for BVID: $bvid, CID: $cid" }
        withContext(Dispatchers.Main) {
            isFetchingSponsorBlockData = true
            sponsorBlockErrorMessage = null // Clear previous error
        }

        val result = SponsorBlockHttpApi.getSkipSegments(
            videoID = bvid,
            cid = cid
            // categories = userEnabledCategories // Pass if API supports filtering and user has choices
        )

        withContext(Dispatchers.Main) {
            result.onSuccess { segments ->
                sponsorBlockSegments.swapList(segments)
                // logger.fInfo { "SponsorBlock: API返回 ${segments.size} 个片段" }
                // logger.fInfo { "SponsorBlock: 用户设置: $sponsorBlockUserActions" }
                if (segments.isNotEmpty()) {
                    addLogs("SponsorBlock: 找到 ${segments.size} 个片段")
                    segments.take(3).forEach { segment -> // Log first 3 segments for brevity
                        val action = getActionForSponsorBlockSegment(segment)
                        addLogs("  - ${segment.category} [${segment.startTimeSeconds}s - ${segment.endTimeSeconds}s] -> $action")
                        // logger.fInfo { "SponsorBlock: 片段 ${segment.category} (${segment.uuid}) [${segment.startTimeMillis}ms - ${segment.endTimeMillis}ms] -> $action" }
                    }
                } else {
                    addLogs("SponsorBlock: 未找到片段数据")
                    // logger.fInfo { "SponsorBlock: API returned empty segments list" }
                }
            }.onFailure { exception ->
                val errorMsg =
                    "SponsorBlock: 获取数据失败 - ${exception.localizedMessage ?: exception.message ?: "未知错误"}"
                addLogs(errorMsg)
                sponsorBlockErrorMessage = errorMsg // Set error message for UI
                logger.fWarn { "Failed to load SponsorBlock segments: ${exception.stackTraceToString()}" }
            }
            isFetchingSponsorBlockData = false
        }
    }
}