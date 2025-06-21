package dev.aaa1115910.bv.tv.screens.user

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.biliapi.http.entity.AuthFailureException
import dev.aaa1115910.biliapi.repositories.FavoriteRepository
import dev.aaa1115910.biliapi.repositories.HistoryRepository
import dev.aaa1115910.biliapi.repositories.SeasonRepository
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.tv.component.videocard.SeasonCard
import dev.aaa1115910.bv.tv.component.videocard.VideosRow
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.tv.activities.user.FavoriteActivity
import dev.aaa1115910.bv.tv.activities.user.FollowActivity
import dev.aaa1115910.bv.tv.activities.user.FollowingSeasonActivity
import dev.aaa1115910.bv.tv.activities.user.HistoryActivity
import dev.aaa1115910.bv.tv.activities.user.UserSwitchActivity
import dev.aaa1115910.bv.tv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.formatHourMinSec
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.UserViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@Composable
fun UserInfoScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = koinViewModel(),
    userRepository: UserRepository = getKoin().get(),
    favoriteRepository: FavoriteRepository = getKoin().get(),
    seasonRepository: SeasonRepository = getKoin().get(),
    historyRepository: HistoryRepository = getKoin().get(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val logger = KotlinLogging.logger { }
    val focusRequester = remember { FocusRequester() }
    var showLargeTitle by remember { mutableStateOf(true) }

    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )
    val randomTitleList = context.resources.getStringArray(R.array.user_homepage_random_title)
    val title by remember { mutableStateOf(randomTitleList.random()) }

    var followingUpCount by remember { mutableIntStateOf(0) }

    val histories = remember { mutableStateListOf<VideoCardData>() }
    val animes = remember { mutableStateListOf<SeasonCardData>() }
    val favorites = remember { mutableStateListOf<VideoCardData>() }

    val updateHistories = {
        scope.launch(Dispatchers.IO) {
            runCatching {
                val data = historyRepository.getHistories(
                    cursor = 0,
                    preferApiType = Prefs.apiType
                )
                histories.clear()
                data.data.forEach { historyItem ->
                    histories.add(
                        VideoCardData(
                            avid = historyItem.oid,
                            title = historyItem.title,
                            cover = historyItem.cover,
                            upName = historyItem.author,
                            timeString = if (historyItem.progress == -1) context.getString(R.string.play_time_finish)
                            else context.getString(
                                R.string.play_time_history,
                                (historyItem.progress * 1000L).formatHourMinSec(),
                                (historyItem.duration * 1000L).formatHourMinSec()
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
    }

    val updateFollowedAnimes = {
        scope.launch(Dispatchers.IO) {
            runCatching {
                val followingSeasonData = seasonRepository.getFollowingSeasons(
                    type = FollowingSeasonType.Bangumi,
                    status = FollowingSeasonStatus.All,
                    pageNumber = 1,
                    pageSize = 15,
                    preferApiType = Prefs.apiType
                )
                animes.clear()
                followingSeasonData.list.forEach { followedSeason ->
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
    }

    val updateFavoriteVideos = {
        scope.launch(Dispatchers.IO) {
            var defaultFolderId: Long = 0
            runCatching {
                val favoriteFolderMetadataList =
                    favoriteRepository.getAllFavoriteFolderMetadataList(
                        mid = Prefs.uid,
                        preferApiType = Prefs.apiType
                    )
                if (favoriteFolderMetadataList.isEmpty()) {
                    "未找到收藏夹".toast(context)
                    return@launch
                }
                defaultFolderId =
                    favoriteFolderMetadataList.find { it.title == "默认收藏夹" }?.id ?: 0
                logger.fInfo { "Get favorite folders: ${favoriteFolderMetadataList.map { it.id }}" }
            }.onFailure {
                logger.fException(it) { "Load favorite folders failed" }
            }
            runCatching {
                val favoriteItems = favoriteRepository.getFavoriteFolderData(
                    mediaId = defaultFolderId,
                    preferApiType = Prefs.apiType
                ).medias
                favorites.clear()
                favoriteItems.forEach { favoriteItem ->
                    favorites.add(
                        VideoCardData(
                            avid = favoriteItem.id,
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
    }

    val updateFollowingUpCount = {
        scope.launch(Dispatchers.IO) {
            logger.fInfo { "Update following up count with user ${Prefs.uid}" }
            followingUpCount = userRepository.getFollowingUpCount(
                mid = Prefs.uid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Following up count: $followingUpCount" }
        }
    }

    val updateData = {
        userViewModel.updateUserInfo(forceUpdate = true)
        updateHistories()
        updateFollowedAnimes()
        updateFavoriteVideos()
        updateFollowingUpCount()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        updateData()
    }

    DisposableEffect(lifecycleOwner) {
        var leaveFromThisPage = false
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                leaveFromThisPage = true
            } else if (event == Lifecycle.Event.ON_RESUME) {
                if (leaveFromThisPage) updateData()
                leaveFromThisPage = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                UserRow(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            showLargeTitle = it.hasFocus
                        },
                    username = userViewModel.username,
                    face = userViewModel.face,
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
                    labelUrl = userViewModel.responseData?.vip?.label?.imgLabelUriHansStatic ?: "",
                    followingUpCount = followingUpCount,
                    onOpenFollowingUser = {
                        context.startActivity(Intent(context, FollowActivity::class.java))
                    },
                    onOpenUserSwitch = {
                        context.startActivity(Intent(context, UserSwitchActivity::class.java))
                    }
                )
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
}

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
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
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
            .size(480.dp, 140.dp)
            .onFocusChanged { hasFocus = it.hasFocus },
        colors = ClickableSurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
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

                val sliderColor = if (hasFocus) {
                    SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.inverseOnSurface,
                        disabledInactiveTrackColor = MaterialTheme.colorScheme.inverseOnSurface.copy(
                            alpha = 0.3f
                        )
                    )
                } else {
                    SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                    )
                }

                Slider(
                    enabled = false,
                    value = levelSlider,
                    onValueChange = {},
                    colors = sliderColor
                )
            }
        }
    }
}

@Composable
private fun IncognitoModeCard(
    modifier: Modifier = Modifier
) {
    var enabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        enabled = Prefs.incognitoMode
    }

    IncognitoModeCardContent(
        modifier = modifier,
        enabled = enabled,
        onClick = {
            enabled = !enabled
            Prefs.incognitoMode = enabled
        }
    )
}

@Composable
private fun IncognitoModeCardContent(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(140.dp),
        colors = ClickableSurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
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
                text = stringResource(R.string.user_info_Incognito_mode_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (enabled) "\uD83D\uDC7B" + stringResource(R.string.user_info_Incognito_mode_on)
                else stringResource(R.string.user_info_Incognito_mode_off),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun FollowedUserCard(
    modifier: Modifier = Modifier,
    size: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(140.dp),
        colors = ClickableSurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
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
private fun UserSwitchCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(140.dp),
        colors = ClickableSurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
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
                text = stringResource(R.string.user_homepage_user_switch),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun UserRow(
    modifier: Modifier = Modifier,
    username: String,
    face: String,
    uid: Long,
    level: Int,
    currentExp: Int,
    nextLevelExp: Int,
    showLabel: Boolean,
    labelUrl: String,
    followingUpCount: Int,
    onOpenFollowingUser: () -> Unit,
    onOpenUserSwitch: () -> Unit
) {
    val animateFollowingNumber by animateIntAsState(
        targetValue = followingUpCount,
        label = "animate following number"
    )

    LazyRow(
        modifier = modifier.padding(vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        contentPadding = PaddingValues(horizontal = 50.dp)
    ) {
        item {
            UserInfo(
                modifier = Modifier,
                face = face,
                username = username,
                uid = uid,
                level = level,
                currentExp = currentExp,
                nextLevelExp = nextLevelExp,
                showLabel = showLabel,
                labelUrl = labelUrl,
                onClick = { }
            )
        }
        item {
            IncognitoModeCard()
        }
        item {
            FollowedUserCard(
                size = animateFollowingNumber,
                onClick = onOpenFollowingUser
            )
        }
        item {
            UserSwitchCard(
                onClick = onOpenUserSwitch
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
    val context = LocalContext.current
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = stringResource(R.string.user_homepage_recent),
        hideShowMore = false,
        showMore = showMore,
        videos = videos,
        onOpenSeasonInfo = { videoData ->
            SeasonInfoActivity.actionStart(
                context = context,
                epId = videoData.epId!!,
                proxyArea = ProxyArea.checkProxyArea(videoData.title)
            )
        },
        onOpenVideoInfo = { videoData ->
            VideoInfoActivity.actionStart(context, videoData.avid)
        }
    )
}

@Composable
private fun FollowingAnimeVideosRow(
    modifier: Modifier = Modifier,
    videos: List<SeasonCardData>,
    showMore: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(
        targetValue = if (hasFocus) 30f else 14f,
        label = "title font size"
    )
    var rowHeight by remember { mutableStateOf(0.dp) }

    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .onFocusChanged { hasFocus = it.hasFocus }
    ) {
        Text(
            modifier = Modifier.padding(start = 50.dp),
            text = stringResource(R.string.user_homepage_anime),
            fontSize = titleFontSize.sp,
            color = titleColor
        )
        LazyRow(
            modifier = Modifier
                .padding(top = 15.dp)
                .onGloballyPositioned {
                    rowHeight = with(density) {
                        it.size.height.toDp()
                    }
                },
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 62.dp)
        ) {
            items(items = videos) { seasonCardData ->
                SeasonCard(
                    modifier = Modifier.width(150.dp),
                    data = seasonCardData,
                    onClick = {
                        SeasonInfoActivity.actionStart(
                            context = context,
                            seasonId = seasonCardData.seasonId,
                            proxyArea = ProxyArea.checkProxyArea(seasonCardData.title)
                        )
                    }
                )
            }
            item {
                Button(
                    modifier = Modifier.height(rowHeight),
                    shape = ButtonDefaults.shape(shape = MaterialTheme.shapes.large),
                    onClick = showMore
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "显示更多")
                    }
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
    val context = LocalContext.current
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = stringResource(R.string.user_homepage_favorite),
        hideShowMore = false,
        showMore = showMore,
        videos = videos,
        onOpenSeasonInfo = { videoData ->
            SeasonInfoActivity.actionStart(
                context = context,
                epId = videoData.epId!!,
                proxyArea = ProxyArea.checkProxyArea(videoData.title)
            )
        },
        onOpenVideoInfo = { videoData ->
            VideoInfoActivity.actionStart(context, videoData.avid)
        }
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
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun UserInfoFocusedPreview() {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BVTheme {
        UserInfo(
            modifier = Modifier.focusRequester(focusRequester),
            face = "",
            username = "Username",
            uid = 12345,
            level = 6,
            currentExp = 1234,
            nextLevelExp = 2345,
            showLabel = false,
            labelUrl = "",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun IncognitoModeCardOnPreview() {
    BVTheme {
        IncognitoModeCardContent(
            enabled = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun IncognitoModeCardOffPreview() {
    BVTheme {
        IncognitoModeCardContent(
            enabled = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun FollowedUserCardPreview() {
    BVTheme {
        FollowedUserCard(
            size = 466,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun UserSwitchCardPreview() {
    BVTheme {
        UserSwitchCard(
            onClick = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun UserRowPreview() {
    BVTheme {
        UserRow(
            username = "Username",
            face = "",
            uid = 1234567890,
            level = 4,
            currentExp = 123,
            nextLevelExp = 431,
            showLabel = false,
            labelUrl = "",
            followingUpCount = 466,
            onOpenFollowingUser = { },
            onOpenUserSwitch = {}
        )
    }
}