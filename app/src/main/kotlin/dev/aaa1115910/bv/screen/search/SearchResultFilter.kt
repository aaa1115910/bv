package dev.aaa1115910.bv.screen.search

import android.content.Context
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.util.Partition
import dev.aaa1115910.bv.util.PartitionUtil

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultVideoFilter(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideFilter: () -> Unit,
    selectedOrder: SearchResultFilterOrderType,
    selectedDuration: SearchResultFilterDuration,
    selectedPartition: Partition?,
    selectedChildPartition: Partition?,
    onSelectedOrderChange: (SearchResultFilterOrderType) -> Unit,
    onSelectedDurationChange: (SearchResultFilterDuration) -> Unit,
    onSelectedPartitionChange: (Partition?) -> Unit,
    onSelectedChildPartitionChange: (Partition?) -> Unit,
) {
    val context = LocalContext.current
    val partitions = remember { PartitionUtil.partitions }
    val defaultFocusRequester = remember { FocusRequester() }
    val durationFocusRequester = remember { FocusRequester() }
    val partitionFocusRequester = remember { FocusRequester() }
    val partitionChildFocusRequester = remember { FocusRequester() }

    val filterRowSpace = 8.dp

    LaunchedEffect(show) {
        if (show) defaultFocusRequester.requestFocus()
    }

    if (show) {
        AlertDialog(
            modifier = modifier
                .fillMaxWidth(0.8f),
            onDismissRequest = onHideFilter,
            title = { Text(text = stringResource(R.string.search_result_video_filter_title)) },
            text = {
                Column {
                    TvLazyRow(
                        modifier = Modifier.onPreviewKeyEvent {
                            if (it.key == Key.DirectionDown) {
                                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                    durationFocusRequester.requestFocus()
                                    return@onPreviewKeyEvent true
                                }
                                return@onPreviewKeyEvent true
                            }
                            false
                        },
                        horizontalArrangement = Arrangement.spacedBy(filterRowSpace)
                    ) {
                        items(items = SearchResultFilterOrderType.values()) { orderType ->
                            FilterDialogFilterChip(
                                focusRequester = defaultFocusRequester,
                                selected = orderType == selectedOrder,
                                onClick = { onSelectedOrderChange(orderType) },
                                label = { Text(text = orderType.getDisplayName(context)) },
                            )
                        }
                    }
                    TvLazyRow(
                        modifier = Modifier.onPreviewKeyEvent {
                            if (it.key == Key.DirectionDown) {
                                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                    partitionFocusRequester.requestFocus()
                                    return@onPreviewKeyEvent true
                                }
                                return@onPreviewKeyEvent true
                            }
                            if (it.key == Key.DirectionUp) {
                                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                    defaultFocusRequester.requestFocus()
                                    return@onPreviewKeyEvent true
                                }
                                return@onPreviewKeyEvent true
                            }
                            false
                        },
                        horizontalArrangement = Arrangement.spacedBy(filterRowSpace)
                    ) {
                        items(items = SearchResultFilterDuration.values()) { duration ->
                            FilterDialogFilterChip(
                                focusRequester = durationFocusRequester,
                                selected = duration == selectedDuration,
                                onClick = { onSelectedDurationChange(duration) },
                                label = { Text(text = duration.getDisplayName(context)) }
                            )
                        }
                    }
                    TvLazyRow(
                        modifier = Modifier.onPreviewKeyEvent {
                            if (it.key == Key.DirectionDown) {
                                if (selectedChildPartition == null) return@onPreviewKeyEvent false
                                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                    partitionChildFocusRequester.requestFocus()
                                    return@onPreviewKeyEvent true
                                }
                                return@onPreviewKeyEvent true
                            }
                            if (it.key == Key.DirectionUp) {
                                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                    durationFocusRequester.requestFocus()
                                    return@onPreviewKeyEvent true
                                }
                                return@onPreviewKeyEvent true
                            }
                            false
                        },
                        horizontalArrangement = Arrangement.spacedBy(filterRowSpace)
                    ) {
                        item {
                            FilterDialogFilterChip(
                                focusRequester = partitionFocusRequester,
                                selected = null == selectedPartition,
                                onClick = {
                                    onSelectedPartitionChange(null)
                                    onSelectedChildPartitionChange(null)
                                },
                                label = { Text(text = "全部分区") }
                            )
                        }
                        items(items = partitions) { partition ->
                            FilterDialogFilterChip(
                                focusRequester = partitionFocusRequester,
                                selected = partition == selectedPartition,
                                onClick = {
                                    onSelectedPartitionChange(partition)
                                    onSelectedChildPartitionChange(null)
                                },
                                label = { Text(text = partition.strRes) }
                            )
                        }
                    }
                    AnimatedVisibility(visible = selectedPartition != null) {
                        TvLazyRow(
                            modifier = Modifier.onPreviewKeyEvent {
                                if (it.key == Key.DirectionUp) {
                                    if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                        partitionFocusRequester.requestFocus()
                                        return@onPreviewKeyEvent true
                                    }
                                    return@onPreviewKeyEvent true
                                }
                                false
                            },
                            horizontalArrangement = Arrangement.spacedBy(filterRowSpace)
                        ) {
                            items(items = selectedPartition?.children ?: emptyList()) { partition ->
                                FilterDialogFilterChip(
                                    focusRequester = partitionChildFocusRequester,
                                    selected = partition == selectedChildPartition,
                                    onClick = {
                                        onSelectedChildPartitionChange(
                                            if (partition != selectedChildPartition) partition
                                            else null
                                        )
                                    },
                                    label = { Text(text = partition.strRes) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }

    BackHandler(
        enabled = show,
        onBack = onHideFilter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDialogFilterChip(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
) {
    var hasFocus by remember { mutableStateOf(false) }
    val focusRequesterModifier = if (selected)
        modifier.focusRequester(focusRequester)
    else modifier

    FilterChip(
        modifier = focusRequesterModifier.onFocusChanged { hasFocus = it.hasFocus },
        selected = selected,
        onClick = onClick,
        label = label,
        border = if (hasFocus) FilterChipDefaults.filterChipBorder(
            borderColor = Color.White,
            borderWidth = 2.dp,
            selectedBorderColor = Color.White,
            selectedBorderWidth = 2.dp
        )
        else FilterChipDefaults.filterChipBorder()
    )
}

enum class SearchResultFilterOrderType(val order: String?, private val strRes: Int) {
    ComprehensiveSort(null, R.string.search_result_filter_order_type_comprehensive_sort),
    MostClicks("click", R.string.search_result_filter_order_type_most_clicks),
    LatestPublish("pubdate", R.string.search_result_filter_order_type_latest_publish),
    MostDanmaku("dm", R.string.search_result_filter_order_type_most_danmaku),
    MostFavorites("stow", R.string.search_result_filter_order_type_most_favorites);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

enum class SearchResultFilterDuration(val duration: Int?, private val strRes: Int) {
    All(null, R.string.search_result_filter_duration_all),
    TimeLessThan10(1, R.string.search_result_filter_duration_less_than_10),
    Time10To30(2, R.string.search_result_filter_duration_10_to_30),
    Time30To60(3, R.string.search_result_filter_duration_30_to_60),
    TimeMoreThan60(4, R.string.search_result_filter_duration_more_than_60);

    fun getDisplayName(context: Context) = context.getString(strRes)
}
