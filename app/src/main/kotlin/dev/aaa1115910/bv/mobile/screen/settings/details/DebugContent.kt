package dev.aaa1115910.bv.mobile.screen.settings.details

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.schnettler.datastore.manager.DataStoreManager
import dev.aaa1115910.bv.dataStore
import dev.aaa1115910.bv.mobile.component.preferences.SwitchPreferenceItem
import dev.aaa1115910.bv.util.PrefKeys

@Composable
fun DebugContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(LocalContext.current.dataStore)
    val prefs by dataStoreManager.preferenceFlow.collectAsState(initial = null)

    Column(
        modifier = modifier
    ) {
        SwitchPreferenceItem(
            title = "使用旧版播放器（TV）",
            summary = "此处对移动端没有任何作用，禁用于组件测试",
            prefReq = PrefKeys.prefShowFpsRequest
        )
    }
}