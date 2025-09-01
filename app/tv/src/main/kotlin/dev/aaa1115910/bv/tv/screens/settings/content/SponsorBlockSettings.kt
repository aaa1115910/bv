package dev.aaa1115910.bv.tv.screens.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.sponsorblock.entity.SegmentCategory
import dev.aaa1115910.bv.sponsorblock.entity.SponsorBlockSetting
import dev.aaa1115910.bv.tv.component.settings.SettingsMenuSelectItem
import dev.aaa1115910.bv.util.Prefs

@Composable
fun SponsorBlockSettings(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(Prefs.enableSponsorBlock) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SponsorBlock",
                style = MaterialTheme.typography.displaySmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "启用 SponsorBlock")
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                        Prefs.enableSponsorBlock = it
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = SegmentCategory.entries) { category ->
                    var selectedSetting by remember { mutableStateOf(Prefs.getSponsorBlockSetting(category)) }
                    Text(text = category.title)
                    Row {
                        SponsorBlockSetting.entries.forEach { setting ->
                            SettingsMenuSelectItem(
                                text = setting.displayName,
                                selected = selectedSetting == setting,
                                onClick = {
                                    selectedSetting = setting
                                    Prefs.setSponsorBlockSetting(category, setting)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
