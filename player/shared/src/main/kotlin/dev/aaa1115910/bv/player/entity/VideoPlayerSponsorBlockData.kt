package dev.aaa1115910.bv.player.entity

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import dev.aaa1115910.bv.entity.sponsorblock.SegmentItem
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockActionType
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockCategories

/**
 * Enum for skip toast types
 */
enum class SkipToastType {
    AUTO_SKIP,
    MANUAL_SKIP
}

/**
 * Data class holding SponsorBlock related information for the video player.
 *
 * @param segments List of SponsorBlock segments for the current video.
 * @param userActions User-defined actions for each category.
 * @param defaultActions Default actions for each category, used as a fallback.
 * @param isEnabled Whether the SponsorBlock feature is globally enabled.
 * @param showSkipToast Whether to show the skip toast.
 * @param skipToastMessage The message to display in the skip toast.
 * @param skipToastType The type of skip toast (auto or manual).
 * @param showResultToast Whether to show the result toast.
 * @param resultToastMessage The message to display in the result toast.
 */
@Immutable
data class VideoPlayerSponsorBlockData(
    val segments: List<SegmentItem> = emptyList(),
    val userActions: Map<String, SponsorBlockActionType> = emptyMap(),
    val defaultActions: Map<String, SponsorBlockActionType> = SponsorBlockCategories.DEFAULT_ACTIONS,
    val isEnabled: Boolean = true, // Global toggle for the feature
    val showSkipToast: Boolean = false,
    val skipToastMessage: String = "",
    val skipToastType: SkipToastType = SkipToastType.AUTO_SKIP,
    val showResultToast: Boolean = false,
    val resultToastMessage: String = ""
) {
    /**
     * Determines the effective action to be taken for a given segment.
     * It considers user preferences first, then defaults, and finally falls back to DO_NOTHING.
     */
    fun getActionFor(segment: SegmentItem): SponsorBlockActionType {
        if (!isEnabled) return SponsorBlockActionType.DO_NOTHING
        return userActions[segment.category]
            ?: defaultActions[segment.category]
            ?: SponsorBlockActionType.DO_NOTHING
    }
}

/**
 * CompositionLocal for providing [VideoPlayerSponsorBlockData] down the composition tree.
 */
val LocalVideoPlayerSponsorBlockData = staticCompositionLocalOf { VideoPlayerSponsorBlockData() }
