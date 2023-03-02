package dev.aaa1115910.bv.screen.user

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.util.getDisplayName

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FollowingSeasonFilter(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideFilter: () -> Unit,
    selectedType: FollowingSeasonType,
    selectedStatus: FollowingSeasonStatus,
    onSelectedTypeChange: (FollowingSeasonType) -> Unit,
    onSelectedStatusChange: (FollowingSeasonStatus) -> Unit
) {
    val context = LocalContext.current
    val defaultFocusRequester = remember { FocusRequester() }
    val statusFocusRequester = remember { FocusRequester() }

    val filterRowSpace = 8.dp

    LaunchedEffect(show) {
        if (show) defaultFocusRequester.requestFocus()
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onHideFilter,
            title = { Text(text = stringResource(R.string.filter_dialog_title)) },
            text = {
                Column {
                    TvLazyRow(
                        modifier = Modifier.onPreviewKeyEvent {
                            if (it.key == Key.DirectionDown) {
                                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                    statusFocusRequester.requestFocus()
                                    return@onPreviewKeyEvent true
                                }
                                return@onPreviewKeyEvent true
                            }
                            false
                        },
                        horizontalArrangement = Arrangement.spacedBy(filterRowSpace)
                    ) {
                        items(items = FollowingSeasonType.values()) { type ->
                            FilterDialogFilterChip(
                                focusRequester = defaultFocusRequester,
                                selected = type == selectedType,
                                onClick = { onSelectedTypeChange(type) },
                                label = { Text(text = type.getDisplayName(context)) },
                            )
                        }
                    }
                    TvLazyRow(
                        modifier = Modifier.onPreviewKeyEvent {
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
                        items(items = FollowingSeasonStatus.values()) { status ->
                            FilterDialogFilterChip(
                                focusRequester = statusFocusRequester,
                                selected = status == selectedStatus,
                                onClick = { onSelectedStatusChange(status) },
                                label = { Text(text = status.getDisplayName(context)) }
                            )
                        }
                    }
                }
            },
            confirmButton = {}
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