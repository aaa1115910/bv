package dev.aaa1115910.bv.screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.user.FollowAction
import dev.aaa1115910.biliapi.entity.user.FollowActionSource
import dev.aaa1115910.biliapi.entity.user.RelationData
import dev.aaa1115910.biliapi.entity.user.RelationType
import dev.aaa1115910.biliapi.entity.video.Dimension
import dev.aaa1115910.biliapi.entity.video.Tag
import dev.aaa1115910.biliapi.entity.video.UgcSeason
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import dev.aaa1115910.biliapi.entity.video.VideoPage
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.activities.video.TagActivity
import dev.aaa1115910.bv.activities.video.UpInfoActivity
import dev.aaa1115910.bv.activities.video.VideoPlayerActivity
import dev.aaa1115910.bv.component.SurfaceWithoutClickable
import dev.aaa1115910.bv.component.UpIcon
import dev.aaa1115910.bv.component.buttons.FavoriteButton
import dev.aaa1115910.bv.component.videocard.VideosRow
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.repository.VideoListItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.focusedBorder
import dev.aaa1115910.bv.util.formatPubTimeString
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.androidx.compose.getKoin
import java.util.Date

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoInfoScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    videoInfoRepository: VideoInfoRepository = getKoin().get()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent
    val logger = KotlinLogging.logger { }

    var videoInfo: VideoInfo? by remember { mutableStateOf(null) }
    val relatedVideos = remember { mutableStateListOf<VideoCardData>() }
    var relations: RelationData? by remember { mutableStateOf(null) }
    val tags = remember { mutableStateListOf<Tag>() }

    var lastPlayedCid by remember { mutableStateOf(0) }
    var lastPlayedTime by remember { mutableStateOf(0) }

    var tip by remember { mutableStateOf("Loading") }
    var fromSeason by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    val containsVerticalScreenVideo by remember {
        derivedStateOf {
            videoInfo?.pages?.any { page -> page.dimension.height > page.dimension.width } ?: false
                    || videoInfo?.ugcSeason?.sections?.any { section -> section.episodes.any { episode -> episode.page.dimension.height > episode.page.dimension.width } } ?: false
        }
    }

    val updateV2Data: () -> Unit = {
        scope.launch(Dispatchers.Default) {
            runCatching {
                logger.fInfo { "Get video more info" }
                val moreInfoResponse = BiliApi.getVideoMoreInfo(
                    avid = videoInfo!!.aid, cid = videoInfo!!.cid, sessData = Prefs.sessData
                ).getResponseData()
                lastPlayedCid = moreInfoResponse.lastPlayCid
                lastPlayedTime = moreInfoResponse.lastPlayTime
            }.onFailure {
                logger.fInfo { "Get video more info failed: ${it.stackTraceToString()}" }
            }
        }
    }

    val updateRelationVideos: (avid: Long) -> Unit = { avid ->
        scope.launch(Dispatchers.Default) {
            runCatching {
                val response = BiliApi.getRelatedVideos(avid = avid)
                relatedVideos.swapList(response.data.map {
                    VideoCardData(
                        avid = it.aid,
                        title = it.title,
                        cover = it.pic,
                        upName = it.owner.name,
                        time = it.duration * 1000L,
                        play = it.stat.view,
                        danmaku = it.stat.danmaku
                    )
                })
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "获取相关视频失败：${it.localizedMessage}".toast(context)
                }
                logger.fException(it) { "Get related videos failed" }
            }
        }
    }

    val updateRelationData: () -> Unit = {
        scope.launch(Dispatchers.Default) {
            runCatching {
                logger.fInfo { "Get relation data with user ${videoInfo?.owner?.mid}" }
                relations = BiliApi.getRelations(
                    mid = videoInfo?.owner?.mid ?: -1, sessData = Prefs.sessData
                ).getResponseData()
            }.onFailure {
                logger.fInfo { "Get relation data failed: ${it.stackTraceToString()}" }
            }
        }
    }

    val updateTags: (avid: Int) -> Unit = { avid ->
        scope.launch(Dispatchers.Default) {
            runCatching {
                logger.fInfo { "Getting tags" }
                tags.swapList(BiliApi.getVideoTags(avid = avid).getResponseData())
            }.onFailure {
                logger.fInfo { "Get tags failed: ${it.stackTraceToString()}" }
            }
        }
    }

    val modifyRelation: (action: FollowAction, afterModify: () -> Unit) -> Unit =
        { action, afterModify ->
            scope.launch(Dispatchers.Default) {
                runCatching {
                    logger.fInfo { "Modify relation: $action" }
                    BiliApi.modifyFollow(
                        mid = videoInfo?.owner?.mid ?: -1,
                        action = action,
                        actionSource = FollowActionSource.Video,
                        csrf = Prefs.biliJct,
                        sessData = Prefs.sessData
                    )
                    afterModify()
                }.onFailure {
                    logger.fInfo { "Modify relation failed: ${it.stackTraceToString()}" }
                }
            }
        }

    LaunchedEffect(Unit) {
        if (intent.hasExtra("aid")) {
            val aid = intent.getIntExtra("aid", 170001)
            fromSeason = intent.getBooleanExtra("fromSeason", false)
            //获取视频信息
            scope.launch(Dispatchers.Default) {
                runCatching {
                    val response = BiliApi.getVideoInfo(av = aid, sessData = Prefs.sessData)
                    videoInfo = response.getResponseData()
                    updateV2Data()

                    //如果是从剧集跳转过来的，就直接播放 P1
                    if (fromSeason) {
                        val playPart = videoInfo!!.pages.first()
                        VideoPlayerActivity.actionStart(
                            context = context,
                            avid = videoInfo!!.aid,
                            cid = playPart.cid,
                            title = videoInfo!!.title,
                            partTitle = videoInfo!!.pages.find { it.cid == playPart.cid }!!.part,
                            played = if (playPart.cid == lastPlayedCid) lastPlayedTime else 0,
                            fromSeason = true
                        )
                        context.finish()
                    } else if (videoInfo?.isSeasonDisplay == true) {
                        //如果不是剧集，则设置分p数据，以便播放器读取（合集）
                        val partVideoList =
                            videoInfo!!.ugcSeason!!.sections[0].episodes.mapIndexed { index, episode ->
                                VideoListItem(
                                    aid = episode.aid,
                                    cid = episode.cid,
                                    title = episode.title,
                                    index = index,
                                    isEpisode = false
                                )
                            }
                        videoInfoRepository.videoList.clear()
                        videoInfoRepository.videoList.addAll(partVideoList)
                    } else {
                        //如果不是剧集，则设置分p数据，以便播放器读取（分P）
                        val partVideoList = videoInfo!!.pages.mapIndexed { index, videoPage ->
                            VideoListItem(
                                aid = aid,
                                cid = videoPage.cid,
                                title = videoPage.part,
                                index = index,
                                isEpisode = false
                            )
                        }
                        videoInfoRepository.videoList.clear()
                        videoInfoRepository.videoList.addAll(partVideoList)
                    }
                }.onFailure {
                    tip = it.localizedMessage ?: "未知错误"
                    logger.fInfo { "Get video info failed: ${it.stackTraceToString()}" }
                }
            }
            //如果是从剧集跳转过来的，就不需要获取相关视频等信息，因为页面一直都是 Loading
            if (!fromSeason) {
                updateRelationVideos(aid.toLong())
                updateTags(aid)
            }
        }
    }

    LaunchedEffect(videoInfo) {
        //如果是从剧集页跳转回来的，那就不需要再跳转到剧集页了
        if (fromSeason) return@LaunchedEffect

        videoInfo?.let {
            logger.fInfo { "Redirect url: ${videoInfo?.redirectUrl}" }
            if (it.redirectUrl?.contains("ep") == true) {
                runCatching {
                    //redirectUrl example https://www.bilibili.com/bangumi/play/ep706549?theme=movie
                    val epid = videoInfo!!.redirectUrl!!.split("ep", "?")[1].toInt()
                    logger.fInfo { "Redirect to season activity: ep${epid}" }
                    SeasonInfoActivity.actionStart(context, epid)
                    context.finish()
                }.onFailure {
                    logger.fWarn { "Redirect failed: ${it.stackTraceToString()}" }
                }
            } else {
                logger.fInfo { "No redirection required" }
            }

            if (!fromSeason) updateRelationData()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                paused = true
            } else if (event == Lifecycle.Event.ON_RESUME) {
                // 如果 pause==true 那可能是从播放页返回回来的，此时更新历史记录
                if (paused) updateV2Data()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (videoInfo == null || videoInfo?.redirectUrl?.contains("ep") == true || fromSeason) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = tip
            )
        }
    } else {
        Scaffold(
            containerColor = Color.Black
        ) { innerPadding ->
            Box(
                modifier.padding(innerPadding)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(videoInfo!!.ugcSeason?.cover ?: videoInfo!!.pic)
                            .transformations(BlurTransformation(LocalContext.current, 20f, 5f))
                            .build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.6f
                )
                TvLazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (containsVerticalScreenVideo)
                        item {
                            SurfaceWithoutClickable(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 50.dp),
                                color = Color.Yellow.copy(alpha = 0.2f),
                                contentColor = Color.Yellow,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Warning,
                                        contentDescription = null,
                                        tint = Color.Yellow
                                    )
                                    Text(text = "温馨提示：该稿件包含竖屏视频内容")
                                }
                            }
                        }
                    item {
                        VideoInfoData(
                            videoInfo = videoInfo!!,
                            relations = relations,
                            tags = tags,
                            onClickCover = {
                                logger.fInfo { "Click video cover" }
                                VideoPlayerActivity.actionStart(
                                    context = context,
                                    avid = videoInfo!!.aid,
                                    cid = videoInfo!!.pages.first().cid,
                                    title = videoInfo!!.title,
                                    partTitle = videoInfo!!.pages.first().part,
                                    played = if (videoInfo!!.cid == lastPlayedCid) lastPlayedTime else 0,
                                    fromSeason = false
                                )
                            },
                            onClickUp = {
                                UpInfoActivity.actionStart(
                                    context,
                                    mid = videoInfo!!.owner.mid,
                                    name = videoInfo!!.owner.name
                                )
                            },
                            onAddFollow = {
                                modifyRelation(FollowAction.AddFollow) {
                                    updateRelationData()
                                }
                            },
                            onDelFollow = {
                                modifyRelation(FollowAction.DelFollow) {
                                    updateRelationData()
                                }
                            },
                            onClickTip = { tag ->
                                TagActivity.actionStart(
                                    context = context,
                                    tagId = tag.tagId,
                                    tagName = tag.tagName
                                )
                            }
                        )
                    }
                    item {
                        VideoDescription(
                            description = videoInfo?.desc ?: "no desc"
                        )
                    }
                    if (videoInfo?.isSeasonDisplay != true) {
                        item {
                            VideoPartRow(
                                pages = videoInfo?.pages ?: emptyList(),
                                lastPlayedCid = lastPlayedCid,
                                lastPlayedTime = lastPlayedTime,
                                onClick = { cid ->
                                    logger.fInfo { "Click video part: [av:${videoInfo?.aid}, bv:${videoInfo?.bvid}, cid:$cid]" }
                                    VideoPlayerActivity.actionStart(
                                        context = context,
                                        avid = videoInfo!!.aid,
                                        cid = cid,
                                        title = videoInfo!!.title,
                                        partTitle = videoInfo!!.pages.find { it.cid == cid }!!.part,
                                        played = if (cid == lastPlayedCid) lastPlayedTime else 0,
                                        fromSeason = false
                                    )
                                }
                            )
                        }
                    } else {
                        item {
                            VideoUgcSeasonRow(
                                episodes = videoInfo?.ugcSeason?.sections?.get(0)?.episodes
                                    ?: emptyList(),
                                lastPlayedCid = lastPlayedCid,
                                lastPlayedTime = lastPlayedTime,
                                onClick = { aid, cid ->
                                    logger.fInfo { "Click ugc season part: [av:${videoInfo?.aid}, bv:${videoInfo?.bvid}, cid:$cid]" }
                                    VideoPlayerActivity.actionStart(
                                        context = context,
                                        avid = aid,
                                        cid = cid,
                                        title = videoInfo!!.title,
                                        partTitle = videoInfo!!.ugcSeason!!.sections[0].episodes.find { it.cid == cid }!!.title,
                                        played = if (cid == lastPlayedCid) lastPlayedTime else 0,
                                        fromSeason = false
                                    )
                                }
                            )
                        }
                    }
                    item {
                        VideosRow(
                            header = stringResource(R.string.video_info_related_video_title),
                            videos = relatedVideos,
                            showMore = {}
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoInfoData(
    modifier: Modifier = Modifier,
    videoInfo: VideoInfo,
    relations: RelationData?,
    tags: List<Tag>,
    onClickCover: () -> Unit,
    onClickUp: () -> Unit,
    onAddFollow: () -> Unit,
    onDelFollow: () -> Unit,
    onClickTip: (Tag) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val localDensity = LocalDensity.current
    val focusRequester = remember { FocusRequester() }

    var heightIs by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    Row(
        modifier = modifier
            .padding(horizontal = 50.dp, vertical = 16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(3f)
                .aspectRatio(1.6f)
                .clip(MaterialTheme.shapes.large)
                .onGloballyPositioned { coordinates ->
                    heightIs = with(localDensity) { coordinates.size.height.toDp() }
                }
                .focusedBorder(MaterialTheme.shapes.large)
                .clickable { onClickCover() },
            model = videoInfo.ugcSeason?.cover ?: videoInfo.pic,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(7f)
                .height(heightIs),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = videoInfo.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UpButton(
                        name = videoInfo.owner.name,
                        followed = listOf(
                            RelationType.Followed,
                            RelationType.FollowedQuietly,
                            RelationType.BothFollowed
                        ).contains(relations?.relation?.attribute),
                        showFollowButton = relations != null,
                        onClickUp = onClickUp,
                        onAddFollow = onAddFollow,
                        onDelFollow = onDelFollow
                    )
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        R.string.video_info_time,
                        Date(videoInfo.ctime.toLong() * 1000).formatPubTimeString()
                    ),
                    maxLines = 1,
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Box(modifier = Modifier.focusable(true)) {}
                    FavoriteButton(
                        isFavorite = false,
                        onClick = { "还没写呢".toast(context) }
                    )
                    Box(modifier = Modifier.focusable(true)) {}
                }
                Text(
                    text = "点赞：${videoInfo.stat.like}",
                    color = Color.White
                )
                Text(
                    text = "投币：${videoInfo.stat.coin}",
                    color = Color.White
                )
                Text(
                    text = "收藏：${videoInfo.stat.favorite}",
                    color = Color.White
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.video_info_tags),
                    color = Color.White
                )
                TvLazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(tags) { tag ->
                        Surface(
                            modifier = Modifier
                                .focusedBorder(MaterialTheme.shapes.small),
                            color = Color.White.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small,
                            onClick = { onClickTip(tag) }
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp, 4.dp),
                                text = tag.tagName,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpButton(
    modifier: Modifier = Modifier,
    name: String,
    followed: Boolean,
    showFollowButton: Boolean = false,
    onClickUp: () -> Unit,
    onAddFollow: () -> Unit,
    onDelFollow: () -> Unit
) {
    val isLogin by remember { mutableStateOf(Prefs.isLogin) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(Color.White.copy(alpha = 0.2f))
                .focusedBorder(MaterialTheme.shapes.small)
                .padding(4.dp)
                .clickable { onClickUp() }
        ) {
            UpIcon(color = Color.White)
            Text(text = name, color = Color.White)
        }
        AnimatedVisibility(visible = isLogin && showFollowButton) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.White.copy(alpha = 0.2f))
                    .focusedBorder(MaterialTheme.shapes.small)
                    .padding(horizontal = 4.dp, vertical = 3.dp)
                    .clickable { if (followed) onDelFollow() else onAddFollow() }
                    .animateContentSize()
            ) {
                if (followed) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(text = stringResource(R.string.video_info_followed), color = Color.White)
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(text = stringResource(R.string.video_info_follow), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun VideoDescription(
    modifier: Modifier = Modifier,
    description: String
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)
    var showDescriptionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 50.dp),
    ) {
        Text(
            text = stringResource(R.string.video_info_description_title),
            fontSize = titleFontSize.sp,
            color = titleColor
        )
        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .onFocusChanged { hasFocus = it.hasFocus }
                .clip(MaterialTheme.shapes.medium)
                .focusedBorder(MaterialTheme.shapes.medium)
                .padding(8.dp)
                .clickable {
                    showDescriptionDialog = true
                }
        ) {
            Text(
                text = description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        }
    }

    VideoDescriptionDialog(
        show = showDescriptionDialog,
        onHideDialog = { showDescriptionDialog = false },
        description = description
    )
}

@Composable
fun VideoDescriptionDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    description: String
) {
    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = {
                Text(
                    text = stringResource(R.string.video_info_description_title),
                    color = Color.White
                )
            },
            text = {
                TvLazyColumn {
                    item {
                        Text(text = description)
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoPartButton(
    modifier: Modifier = Modifier,
    index: Int,
    title: String,
    duration: Int,
    played: Int = 0,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .focusedBorder(MaterialTheme.shapes.medium),
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium,
        onClick = { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(200.dp, 64.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.2f))
                    .fillMaxHeight()
                    .fillMaxWidth(if (played < 0) 1f else (played / (duration * 1000f)))
            ) {}
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = "P$index $title",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun VideoPartRow(
    modifier: Modifier = Modifier,
    pages: List<VideoPage>,
    lastPlayedCid: Int = 0,
    lastPlayedTime: Int = 0,
    onClick: (cid: Int) -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)

    Column(
        modifier = modifier
            .padding(start = 50.dp)
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.video_info_part_row_title),
            fontSize = titleFontSize.sp,
            color = titleColor
        )

        TvLazyRow(
            modifier = Modifier
                .padding(top = 15.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items = pages, key = { _, page -> page.cid }) { index, page ->
                VideoPartButton(
                    index = index + 1,
                    title = page.part,
                    played = if (page.cid == lastPlayedCid) lastPlayedTime else 0,
                    duration = page.duration,
                    onClick = { onClick(page.cid) }
                )
            }
        }
    }
}

@Composable
fun VideoUgcSeasonRow(
    modifier: Modifier = Modifier,
    episodes: List<UgcSeason.Section.Episode>,
    lastPlayedCid: Int = 0,
    lastPlayedTime: Int = 0,
    onClick: (avid: Int, cid: Int) -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)

    Column(
        modifier = modifier
            .padding(start = 50.dp)
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.video_info_ugc_season_row_title),
            fontSize = titleFontSize.sp,
            color = titleColor
        )

        TvLazyRow(
            modifier = Modifier
                .padding(top = 15.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items = episodes) { index, episode ->
                VideoPartButton(
                    index = index + 1,
                    title = episode.title,
                    played = if (episode.cid == lastPlayedCid) lastPlayedTime else 0,
                    duration = episode.arc.duration,
                    onClick = { onClick(episode.aid, episode.cid) }
                )
            }
        }
    }
}


@Preview
@Composable
fun VideoPartButtonShortTextPreview() {
    BVTheme {
        VideoPartButton(
            index = 2,
            title = "这是一段短文字",
            duration = 100,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun VideoPartButtonLongTextPreview() {
    BVTheme {
        VideoPartButton(
            index = 2,
            title = "这可能是我这辈子距离梅西最近的一次",
            played = 23333,
            duration = 100,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun VideoPartRowPreview() {
    val pages = remember { mutableStateListOf<VideoPage>() }
    for (i in 0..10) {
        pages.add(
            VideoPage(
                1000 + i, 0, "", "这可能是我这辈子距离梅西最近的一次", 10,
                "", "", Dimension(0, 0, 0)
            )
        )
    }
    BVTheme {
        VideoPartRow(pages = pages, onClick = {})
    }
}

@Preview
@Composable
fun VideoDescriptionPreview() {
    BVTheme {
        VideoDescription(description = "12435678")
    }
}

@Preview
@Composable
private fun UpButtonPreview() {
    var followed by remember { mutableStateOf(false) }
    BVTheme {
        UpButton(
            name = "12435678",
            followed = followed,
            onClickUp = { followed = !followed },
            onAddFollow = {},
            onDelFollow = {}
        )
    }
}