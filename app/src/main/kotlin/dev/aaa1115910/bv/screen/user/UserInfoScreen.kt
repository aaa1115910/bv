package dev.aaa1115910.bv.screen.user

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.bv.activities.user.FavoriteActivity
import dev.aaa1115910.bv.activities.user.HistoryActivity
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.videocard.VideosRow
import dev.aaa1115910.bv.entity.VideoCardData
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    val histories = remember { mutableStateListOf<VideoCardData>() }
    val animes = remember { mutableStateListOf<VideoCardData>() }
    val favorites = remember { mutableStateListOf<VideoCardData>() }

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
                            time = historyItem.duration.toLong() * 1000
                        )
                    )
                }
            }.onFailure {
                logger.fException(it) { "Load recent videos failed" }
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
                logger.fException(it) { "Load favorite items failed" }
            }
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
        TvLazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                UserInfo(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    face = userViewModel.face,
                    username = userViewModel.username,
                    uid = userViewModel.responseData?.mid ?: 0,
                    level = userViewModel.responseData?.level ?: 0,
                    currentExp = userViewModel.responseData?.levelExp?.currentExp ?: 0,
                    nextLevelExp = userViewModel.responseData?.levelExp?.currentMin ?: 1,
                    showLabel = userViewModel.responseData?.vip?.avatarSubscript == 1,
                    labelUrl = userViewModel.responseData?.vip?.label?.imgLabelUriHansStatic ?: "",
                    onFocusChange = { hasFocus ->
                        //当焦点在此项时，显示大标题
                        showLargeTitle = hasFocus
                    },
                    onClick = { showLogoutConfirmDialog = true }
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
                AnimeVideosRow(
                    videos = animes,
                    showMore = {
                        "还没写呢！！！".toast(context)
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
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) focusRequester.requestFocus()
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
    val density = LocalDensity.current
    var hasFocus by remember { mutableStateOf(false) }
    val levelSlider by animateFloatAsState(
        targetValue = currentExp.toFloat() / nextLevelExp,
        animationSpec = tween(
            durationMillis = 1500,
            easing = LinearEasing
        )
    )

    Surface(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.isFocused
                onFocusChange(it.hasFocus)
            }
            .padding(horizontal = 50.dp, vertical = 28.dp)
            .size(480.dp, 140.dp)
            .border(
                width = 2.dp,
                color = if (hasFocus) Color.White else Color.Transparent,
                shape = MaterialTheme.shapes.large
            )
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
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

                Text(
                    modifier = Modifier.padding(start = startPaddingValue),
                    text = "UID: $uid"
                )
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
private fun AnimeVideosRow(
    modifier: Modifier = Modifier,
    videos: List<VideoCardData>,
    showMore: () -> Unit
) {
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = stringResource(R.string.user_homepage_anime),
        hideShowMore = false,
        showMore = showMore,
        videos = videos
    )
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