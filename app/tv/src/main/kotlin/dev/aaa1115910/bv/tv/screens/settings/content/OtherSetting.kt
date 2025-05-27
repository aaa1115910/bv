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
import dev.aaa1115910.bv.component.settings.CookiesDialog
import dev.aaa1115910.bv.component.settings.SettingListItem
import dev.aaa1115910.bv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.player.entity.Resolution
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
    var playerExitWhenAllIsPlayed by remember { mutableStateOf(Prefs.playerExitWhenAllIsPlayed) }

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
                    title = stringResource(R.string.settings_player_exit_when_all_is_played_title),
                    supportText = stringResource(R.string.settings_player_exit_when_all_is_played_text),
                    checked = playerExitWhenAllIsPlayed,
                    onCheckedChange = {
                        playerExitWhenAllIsPlayed = it
                        Prefs.playerExitWhenAllIsPlayed = it
                    }
                )
            }
        }
    }
}