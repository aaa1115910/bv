package dev.aaa1115910.bv.component.pgc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.entity.pgc.index.Area
import dev.aaa1115910.biliapi.entity.pgc.index.Copyright
import dev.aaa1115910.biliapi.entity.pgc.index.IndexOrder
import dev.aaa1115910.biliapi.entity.pgc.index.IndexOrderType
import dev.aaa1115910.biliapi.entity.pgc.index.IsFinish
import dev.aaa1115910.biliapi.entity.pgc.index.PgcIndexParam
import dev.aaa1115910.biliapi.entity.pgc.index.Producer
import dev.aaa1115910.biliapi.entity.pgc.index.ReleaseDate
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonMonth
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonStatus
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonVersion
import dev.aaa1115910.biliapi.entity.pgc.index.SpokenLanguage
import dev.aaa1115910.biliapi.entity.pgc.index.Style
import dev.aaa1115910.biliapi.entity.pgc.index.Year
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.util.getDisplayName

@Composable
fun IndexFilter(
    modifier: Modifier = Modifier,
    type: PgcType,
    show: Boolean,
    onDismissRequest: () -> Unit,
    order: IndexOrder,
    orderType: IndexOrderType,
    seasonVersion: SeasonVersion,
    spokenLanguage: SpokenLanguage,
    area: Area,
    isFinish: IsFinish,
    copyright: Copyright,
    seasonStatus: SeasonStatus,
    seasonMonth: SeasonMonth,
    producer: Producer,
    year: Year,
    releaseDate: ReleaseDate,
    style: Style,
    onOrderChange: (IndexOrder) -> Unit,
    onOrderTypeChange: (IndexOrderType) -> Unit,
    onSeasonVersionChange: (SeasonVersion) -> Unit,
    onSpokenLanguageChange: (SpokenLanguage) -> Unit,
    onAreaChange: (Area) -> Unit,
    onIsFinishChange: (IsFinish) -> Unit,
    onCopyrightChange: (Copyright) -> Unit,
    onSeasonStatusChange: (SeasonStatus) -> Unit,
    onSeasonMonthChange: (SeasonMonth) -> Unit,
    onProducerChange: (Producer) -> Unit,
    onYearChange: (Year) -> Unit,
    onReleaseDateChange: (ReleaseDate) -> Unit,
    onStyleChange: (Style) -> Unit
) {
    val context = LocalContext.current

    IndexFilterContent(
        modifier = modifier,
        title = stringResource(R.string.pgc_index_filter_title_prefix) + type.getDisplayName(context),
        show = show,
        onDismissRequest = onDismissRequest,
        content = {
            IndexFilterChipRow(
                title = stringResource(R.string.pgc_index_filter_order),
                filters = IndexOrder.getList(type),
                selectedFilter = order,
                onFilterChange = onOrderChange
            )
            IndexFilterChipRow(
                title = stringResource(R.string.pgc_index_filter_order_type),
                filters = IndexOrderType.entries,
                selectedFilter = orderType,
                onFilterChange = onOrderTypeChange
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn {
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_season_version),
                    filters = SeasonVersion.getList(type),
                    selectedFilter = seasonVersion,
                    onFilterChange = onSeasonVersionChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_spoken_language),
                    filters = SpokenLanguage.getList(type),
                    selectedFilter = spokenLanguage,
                    onFilterChange = onSpokenLanguageChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_is_finish),
                    filters = IsFinish.getList(type),
                    selectedFilter = isFinish,
                    onFilterChange = onIsFinishChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_season_status),
                    filters = SeasonStatus.getList(type),
                    selectedFilter = seasonStatus,
                    onFilterChange = onSeasonStatusChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_area),
                    filters = Area.getList(type),
                    selectedFilter = area,
                    onFilterChange = onAreaChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_copyright),
                    filters = Copyright.getList(type),
                    selectedFilter = copyright,
                    onFilterChange = onCopyrightChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_season_month),
                    filters = SeasonMonth.getList(type),
                    selectedFilter = seasonMonth,
                    onFilterChange = onSeasonMonthChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_producer),
                    filters = Producer.getList(type),
                    selectedFilter = producer,
                    onFilterChange = onProducerChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_year),
                    filters = Year.getList(type),
                    selectedFilter = year,
                    onFilterChange = onYearChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_release_date),
                    filters = ReleaseDate.getList(type),
                    selectedFilter = releaseDate,
                    onFilterChange = onReleaseDateChange
                )
                indexFilterChipRow(
                    title = context.getString(R.string.pgc_index_filter_style),
                    filters = Style.getList(type),
                    selectedFilter = style,
                    onFilterChange = onStyleChange
                )
            }
        }
    )
}

