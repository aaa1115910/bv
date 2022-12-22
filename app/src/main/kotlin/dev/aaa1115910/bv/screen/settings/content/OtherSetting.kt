package dev.aaa1115910.bv.screen.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.settings.CookiesDialog
import dev.aaa1115910.bv.component.settings.SettingListItem
import dev.aaa1115910.bv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.util.Prefs

@Composable
fun OtherSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showCookiesDialog by remember { mutableStateOf(false) }

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
        TvLazyColumn(
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
                        Firebase.crashlytics.setCrashlyticsCollectionEnabled(it)
                        FirebaseAnalytics.getInstance(context)
                            .setAnalyticsCollectionEnabled(it)
                    }
                )
            }
            item {
                SettingListItem(
                    title = "Cookies 导入/导出",
                    supportText = "该功能不适合使用遥控器操作",
                    onClick = { showCookiesDialog = true }
                )
            }
        }
    }

    CookiesDialog(
        show = showCookiesDialog,
        onHideDialog = { showCookiesDialog = false }
    )
}