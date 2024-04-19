package dev.aaa1115910.bv.mobile.screen.settings.details

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.schnettler.datastore.manager.DataStoreManager
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.bv.dataStore
import dev.aaa1115910.bv.mobile.component.preferences.RadioPreferenceItem
import dev.aaa1115910.bv.mobile.component.preferences.getOrDefault
import dev.aaa1115910.bv.util.PrefKeys

@Composable
fun AdvanceContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(LocalContext.current.dataStore)
    val prefs by dataStoreManager.preferenceFlow.collectAsState(initial = null)

    Column(modifier = modifier) {
        RadioPreferenceItem(
            title = "接口偏好",
            items = ApiType.entries.associate { it.ordinal to it.name },
            prefReq = PrefKeys.prefApiTypeRequest,
            summary = ApiType.entries[prefs.getOrDefault(PrefKeys.prefApiTypeRequest)].name
        )
    }
}