package dev.aaa1115910.bv.tv.screens.settings.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockActionType
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockCategories
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockColors
import dev.aaa1115910.bv.tv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.tv.screens.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.viewmodel.settings.SponsorBlockSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SponsorBlockSetting(
    modifier: Modifier = Modifier,
    viewModel: SponsorBlockSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = SettingsMenuNavItem.SponsorBlock.getDisplayName(context),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Enable/Disable SponsorBlock
            item {
                SettingSwitchListItem(
                    title = "启用 SponsorBlock",
                    supportText = "自动跳过视频中的赞助商内容",
                    checked = viewModel.enabled,
                    onCheckedChange = { 
                        viewModel.setEnableSponsorBlock(it)
                        viewModel.saveSettings()
                    }
                )
            }
            
            // Category settings
            items(SponsorBlockCategories.ALL_CATEGORIES_ORDERED) { category ->
                SponsorBlockCategoryItem(
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
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SponsorBlockCategoryItem(
    modifier: Modifier = Modifier,
    category: String,
    displayName: String,
    currentAction: SponsorBlockActionType,
    currentColor: Color,
    onActionChange: (SponsorBlockActionType) -> Unit
) {
    var showActionSelector by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = { 
            // Cycle through actions
            val nextAction = when (currentAction) {
                SponsorBlockActionType.AUTO_SKIP -> SponsorBlockActionType.MANUAL_SKIP
                SponsorBlockActionType.MANUAL_SKIP -> SponsorBlockActionType.SHOW_MARK_ONLY
                SponsorBlockActionType.SHOW_MARK_ONLY -> SponsorBlockActionType.DO_NOTHING
                SponsorBlockActionType.DO_NOTHING -> SponsorBlockActionType.AUTO_SKIP
            }
            onActionChange(nextAction)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
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
                
                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = getActionDisplayName(currentAction),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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