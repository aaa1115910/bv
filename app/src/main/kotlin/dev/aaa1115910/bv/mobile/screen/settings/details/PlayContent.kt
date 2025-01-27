package dev.aaa1115910.bv.mobile.screen.settings.details

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.schnettler.datastore.manager.DataStoreManager
import dev.aaa1115910.bv.dataStore
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.mobile.component.preferences.RadioPreferenceItem
import dev.aaa1115910.bv.mobile.component.preferences.getOrDefault
import dev.aaa1115910.bv.util.PrefKeys

@Composable
fun PlayContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(LocalContext.current.dataStore)
    val prefs by dataStoreManager.preferenceFlow.collectAsState(initial = null)

    Column(
        modifier = modifier
    ) {
        RadioPreferenceItem(
            title = "默认画质",
            items = Resolution.entries.associate { it.code to it.getDisplayName(context) },
            prefReq = PrefKeys.prefDefaultQualityRequest,
            summary = Resolution.fromCode(prefs.getOrDefault(PrefKeys.prefDefaultQualityRequest))
                ?.getDisplayName(context) ?: ""
        )
        RadioPreferenceItem(
            title = "默认音频",
            items = Audio.entries.associate { it.code to it.getDisplayName(context) },
            prefReq = PrefKeys.prefDefaultAudioRequest,
            summary = Audio.fromCode(prefs.getOrDefault(PrefKeys.prefDefaultAudioRequest))
                ?.getDisplayName(context) ?: ""
        )
    }
}