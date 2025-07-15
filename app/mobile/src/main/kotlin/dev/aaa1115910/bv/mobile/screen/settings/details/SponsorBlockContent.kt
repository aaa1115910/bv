package dev.aaa1115910.bv.mobile.screen.settings.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aaa1115910.bv.player.entity.sponsorblock.SponsorBlockActionType
import dev.aaa1115910.bv.player.entity.sponsorblock.SponsorBlockCategories
import dev.aaa1115910.bv.player.entity.sponsorblock.SponsorBlockColors
import dev.aaa1115910.bv.viewmodel.settings.SponsorBlockSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SponsorBlockContent(
    modifier: Modifier = Modifier,
    viewModel: SponsorBlockSettingsViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Enable/Disable SponsorBlock
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "启用 SponsorBlock",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "自动跳过视频中的赞助商内容",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = viewModel.enabled,
                        onCheckedChange = {
                            viewModel.setEnableSponsorBlock(it)
                            viewModel.saveSettings()
                        }
                    )
                }
            }
        }

        // Category settings
        items(SponsorBlockCategories.ALL_CATEGORIES_ORDERED) { category ->
            SponsorBlockCategoryCard(
                category = category,
                displayName = SponsorBlockCategories.getDisplayName(category),
                currentAction = viewModel.categoryActions[category]
                    ?: SponsorBlockCategories.DEFAULT_ACTIONS[category]
                    ?: SponsorBlockActionType.DO_NOTHING,
                currentColor = SponsorBlockColors.hexToColor(
                    viewModel.categoryColors[category]
                        ?: SponsorBlockColors.DefaultCategoryColorsHex[category]
                ),
                onActionChange = { action ->
                    viewModel.setActionForCategory(category, action)
                    viewModel.saveSettings()
                }
            )
        }
    }
}

@Composable
private fun SponsorBlockCategoryCard(
    modifier: Modifier = Modifier,
    category: String,
    displayName: String,
    currentAction: SponsorBlockActionType,
    currentColor: Color,
    onActionChange: (SponsorBlockActionType) -> Unit
) {
    var showActionSelector by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { showActionSelector = !showActionSelector }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(currentColor)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = getActionDisplayName(currentAction),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showActionSelector) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SponsorBlockActionType.entries.forEach { action ->
                        TextButton(
                            onClick = {
                                onActionChange(action)
                                showActionSelector = false
                            }
                        ) {
                            Text(
                                text = getActionDisplayName(action),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getActionDisplayName(action: SponsorBlockActionType): String {
    return when (action) {
        SponsorBlockActionType.AUTO_SKIP -> "自动跳过"
        SponsorBlockActionType.MANUAL_SKIP -> "手动跳过"
        SponsorBlockActionType.SHOW_MARK_ONLY -> "仅显示标记"
        SponsorBlockActionType.DO_NOTHING -> "不处理"
    }
} 