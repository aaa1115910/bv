package dev.aaa1115910.bv.screen.settings.content

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem

@Composable
fun InfoSetting(
    modifier: Modifier = Modifier,
    maxHeight: Dp,
    maxWidth: Dp
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val getMemoryInfo: () -> String = {
        val memoryInfo = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getMemoryInfo(memoryInfo)
        "${memoryInfo.availMem / (1024 * 1024)}MB / ${memoryInfo.totalMem / (1024 * 1024)}MB"
    }

    val getStorageInfo: () -> String = {
        runCatching {
            val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
            "${statFs.availableBytes / (1024 * 1024)}MB / ${statFs.totalBytes / (1024 * 1024)}MB"
        }.getOrDefault("Unknown")
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
                    R.string.settings_info_resolution,
                    with(density) { maxWidth.toPx() }.toInt(),
                    with(density) { maxHeight.toPx() }.toInt()
                )
            )
            if (Build.VERSION.SDK_INT >= 31)
                Text(
                    text = stringResource(
                        R.string.settings_info_soc, Build.SOC_MANUFACTURER, Build.SOC_MODEL
                    )
                )
            Text(text = stringResource(R.string.settings_info_memory, getMemoryInfo()))
            Text(text = stringResource(R.string.settings_info_storage, getStorageInfo()))
        }
    }
}