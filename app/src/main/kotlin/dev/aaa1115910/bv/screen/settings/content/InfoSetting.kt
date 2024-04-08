package dev.aaa1115910.bv.screen.settings.content

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.settings.MediaCodecActivity
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem
import java.text.DecimalFormat
import kotlin.math.pow


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun InfoSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val memoryInfo by remember {
        mutableStateOf(
            lazy {
                runCatching {
                    val memoryInfo = ActivityManager.MemoryInfo()
                    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                        .getMemoryInfo(memoryInfo)
                    val df = DecimalFormat("###.##")
                    Pair(
                        "${df.format(memoryInfo.availMem / 1024.0.pow(3))} GB",
                        "${df.format(memoryInfo.totalMem / 1024.0.pow(3))} GB"
                    )
                }.getOrDefault(Pair("Unknown", "Unknown"))
            }.value
        )
    }

    val storageInfo by remember {
        mutableStateOf(
            lazy {
                runCatching {
                    val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
                    val df = DecimalFormat("###.##")
                    Pair(
                        "${df.format(statFs.availableBytes / 1024.0.pow(3))} GB",
                        "${df.format(statFs.totalBytes / 1024.0.pow(3))} GB"
                    )
                }.getOrDefault(Pair("Unknown", "Unknown"))
            }.value
        )
    }

    @Suppress("DEPRECATION")
    val screenInfo by remember {
        mutableStateOf(
            lazy {
                val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    context.display!!
                } else {
                    (context as Activity).windowManager.defaultDisplay
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val mode = display.mode
                    Triple(mode.physicalWidth, mode.physicalHeight, mode.refreshRate)
                } else {
                    Triple(display.width, display.height, display.refreshRate)
                }
            }.value
        )
    }



    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = SettingsMenuNavItem.Info.getDisplayName(context),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(R.string.settings_info_manufacturer, Build.MANUFACTURER))
            Text(
                text = stringResource(R.string.settings_info_model, Build.MODEL, Build.PRODUCT)
            )
            Text(text = stringResource(R.string.settings_info_system, Build.VERSION.RELEASE))
            Text(
                text = stringResource(
                    R.string.settings_info_screen, *screenInfo.toList().toTypedArray()
                )
            )
            if (Build.VERSION.SDK_INT >= 31)
                Text(
                    text = stringResource(
                        R.string.settings_info_soc, Build.SOC_MANUFACTURER, Build.SOC_MODEL
                    )
                )
            Text(
                text = stringResource(
                    R.string.settings_info_memory,
                    *memoryInfo.toList().toTypedArray()
                )
            )
            Text(
                text = stringResource(
                    R.string.settings_info_storage,
                    *storageInfo.toList().toTypedArray()
                )
            )
        }
        Button(onClick = {
            context.startActivity(Intent(context, MediaCodecActivity::class.java))
        }) {
            Text(stringResource(id = R.string.title_activity_media_codec))
        }
    }
}