package dev.aaa1115910.bv.screen.user

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.AuthFailureException
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.biliapi.entity.user.RelationStat
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.user.FavoriteActivity
import dev.aaa1115910.bv.activities.user.FollowActivity
import dev.aaa1115910.bv.activities.user.FollowingSeasonActivity
import dev.aaa1115910.bv.activities.user.HistoryActivity
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.component.videocard.VideosRow
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.formatMinSec
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserInfoScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }
    val focusRequester = remember { FocusRequester() }
    var showLargeTitle by remember { mutableStateOf(true) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)
    val randomTitleList = context.resources.getStringArray(R.array.user_homepage_random_title)
    val title by remember { mutableStateOf(randomTitleList.random()) }

    var relationStat: RelationStat? by remember { mutableStateOf(null) }
    val followingNumber by animateIntAsState(targetValue = relationStat?.following ?: 0)

    val histories = remember { mutableStateListOf<VideoCardData>() }
    val animes = remember { mutableStateListOf<SeasonCardData>() }
    val favorites = remember { mutableStateListOf<VideoCardData>() }

    var focusOnUserInfo by remember { mutableStateOf(false) }
    var focusOnIncognitoModeCard by remember { mutableStateOf(false) }
    var focusOnFollowedUserCard by remember { mutableStateOf(false) }

    val updateRelationStat: () -> Unit = {
        scope.launch(Dispatchers.Default) {
            runCatching {
                logger.fInfo { "Get relation stat with user ${Prefs.uid}" }
                relationStat = BiliApi.getRelationStat(mid = Prefs.uid).getResponseData()
            }.onFailure {
                logger.fInfo { "Get relation stat failed: ${it.stackTraceToString()}" }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        userViewModel.updateUserInfo()

        //update histories
        scope.launch(Dispatchers.Default) {
            runCatching {
                val responseData = BiliApi.getHistories(sessData = Prefs.sessData).getResponseData()
                responseData.list.forEach { historyItem ->
                    val supportedBusinessList = listOf("archive", "pgc")
                    if (!supportedBusinessList.contains(historyItem.history.business)) return@forEach
                    histories.add(
                        VideoCardData(
                            avid = historyItem.history.oid,
                            title = historyItem.title,
                            cover = historyItem.cover,
                            upName = historyItem.authorName,
                            timeString = if (historyItem.progress == -1) context.getString(R.string.play_time_finish)
                            else context.getString(
                                R.string.play_time_history,
                                (historyItem.progress * 1000L).formatMinSec(),
                                (historyItem.duration * 1000L).formatMinSec()
                            )
                        )
                    )
                }
            }.onFailure {
                logger.fWarn { "Load recent videos failed: ${it.stackTraceToString()}" }
                when (it) {
                    is AuthFailureException -> {
                        withContext(Dispatchers.Main) {
                            context.getString(R.string.exception_auth_failure).toast(context)
                        }
                        logger.fInfo { "User auth failure" }
                        if (!BuildConfig.DEBUG) userViewModel.logout()
                    }

                    else -> {}
                }
            }
        }

        //update followed animes
        scope.launch(Dispatchers.Default) {
            runCatching {
                val followedSeasons = BiliApi.getFollowingSeasons(
                    type = FollowingSeasonType.Bangumi,
                    status = FollowingSeasonStatus.All,
                    pageNumber = 1,
                    pageSize = 15,
                    mid = Prefs.uid,
                    sessData = Prefs.sessData
                ).getResponseData()
                followedSeasons.list.forEach { followedSeason ->
                    animes.add(
                        SeasonCardData(
                            seasonId = followedSeason.seasonId,
                            title = followedSeason.title,
                            cover = followedSeason.cover,
                            rating = null
                        )
                    )
                }
            }.onFailure {
                logger.fWarn { "Load followed animes failed: ${it.stackTraceToString()}" }
                when (it) {
                    is AuthFailureException -> {
                        withContext(Dispatchers.Main) {
                            context.getString(R.string.exception_auth_failure).toast(context)
                        }
                        logger.fInfo { "User auth failure" }
                        if (!BuildConfig.DEBUG) userViewModel.logout()
                    }

                    else -> {}
                }
            }
        }

        //update favorite videos
        scope.launch(Dispatchers.Default) {
            var folderId: Long = 0
            runCatching {
                val foldersInfo = BiliApi.getAllFavoriteFoldersInfo(
                    mid = Prefs.uid,
                    type = 2,
                    sessData = Prefs.sessData
                ).getResponseData()
                if (foldersInfo.count == 0) {
                    "未找到收藏夹".toast(context)
                    return@launch
                }
                logger.fInfo { "Get favorite folders: ${foldersInfo.list.map { it.id }}" }
                folderId = foldersInfo.list.first().id
            }.onFailure {
                logger.fException(it) { "Load favorite folders failed" }
            }
            runCatching {
                val favoriteItems = BiliApi.getFavoriteList(
                    mediaId = folderId,
                    sessData = Prefs.sessData
                ).getResponseData().medias
                favoriteItems.forEach { favoriteItem ->
                    favorites.add(
                        VideoCardData(
                            avid = favoriteItem.id.toInt(),
                            title = favoriteItem.title,
                            cover = favoriteItem.cover,
                            upName = favoriteItem.upper.name,
                            time = favoriteItem.duration.toLong() * 1000
                        )
                    )
                }
            }.onFailure {
                logger.fWarn { "Load favorite items failed: ${it.stackTraceToString()}" }
                when (it) {
                    is AuthFailureException -> {
                        withContext(Dispatchers.Main) {
                            context.getString(R.string.exception_auth_failure).toast(context)
                        }
                        logger.fInfo { "User auth failure" }
                        if (!BuildConfig.DEBUG) userViewModel.logout()
                    }

                    else -> {}
                }
            }
        }

        updateRelationStat()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = titleFontSize.sp
                )
            }

        }
    ) { innerPadding ->
        TvLazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 50.dp, vertical = 28.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
                ) {
                    UserInfo(
                        modifier = Modifier
                            .focusRequester(focusRequester),
                        face = userViewModel.face,
                        username = userViewModel.username,
                        uid = userViewModel.responseData?.mid ?: 0,
                        level = userViewModel.responseData?.level ?: 0,
                        currentExp = userViewModel.responseData?.levelExp?.currentExp ?: 0,
                        nextLevelExp = with(userViewModel.responseData?.levelExp?.nextExp) {
                            if (this == null) {
                                1
                            } else if (this <= 0) {
                                userViewModel.responseData?.levelExp?.currentExp ?: 1
                            } else {
                                (userViewModel.responseData?.levelExp?.currentExp ?: 1)
                                +(userViewModel.responseData?.levelExp?.nextExp ?: 0)
                            }
                        },
                        showLabel = userViewModel.responseData?.vip?.avatarSubscript == 1,
                        labelUrl = userViewModel.responseData?.vip?.label?.imgLabelUriHansStatic
                            ?: "",
                        onFocusChange = { hasFocus ->
                            focusOnUserInfo = hasFocus
                            showLargeTitle =
                                focusOnUserInfo || focusOnIncognitoModeCard || focusOnFollowedUserCard
                        },
                        onClick = { showLogoutConfirmDialog = true }
                    )
                    IncognitoModeCard(
                        onFocusChange = { hasFocus ->
                            focusOnIncognitoModeCard = hasFocus
                            showLargeTitle =
                                focusOnUserInfo || focusOnIncognitoModeCard || focusOnFollowedUserCard
                        },
                        onClick = {
                            Prefs.incognitoMode = !Prefs.incognitoMode
                        }
                    )
                    FollowedUserCard(
                        onFocusChange = { hasFocus ->
                            focusOnFollowedUserCard = hasFocus
                            showLargeTitle =
                                focusOnUserInfo || focusOnIncognitoModeCard || focusOnFollowedUserCard
                        },
                        size = followingNumber,
                        onClick = {
                            context.startActivity(Intent(context, FollowActivity::class.java))
                        }
                    )
                }
            }
            item {
                RecentVideosRow(
                    videos = histories,
                    showMore = {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                    }
                )
            }
            item {
                FollowingAnimeVideosRow(
                    videos = animes,
                    showMore = {
                        context.startActivity(Intent(context, FollowingSeasonActivity::class.java))
                    }
                )
            }
            item {
                FavoriteVideosRow(
                    videos = favorites,
                    showMore = {
                        context.startActivity(Intent(context, FavoriteActivity::class.java))
                    }
                )
            }
        }
    }

    LogoutConfirmDialog(
        show = showLogoutConfirmDialog,
        onHideDialog = { showLogoutConfirmDialog = false },
        onConfirm = {
            userViewModel.logout()
            (context as Activity).finish()
        }
    )
}

