package dev.aaa1115910.bv.tv.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.player.entity.sponsorblock.SponsorBlockActionType
import dev.aaa1115910.bv.player.entity.sponsorblock.SponsorBlockCategories
import dev.aaa1115910.bv.player.entity.sponsorblock.SponsorBlockColors
import dev.aaa1115910.bv.tv.component.settings.SettingsMenuSelectItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.stringResId
import dev.aaa1115910.bv.viewmodel.settings.SponsorBlockSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SponsorBlockScreen(
    modifier: Modifier = Modifier,
    viewModel: SponsorBlockSettingsViewModel = koinViewModel()
) {
    var currentCategory by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    SponsorBlockContent(
        modifier = modifier,
        enabled = viewModel.enabled,
        categoryActions = viewModel.categoryActions,
        categoryColors = viewModel.categoryColors,
        currentCategory = currentCategory,
        onCategoryChange = { currentCategory = it },
        onCategoryActionChange = { category, action ->
            viewModel.setActionForCategory(category, action)
            viewModel.saveSettings()
        },
        onEnabledChange = {
            viewModel.setEnableSponsorBlock(it)
            viewModel.saveSettings()
        }
    )
}

@Composable
private fun SponsorBlockContent(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    categoryActions: Map<String, SponsorBlockActionType>,
    categoryColors: Map<String, String>,
    currentCategory: String,
    onCategoryChange: (String) -> Unit,
    onCategoryActionChange: (String, SponsorBlockActionType) -> Unit,
    onEnabledChange: (Boolean) -> Unit
) {
    val categoryFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = dev.aaa1115910.bv.tv.R.string.title_activity_sponsor_block),
                        fontSize = 48.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(3f)
                    .focusRestorer(categoryFocusRequester),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ListItem(
                        modifier = Modifier
                            .focusRequester(categoryFocusRequester)
                            .onFocusChanged {
                                if (it.hasFocus) onCategoryChange("")
                            },
                        selected = false,
                        onClick = { onEnabledChange(!enabled) },
                        headlineContent = { Text(text = "启用 SponsorBlock") },
                        supportingContent = { Text(text = "自动跳过视频中的赞助商内容") },
                        trailingContent = {
                            Switch(
                                checked = enabled,
                                onCheckedChange = onEnabledChange
                            )
                        }
                    )
                }
                items(SponsorBlockCategories.ALL_CATEGORIES_ORDERED) { category ->
                    SponsorBlockCategoryItem(
                        modifier = Modifier.onFocusChanged {
                            if (it.hasFocus) onCategoryChange(category)
                        },
                        title = SponsorBlockCategories.getDisplayName(category),
                        actionType = categoryActions[category]
                            ?: SponsorBlockCategories.DEFAULT_ACTIONS[category]
                            ?: SponsorBlockActionType.DO_NOTHING,
                        currentColor = SponsorBlockColors.hexToColor(
                            categoryColors[category]
                                ?: SponsorBlockColors.DefaultCategoryColorsHex[category]
                        ),
                        enabled = enabled,
                        selected = currentCategory == category
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(5f)
                    .padding(24.dp)
            ) {
                if (currentCategory.isEmpty()) {
                    SponsorBlockTip(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    SponsorBlockActionTypeList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp)
                            .padding(horizontal = 48.dp),
                        title = SponsorBlockCategories.getDisplayName(currentCategory),
                        currentAction = categoryActions[currentCategory]
                            ?: SponsorBlockCategories.DEFAULT_ACTIONS[currentCategory]
                            ?: SponsorBlockActionType.DO_NOTHING,
                        onActionChange = { action ->
                            onCategoryActionChange(currentCategory, action)
                        }
                    )
                }
            }

        }
    }
}

@Composable
private fun SponsorBlockActionTypeList(
    modifier: Modifier = Modifier,
    title: String,
    currentAction: SponsorBlockActionType,
    onActionChange: (SponsorBlockActionType) -> Unit
) {
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
                text = title,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SponsorBlockActionType.entries) { actionType ->
                    SettingsMenuSelectItem(
                        text = stringResource(actionType.stringResId()),
                        selected = currentAction == actionType,
                        onClick = { onActionChange(actionType) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SponsorBlockCategoryItem(
    modifier: Modifier = Modifier,
    title: String,
    actionType: SponsorBlockActionType,
    currentColor: Color,
    selected: Boolean = false,
    enabled: Boolean = true
) {
    ListItem(
        modifier = modifier,
        selected = selected,
        enabled = enabled,
        onClick = {},
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = stringResource(actionType.stringResId())) },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(currentColor)
            )
        }
    )
}


@Composable
private fun SponsorBlockTip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Image(
                modifier = Modifier
                    .size(128.dp),
                bitmap = ImageBitmap.imageResource(id = R.drawable.ic_sponsor_blocker),
                contentDescription = "Sponsor Block Logo",
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            text = "https://bsbsb.top"
        )
    }
}

private class CategoryPreviewParameterProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf("", "sponsor")
}

@Preview(device = "id:tv_1080p")
@Preview(device = "id:tv_1080p", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SponsorBlockContentPreview(
    @PreviewParameter(CategoryPreviewParameterProvider::class) category: String
) {
    BVTheme {
        SponsorBlockContent(
            enabled = true,
            categoryActions = mapOf(
                "sponsor" to SponsorBlockActionType.AUTO_SKIP,
                "intro" to SponsorBlockActionType.MANUAL_SKIP,
                "outro" to SponsorBlockActionType.SHOW_MARK_ONLY
            ),
            categoryColors = mapOf(
                "sponsor" to "#FF0000",
                "intro" to "#00FF00",
                "outro" to "#0000FF"
            ),
            currentCategory = category,
            onCategoryChange = {},
            onCategoryActionChange = { _, _ -> },
            onEnabledChange = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SponsorBlockTipPreview() {
    BVTheme {
        SponsorBlockTip(
            modifier = Modifier.size(300.dp)
        )
    }
}