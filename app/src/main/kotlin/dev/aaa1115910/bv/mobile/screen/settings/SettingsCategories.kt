package dev.aaa1115910.bv.mobile.screen.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCategories(
    modifier: Modifier = Modifier,
    selectedSettings: MobileSettings?,
    onSelectedSettings: (MobileSettings) -> Unit,
    showNavBack: Boolean,
    onBack: () -> Unit,
    singleList: Boolean
) {
    val containerColor =
        if (singleList) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.let {
            val deepening = 0.5f
            Color(it.red * deepening, it.green * deepening, it.blue * deepening)
        }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }.takeIf { showNavBack }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = containerColor
                )
            )
        },
        containerColor = containerColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(MobileSettings.entries) { item ->
                SettingItem(
                    modifier = Modifier,
                    text = item.name,
                    selected = selectedSettings == item,
                    onClick = {
                        onSelectedSettings(item)
                    },
                    defaultContainerColor = containerColor
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit = {},
    defaultContainerColor: Color = MaterialTheme.colorScheme.surface
) {
    val listItemColors = ListItemDefaults.colors(
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else defaultContainerColor
    )
    ListItem(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        headlineContent = {
            Text(
                text = text,
                //style = MaterialTheme.typography.titleLarge
            )
        },
        colors = listItemColors
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingItemPreview() {
    BVMobileTheme {
        Surface {
            Box(modifier = Modifier.padding(20.dp)) {
                SettingItem(
                    text = "Play",
                    selected = false
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SelectedSettingItemPreview() {
    BVMobileTheme {
        Surface {
            Box(modifier = Modifier.padding(20.dp)) {
                SettingItem(
                    text = "Play",
                    selected = true
                )
            }
        }
    }
}