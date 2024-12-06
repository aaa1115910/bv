package dev.aaa1115910.bv.screen.user

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.util.getDisplayName

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
    val row1FocusRestorerModifiers = createCustomInitialFocusRestorerModifiers()
    val row2FocusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    val filterRowSpace = 8.dp

    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onHideFilter,
            title = { Text(text = stringResource(R.string.filter_dialog_title)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(filterRowSpace)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .then(row1FocusRestorerModifiers.parentModifier),
                        horizontalArrangement = Arrangement.spacedBy(filterRowSpace),
                        contentPadding = PaddingValues(horizontal = filterRowSpace)
                    ) {
                        items(items = FollowingSeasonType.entries) { type ->
                            FilterDialogFilterChip(
                                modifier = Modifier
                                    .ifElse(
                                        type == selectedType,
                                        row1FocusRestorerModifiers.childModifier
                                    ),
                                selected = type == selectedType,
                                onClick = { onSelectedTypeChange(type) },
                                label = { Text(text = type.getDisplayName(context)) },
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier
                            .then(row2FocusRestorerModifiers.parentModifier),
                        horizontalArrangement = Arrangement.spacedBy(filterRowSpace),
                        contentPadding = PaddingValues(horizontal = filterRowSpace)
                    ) {
                        items(items = FollowingSeasonStatus.entries) { status ->
                            FilterDialogFilterChip(
                                modifier = Modifier
                                    .ifElse(
                                        status == selectedStatus,
                                        row2FocusRestorerModifiers.childModifier
                                    ),
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FilterDialogFilterChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        content = label,
        leadingIcon = {
            Row {
                AnimatedVisibility(visible = selected) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Rounded.Done,
                        contentDescription = null
                    )
                }
            }
        }
    )
}