@Composable
private fun IndexFilterContent(
    modifier: Modifier = Modifier,
    title: String,
    show: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    if (show) {
        AlertDialog(
            modifier = modifier
                .fillMaxWidth(0.8f),
            onDismissRequest = onDismissRequest,
            confirmButton = { },
            title = {
                Text(text = title)
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                ) {
                    content()
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
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

private fun <T> LazyListScope.indexFilterChipRow(
    modifier: Modifier = Modifier,
    title: String,
    filters: List<T>,
    selectedFilter: T,
    onFilterChange: (T) -> Unit
) {
    if (filters.isEmpty()) return
    item {
        IndexFilterChipRow(
            modifier = modifier,
            title = title,
            filters = filters,
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange
        )
    }
}

@Composable
private fun <T> IndexFilterChipRow(
    modifier: Modifier = Modifier,
    title: String,
    filters: List<T>,
    selectedFilter: T,
    onFilterChange: (T) -> Unit
) {
    val context = LocalContext.current
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
            items(items = filters) { filter ->
                IndexFilterChip(
                    modifier = Modifier
                        .ifElse(selectedFilter == filter, focusRestorerModifiers.childModifier),
                    selected = selectedFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = (filter as PgcIndexParam).getDisplayName(context)
                )
            }
        }
    }
}

private class PgcTypeProvider : PreviewParameterProvider<PgcType> {
    override val values = PgcType.entries.asSequence()
}

@Preview(device = "id:tv_1080p")
@Composable
private fun IndexFilterPreview(
    @PreviewParameter(PgcTypeProvider::class) pgcType: PgcType
) {
    var order by remember { mutableStateOf(IndexOrder.PlayCount) }
    var orderType by remember { mutableStateOf(IndexOrderType.Desc) }
    var seasonVersion by remember { mutableStateOf(SeasonVersion.All) }
    var spokenLanguage by remember { mutableStateOf(SpokenLanguage.All) }
    var area by remember { mutableStateOf(Area.All) }
    var isFinish by remember { mutableStateOf(IsFinish.All) }
    var copyright by remember { mutableStateOf(Copyright.All) }
    var seasonStatus by remember { mutableStateOf(SeasonStatus.All) }
    var seasonMonth by remember { mutableStateOf(SeasonMonth.All) }
    var producer by remember { mutableStateOf(Producer.All) }
    var year by remember { mutableStateOf(Year.All) }
    var releaseDate by remember { mutableStateOf(ReleaseDate.All) }
    var style by remember { mutableStateOf(Style.All) }

    BVTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            IndexFilter(
                type = pgcType,
                show = true,
                onDismissRequest = { },
                order = order,
                orderType = orderType,
                seasonVersion = seasonVersion,
                spokenLanguage = spokenLanguage,
                area = area,
                isFinish = isFinish,
                copyright = copyright,
                seasonStatus = seasonStatus,
                seasonMonth = seasonMonth,
                producer = producer,
                year = year,
                releaseDate = releaseDate,
                style = style,
                onOrderChange = { order = it },
                onOrderTypeChange = { orderType = it },
                onSeasonVersionChange = { seasonVersion = it },
                onSpokenLanguageChange = { spokenLanguage = it },
                onAreaChange = { area = it },
                onIsFinishChange = { isFinish = it },
                onCopyrightChange = { copyright = it },
                onSeasonStatusChange = { seasonStatus = it },
                onSeasonMonthChange = { seasonMonth = it },
                onProducerChange = { producer = it },
                onYearChange = { year = it },
                onReleaseDateChange = { releaseDate = it },
                onStyleChange = { style = it }
            )
        }
    }
}