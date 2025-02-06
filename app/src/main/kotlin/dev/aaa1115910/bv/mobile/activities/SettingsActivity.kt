package dev.aaa1115910.bv.mobile.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.bv.mobile.component.preferences.SwitchPreferenceItem
import dev.aaa1115910.bv.mobile.screen.settings.SettingsScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.PrefKeys
import dev.aaa1115910.bv.util.Prefs

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVMobileTheme {
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen1() {
    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                Text(text = "Settings")
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(text = "Settings")
            SwitchPreferenceItem(title = "UseOld", prefReq = PrefKeys.prefShowFpsRequest)
            SwitchPreferenceItem(
                title = "UseOld",
                summary = "sssssss",
                prefReq = PrefKeys.prefShowFpsRequest,
                enabled = false
            )
            Text(text = "${Prefs.showFps}")
        }
    }
}