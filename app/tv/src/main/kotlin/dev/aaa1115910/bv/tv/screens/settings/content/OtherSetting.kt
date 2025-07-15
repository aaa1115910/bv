package dev.aaa1115910.bv.tv.screens.settings.content

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.tv.component.settings.SettingListItem
import dev.aaa1115910.bv.tv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.tv.component.settings.SettingNumberListItem
import dev.aaa1115910.bv.tv.activities.settings.LogsActivity
import dev.aaa1115910.bv.tv.screens.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.util.FirebaseUtil
import dev.aaa1115910.bv.util.Prefs

@Composable
fun OtherSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showFps by remember { mutableStateOf(Prefs.showFps) }
    var updateAlpha by remember { mutableStateOf(Prefs.updateAlpha) }
    var enableFfmpegAudioRenderer by remember { mutableStateOf(Prefs.enableFfmpegAudioRenderer) }
    var portraitVideoQualityLimitMax1080P by remember { mutableStateOf(Prefs.portraitVideoQualityLimitMax1080P) }
    var playerAutoPlayNextVideo by remember { mutableStateOf(Prefs.playerAutoPlayNextVideo) }
    var playerExitWhenAllIsPlayed by remember { mutableStateOf(Prefs.playerExitWhenAllIsPlayed) }
    var playerShowBottomProgressBar by remember { mutableStateOf(Prefs.playerShowBottomProgressBar) }
    var defaultPlaybackSpeed by remember { mutableStateOf(Prefs.defaultPlaySpeed.toDouble()) }
    var playerSeekStep by remember { mutableStateOf(Prefs.playerSeekStep.toDouble()) }
    var showUGCVideoInfo by remember { mutableStateOf(Prefs.showUGCVideoInfo) }
    var playerShowDebugInfo by remember { mutableStateOf(Prefs.playerShowDebugInfo) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = SettingsMenuNavItem.Other.getDisplayName(context),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_firebase_title),
                    supportText = stringResource(R.string.settings_other_firebase_text),
                    checked = Prefs.enableFirebaseCollection,
                    onCheckedChange = {
                        Prefs.enableFirebaseCollection = it
                        FirebaseUtil.setCrashlyticsCollectionEnabled(it)
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_fps_title),
                    supportText = stringResource(R.string.settings_other_fps_text),
                    checked = showFps,
                    onCheckedChange = {
                        showFps = it
                        Prefs.showFps = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_alpha_title),
                    supportText = stringResource(R.string.settings_other_alpha_text),
                    checked = updateAlpha,
                    onCheckedChange = {
                        updateAlpha = it
                        Prefs.updateAlpha = it
                    }
                )
            }
            item {
                SettingListItem(
                    title = stringResource(R.string.settings_create_logs_title),
                    supportText = stringResource(R.string.settings_create_logs_text),
                    onClick = {
                        context.startActivity(Intent(context, LogsActivity::class.java))
                    }
                )
            }
            if (BuildConfig.DEBUG) {
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_crash_test_title),
                        supportText = stringResource(R.string.settings_crash_test_text),
                        onClick = {
                            throw Exception("Boom!")
                        }
                    )
                }
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_ffmpeg_audio_renderer_title),
                    supportText = stringResource(R.string.settings_other_ffmpeg_audio_renderer_text),
                    checked = enableFfmpegAudioRenderer,
                    onCheckedChange = {
                        enableFfmpegAudioRenderer = it
                        Prefs.enableFfmpegAudioRenderer = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_show_ugc_video_info_title),
                    supportText = stringResource(R.string.settings_show_ugc_video_info_text),
                    checked = showUGCVideoInfo,
                    onCheckedChange = {
                        showUGCVideoInfo = it
                        Prefs.showUGCVideoInfo = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_show_debug_info_title),
                    supportText = stringResource(R.string.settings_player_show_debug_info_text),
                    checked = playerShowDebugInfo,
                    onCheckedChange = {
                        playerShowDebugInfo = it
                        Prefs.playerShowDebugInfo = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_portrait_video_quality_title),
                    supportText = stringResource(R.string.settings_other_portrait_video_quality_text),
                    checked = portraitVideoQualityLimitMax1080P,
                    onCheckedChange = {
                        portraitVideoQualityLimitMax1080P = it
                        Prefs.portraitVideoQualityLimitMax1080P = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_auto_play_next_video_title),
                    supportText = stringResource(R.string.settings_player_auto_play_next_video_text),
                    checked = playerAutoPlayNextVideo,
                    onCheckedChange = {
                        playerAutoPlayNextVideo = it
                        Prefs.playerAutoPlayNextVideo = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_exit_when_all_is_played_title),
                    supportText = stringResource(R.string.settings_player_exit_when_all_is_played_text),
                    checked = playerExitWhenAllIsPlayed,
                    onCheckedChange = {
                        playerExitWhenAllIsPlayed = it
                        Prefs.playerExitWhenAllIsPlayed = it
                    }
                )
            }
            item {
                SettingNumberListItem(
                    title = stringResource(R.string.settings_player_default_playback_speed_title),
                    supportText = stringResource(R.string.settings_player_default_playback_speed_text),
                    value = defaultPlaybackSpeed,
                    minValue = 0.25,
                    maxValue = 2.5,
                    isInteger = false,
                    step = 0.25,
                    onValueChange = {
                        defaultPlaybackSpeed = it
                        Prefs.defaultPlaySpeed = it.toFloat()
                    }
                )
            }
            item {
                SettingNumberListItem(
                    title = stringResource(R.string.settings_player_seek_step_title),
                    supportText = stringResource(R.string.settings_player_seek_step_text),
                    value = playerSeekStep,
                    minValue = 5.0,
                    maxValue = 30.0,
                    isInteger = true,
                    step = 5.0,
                    onValueChange = {
                        playerSeekStep = it
                        Prefs.playerSeekStep = it.toInt()
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_show_bottom_progress_bar_title),
                    supportText = stringResource(R.string.settings_player_show_bottom_progress_bar_text),
                    checked = playerShowBottomProgressBar,
                    onCheckedChange = {
                        playerShowBottomProgressBar = it
                        Prefs.playerShowBottomProgressBar = it
                    }
                )
            }
        }
    }
}