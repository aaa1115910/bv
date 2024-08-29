package dev.aaa1115910.bv.component.index

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.http.entity.index.IndexOrder
import dev.aaa1115910.biliapi.http.entity.index.animeIndexOrders
import dev.aaa1115910.biliapi.http.entity.index.indexFilterArea
import dev.aaa1115910.biliapi.http.entity.index.indexFilterCopyright
import dev.aaa1115910.biliapi.http.entity.index.indexFilterIsFinish
import dev.aaa1115910.biliapi.http.entity.index.indexFilterSeasonMonth
import dev.aaa1115910.biliapi.http.entity.index.indexFilterSeasonStatus
import dev.aaa1115910.biliapi.http.entity.index.indexFilterSeasonVersion
import dev.aaa1115910.biliapi.http.entity.index.indexFilterSpokenLanguageType
import dev.aaa1115910.biliapi.http.entity.index.indexFilterStyleIdsAnime
import dev.aaa1115910.biliapi.http.entity.index.indexFilterYear
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun AnimeIndexFilter(
    modifier: Modifier = Modifier,
    show: Boolean,
    onDismissRequest: () -> Unit,
    order: IndexOrder,
    seasonVersion: Int,
    spokenLanguageType: Int,
    area: Int,
    isFinish: Int,
    copyright: Int,
    seasonStatus: Int,
    seasonMonth: Int,
    year: String,
    styleId: Int,
    desc: Boolean,
    onOrderChange: (IndexOrder) -> Unit,
    onSeasonVersionChange: (Int) -> Unit,
    onSpokenLanguageTypeChange: (Int) -> Unit,
    onAreaChange: (Int) -> Unit,
    onIsFinishChange: (Int) -> Unit,
    onCopyrightChange: (Int) -> Unit,
    onSeasonStatusChange: (Int) -> Unit,
    onSeasonMonthChange: (Int) -> Unit,
    onYearChange: (String) -> Unit,
    onStyleIdChange: (Int) -> Unit,
    onDescChange: (Boolean) -> Unit,
    // TODO 重置筛选条件
    onReset: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        runCatching {
            if (show) focusRequester.requestFocus()
        }
    }

    if (show) {
        AlertDialog(
            modifier = modifier
                .fillMaxWidth(0.8f),
            onDismissRequest = onDismissRequest,
            confirmButton = { },
            title = {
                Text(text = "番剧索引筛选")
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                ) {
                    IndexFilterChipRow(
                        modifier = Modifier.focusRequester(focusRequester),
                        title = "排序方式",
                        filter = animeIndexOrders.associateWith { it.getDisplayName(context) },
                        selectedFilterId = order,
                        onFilterIdChange = onOrderChange
                    )
                    IndexFilterChipRow(
                        title = "排序顺序",
                        filter = mapOf(true to "降序", false to "升序"),
                        selectedFilterId = desc,
                        onFilterIdChange = onDescChange
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyColumn {
                        item {
                            IndexFilterChipRow(
                                title = "类型",
                                filter = indexFilterSeasonVersion,
                                selectedFilterId = seasonVersion,
                                onFilterIdChange = onSeasonVersionChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "配音",
                                filter = indexFilterSpokenLanguageType,
                                selectedFilterId = spokenLanguageType,
                                onFilterIdChange = onSpokenLanguageTypeChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "地区",
                                filter = indexFilterArea,
                                selectedFilterId = area,
                                onFilterIdChange = onAreaChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "状态",
                                filter = indexFilterIsFinish,
                                selectedFilterId = isFinish,
                                onFilterIdChange = onIsFinishChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "版权",
                                filter = indexFilterCopyright,
                                selectedFilterId = copyright,
                                onFilterIdChange = onCopyrightChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "付费",
                                filter = indexFilterSeasonStatus,
                                selectedFilterId = seasonStatus,
                                onFilterIdChange = onSeasonStatusChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "季度",
                                filter = indexFilterSeasonMonth,
                                selectedFilterId = seasonMonth,
                                onFilterIdChange = onSeasonMonthChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "年份",
                                filter = indexFilterYear,
                                selectedFilterId = year,
                                onFilterIdChange = onYearChange
                            )
                        }
                        item {
                            IndexFilterChipRow(
                                title = "风格",
                                filter = indexFilterStyleIdsAnime,
                                selectedFilterId = styleId,
                                onFilterIdChange = onStyleIdChange
                            )
                        }
                    }
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)

        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun AnimeIndexFilterPreview() {
    var order by remember { mutableStateOf(IndexOrder.PlayCount) }
    var seasonVersion by remember { mutableStateOf(-1) }
    var spokenLanguageType by remember { mutableStateOf(-1) }
    var area by remember { mutableStateOf(-1) }
    var isFinish by remember { mutableStateOf(-1) }
    var copyright by remember { mutableStateOf(-1) }
    var seasonStatus by remember { mutableStateOf(-1) }
    var seasonMonth by remember { mutableStateOf(-1) }
    var year by remember { mutableStateOf("-1") }
    var styleId by remember { mutableStateOf(-1) }
    var desc by remember { mutableStateOf(true) }

    BVTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimeIndexFilter(
                show = true,
                onDismissRequest = { },
                order = order,
                seasonVersion = seasonVersion,
                spokenLanguageType = spokenLanguageType,
                area = area,
                isFinish = isFinish,
                copyright = copyright,
                seasonStatus = seasonStatus,
                seasonMonth = seasonMonth,
                year = year,
                styleId = styleId,
                desc = desc,
                onOrderChange = { order = it },
                onSeasonVersionChange = { seasonVersion = it },
                onSpokenLanguageTypeChange = { spokenLanguageType = it },
                onAreaChange = { area = it },
                onIsFinishChange = { isFinish = it },
                onCopyrightChange = { copyright = it },
                onSeasonStatusChange = { seasonStatus = it },
                onSeasonMonthChange = { seasonMonth = it },
                onYearChange = { year = it },
                onStyleIdChange = { styleId = it },
                onDescChange = { desc = it },
                onReset = { }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun IndexFilterChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = selected) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
            Text(text = label)
        }
    }
}

@Composable
fun <T> IndexFilterChipRow(
    modifier: Modifier = Modifier,
    title: String,
    filter: Map<T, String>,
    selectedFilterId: T,
    onFilterIdChange: (T) -> Unit
) {
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
        LazyRow(
            modifier = modifier
                .then(focusRestorerModifiers.parentModifier),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            items(items = filter.entries.toList()) { (id, label) ->
                IndexFilterChip(
                    modifier = Modifier
                        .ifElse(selectedFilterId == id, focusRestorerModifiers.childModifier),
                    selected = selectedFilterId == id,
                    onClick = { onFilterIdChange(id) },
                    label = label
                )
            }
        }
    }
}

fun IndexOrder.getDisplayName(context: Context) = when (this) {
    IndexOrder.UpdateTime -> context.getString(R.string.index_order_update_time)
    IndexOrder.DanmakuCount -> context.getString(R.string.index_order_danmaku_count)
    IndexOrder.PlayCount -> context.getString(R.string.index_order_play_count)
    IndexOrder.FollowCount -> context.getString(R.string.index_order_follow_count)
    IndexOrder.Score -> context.getString(R.string.index_order_score)
    IndexOrder.StartTime -> context.getString(R.string.index_order_start_time)
    IndexOrder.PublishTime -> context.getString(R.string.index_order_publish_time)
}