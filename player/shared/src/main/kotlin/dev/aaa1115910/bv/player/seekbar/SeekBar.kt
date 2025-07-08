package dev.aaa1115910.bv.player.seekbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockActionType
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockCategories
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockColors
import dev.aaa1115910.bv.player.entity.VideoPlayerSponsorBlockData

@Composable
fun SeekBar(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int,
    colors: SliderColors = SliderDefaults.colors(),
    sponsorBlockData: VideoPlayerSponsorBlockData = VideoPlayerSponsorBlockData() // Added
) {
    val trackWidth = 32f
    val segmentTrackHeight = trackWidth * 0.8f // Make segment markers slightly thinner or adjust as needed

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(trackWidth.dp)
    ) {
        // 1. Draw background track
        drawLine(
            color = colors.inactiveTrackColor,
            start = Offset(0f, center.y),
            end = Offset(size.width - 0f, center.y),
            strokeWidth = trackWidth,
            cap = StrokeCap.Round
        )

        // 2. Draw SponsorBlock segments
        if (sponsorBlockData.isEnabled && duration > 0) {
            sponsorBlockData.segments.forEach { segment ->
                val action = sponsorBlockData.getActionFor(segment)
                if (action != SponsorBlockActionType.DO_NOTHING) {
                    // Use SponsorBlockColors instead of hardcoded colors
                    val segmentColor = SponsorBlockColors.hexToColor(
                        SponsorBlockColors.DefaultCategoryColorsHex[segment.category]
                    )
                    val startX = (segment.startTimeMillis / duration.toFloat()) * size.width
                    val endX = (segment.endTimeMillis / duration.toFloat()) * size.width
                    if (startX < endX) { // Ensure start is before end
                        drawLine(
                            color = segmentColor,
                            start = Offset(startX, center.y),
                            end = Offset(endX, center.y),
                            strokeWidth = segmentTrackHeight, // Can be same as trackWidth or slightly different
                            cap = StrokeCap.Butt // Use Butt for segments to avoid rounded ends extending too far
                        )
                    }
                }
            }
        }

        // 3. Draw buffered progress
        val bufferedEndX = size.width * bufferedPercentage / 100f
        if (bufferedEndX > 0) {
            drawLine(
                color = colors.disabledActiveTrackColor,
                start = Offset(0f, center.y),
                end = Offset(bufferedEndX, center.y),
                strokeWidth = trackWidth,
                cap = StrokeCap.Round
            )
        }

        // 4. Draw current playback progress
        val positionEndX = if (duration > 0) size.width * (position / duration.toFloat()) else 0f
        if (positionEndX > 0) {
            drawLine(
                color = colors.activeTrackColor,
                start = Offset(0f, center.y),
                end = Offset(positionEndX, center.y),
                strokeWidth = trackWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
private fun SeekPreview() {
    MaterialTheme {
        SeekBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            duration = 1000,
            position = 300,
            bufferedPercentage = 50,
            sponsorBlockData = VideoPlayerSponsorBlockData(
                isEnabled = true,
                segments = listOf(
                    dev.aaa1115910.bv.entity.sponsorblock.SegmentItem(
                        segmentTimeSeconds = listOf(10f, 20f), // 10s to 20s
                        uuid = "1", category = SponsorBlockCategories.SPONSOR, actionType = "skip"
                    ),
                    dev.aaa1115910.bv.entity.sponsorblock.SegmentItem(
                        segmentTimeSeconds = listOf(40f, 55f), // 40s to 55s
                        uuid = "2", category = SponsorBlockCategories.INTRO, actionType = "skip"
                    ),
                    dev.aaa1115910.bv.entity.sponsorblock.SegmentItem(
                        segmentTimeSeconds = listOf(70f, 75f), // 70s to 75s
                        uuid = "3", category = "non_existent_category", actionType = "skip"
                    )
                ),
                userActions = mapOf(SponsorBlockCategories.SPONSOR to SponsorBlockActionType.AUTO_SKIP)
            )
        )
    }
}