@Composable
private fun LogoutConfirmDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    onConfirm: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) focusRequester.requestFocus(scope)
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { Text(text = stringResource(R.string.logout_dialog_title)) },
            text = { Text(text = stringResource(R.string.logout_dialog_text, Prefs.uid)) },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = stringResource(R.string.logout_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    onClick = { onHideDialog() }
                ) {
                    Text(text = stringResource(R.string.logout_dialog_dismiss))
                }
            }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun UserInfo(
    modifier: Modifier = Modifier,
    face: String,
    username: String,
    uid: Long,
    level: Int,
    currentExp: Int,
    nextLevelExp: Int,
    showLabel: Boolean,
    labelUrl: String,
    onFocusChange: (hasFocus: Boolean) -> Unit,
    onClick: () -> Unit
) {
    val levelSlider by animateFloatAsState(
        targetValue = currentExp.toFloat() / nextLevelExp,
        animationSpec = tween(
            durationMillis = 1500,
            easing = LinearEasing
        ),
        label = "Loading level exp slider"
    )

    Surface(
        modifier = modifier
            .onFocusChanged { onFocusChange(it.hasFocus) }
            .size(480.dp, 140.dp),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            pressedContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 3.dp, color = Color.White),
                shape = MaterialTheme.shapes.large
            )
        ),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .padding(start = 24.dp, end = 8.dp)
                    .size(80.dp)
                    .clip(CircleShape),
                color = Color.White
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    model = face,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        start = 6.dp,
                        top = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    ),
            ) {
                val startPaddingValue = 6.dp
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Row(
                        modifier = Modifier.padding(end = startPaddingValue),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showLabel)
                            AsyncImage(
                                //大会员 Tag 给定指定大小范围，避免加载时大小会突然变得非常大导致画面闪烁
                                modifier = Modifier
                                    .height(22.dp)
                                    .widthIn(max = 80.dp),
                                model = labelUrl,
                                contentDescription = null,
                                contentScale = ContentScale.FillHeight
                            )
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(start = startPaddingValue),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(R.string.user_info_level, level))
                    Text(text = stringResource(R.string.user_info_uid, uid))
                }

                Slider(
                    enabled = false,
                    value = levelSlider,
                    onValueChange = {},
                    colors = SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IncognitoModeCard(
    modifier: Modifier = Modifier,
    onFocusChange: (hasFocus: Boolean) -> Unit,
    onClick: () -> Unit
) {
    var enabled by remember { mutableStateOf(Prefs.incognitoMode) }
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) Color.Black else MaterialTheme.colorScheme.secondaryContainer,
        label = "Incognito background switch"
    )

    Surface(
        modifier = modifier
            .onFocusChanged { onFocusChange(it.hasFocus) }
            .height(140.dp),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = backgroundColor,
            focusedContainerColor = backgroundColor,
            pressedContainerColor = backgroundColor
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 3.dp, color = Color.White),
                shape = MaterialTheme.shapes.large
            )
        ),
        onClick = {
            enabled = !enabled
            onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.user_info_Incognito_mode_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (enabled) stringResource(R.string.user_info_Incognito_mode_on)
                else stringResource(R.string.user_info_Incognito_mode_off),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FollowedUserCard(
    modifier: Modifier = Modifier,
    size: Int,
    onFocusChange: (hasFocus: Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .onFocusChanged { onFocusChange(it.hasFocus) }
            .height(140.dp),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            pressedContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 3.dp, color = Color.White),
                shape = MaterialTheme.shapes.large
            )
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.user_homepage_follow),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "$size",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun RecentVideosRow(
    modifier: Modifier = Modifier,
    videos: List<VideoCardData>,
    showMore: () -> Unit
) {
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = stringResource(R.string.user_homepage_recent),
        hideShowMore = false,
        showMore = showMore,
        videos = videos
    )
}

