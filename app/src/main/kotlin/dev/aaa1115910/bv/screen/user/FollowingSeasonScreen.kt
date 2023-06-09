package dev.aaa1115910.bv.screen.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.getDisplayName
import dev.aaa1115910.bv.util.resizedImageUrl
import dev.aaa1115910.bv.viewmodel.user.FollowingSeasonViewModel
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@Composable
fun FollowingSeasonScreen(
    modifier: Modifier = Modifier,
    followingSeasonViewModel: FollowingSeasonViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val logger = KotlinLogging.logger { }

    var currentIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 6 } }
    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)
    val subtitleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 36f else 24f)

    var showFilter by remember { mutableStateOf(false) }

    val followingSeasons = followingSeasonViewModel.followingSeasons
    var followingSeasonType by remember { mutableStateOf(followingSeasonViewModel.followingSeasonType) }
    var followingSeasonStatus by remember { mutableStateOf(followingSeasonViewModel.followingSeasonStatus) }
    val noMore = followingSeasonViewModel.noMore

    val updateType: (FollowingSeasonType) -> Unit = {
        followingSeasonType = it
        followingSeasonViewModel.followingSeasonType = it
    }

    val updateStatus: (FollowingSeasonStatus) -> Unit = {
        followingSeasonStatus = it
        followingSeasonViewModel.followingSeasonStatus = it
    }

    LaunchedEffect(followingSeasonType, followingSeasonStatus) {
        logger.fInfo { "Start update search result because filter updated" }
        followingSeasonViewModel.clearData()
        followingSeasonViewModel.loadMore()
    }

    Scaffold(
        modifier = modifier
            .onPreviewKeyEvent {
                when (it.key) {
                    Key.DirectionCenter, Key.Enter -> {
                        // 让 Surface 监听到 KeyUp 事件，否则 Surface 将会是一直按下的状态
                        if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent false

                        if (it.nativeKeyEvent.isLongPress) {
                            showFilter = true
                            return@onPreviewKeyEvent true
                        }
                        if (showFilter) return@onPreviewKeyEvent true
                    }
                }
                false
            },
        topBar = {
            Box(
                modifier = Modifier.padding(
                    start = 48.dp,
                    top = 24.dp,
                    bottom = 8.dp,
                    end = 48.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(R.string.title_activity_following_season),
                            fontSize = titleFontSize.sp
                        )
                        Text(
                            text = followingSeasonType.getDisplayName(context),
                            fontSize = subtitleFontSize.sp
                        )
                        Text(
                            text = "(${followingSeasonStatus.getDisplayName(context)})",
                            fontSize = subtitleFontSize.sp
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = stringResource(R.string.filter_dialog_open_tip),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        if (noMore) {
                            Text(
                                text = stringResource(
                                    R.string.load_data_count_no_more,
                                    followingSeasonViewModel.followingSeasons.size
                                ),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = stringResource(
                                    R.string.load_data_count,
                                    followingSeasonViewModel.followingSeasons.size
                                ),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                }
            }
        }
    ) { innerPadding ->
        TvLazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            columns = TvGridCells.Fixed(6),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(items = followingSeasons) { index, followingSeason ->
                SeasonCard(
                    data = SeasonCardData(
                        seasonId = followingSeason.seasonId,
                        title = followingSeason.title,
                        cover = followingSeason.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                        rating = null
                    ),
                    onFocus = {
                        currentIndex = index
                        if (index + 30 > followingSeasons.size) {
                            followingSeasonViewModel.loadMore()
                        }
                    },
                    onClick = {
                        if (!showFilter) {
                            SeasonInfoActivity.actionStart(
                                context = context,
                                seasonId = followingSeason.seasonId
                            )
                        }
                    }
                )
            }
            if (followingSeasons.isEmpty() && noMore) {
                item(
                    span = { TvGridItemSpan(6) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(text = stringResource(R.string.no_data))
                            TextButton(onClick = { showFilter = true }) {
                                Text(text = stringResource(R.string.filter_dialog_open_tip_click))
                            }
                        }
                    }
                }
            }
        }
    }

    FollowingSeasonFilter(
        show = showFilter,
        onHideFilter = { showFilter = false },
        selectedType = followingSeasonType,
        selectedStatus = followingSeasonStatus,
        onSelectedTypeChange = updateType,
        onSelectedStatusChange = updateStatus
    )
}