@Composable
private fun FollowingAnimeVideosRow(
    modifier: Modifier = Modifier,
    videos: List<SeasonCardData>,
    showMore: () -> Unit
) {
    val context = LocalContext.current
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)

    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .padding(start = 50.dp)
            .onFocusChanged { hasFocus = it.hasFocus }
    ) {
        Text(
            text = stringResource(R.string.user_homepage_anime),
            fontSize = titleFontSize.sp,
            color = titleColor
        )
        TvLazyRow(
            modifier = Modifier.padding(top = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(end = 50.dp, start = 12.dp)
        ) {
            items(items = videos) { seasonCardData ->
                SeasonCard(
                    modifier = Modifier.width(150.dp),
                    data = seasonCardData,
                    onClick = {
                        SeasonInfoActivity.actionStart(
                            context = context,
                            seasonId = seasonCardData.seasonId
                        )
                    }
                )
            }
            item {
                TextButton(onClick = {
                    showMore()
                }) {
                    Text(text = "显示更多")
                }
            }
        }
    }
}

@Composable
private fun FavoriteVideosRow(
    modifier: Modifier = Modifier,
    videos: List<VideoCardData>,
    showMore: () -> Unit
) {
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = stringResource(R.string.user_homepage_favorite),
        hideShowMore = false,
        showMore = showMore,
        videos = videos
    )
}

@Preview
@Composable
private fun UserInfoPreview() {
    BVTheme {
        UserInfo(
            face = "",
            username = "Username",
            uid = 12345,
            level = 6,
            currentExp = 1234,
            nextLevelExp = 2345,
            showLabel = false,
            labelUrl = "",
            onFocusChange = {},
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun IncognitoModeCardPreview() {
    BVTheme {
        IncognitoModeCard(
            onFocusChange = {},
            onClick = {}
        )
    }
}