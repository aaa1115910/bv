package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import android.content.Context
import androidx.activity.BackEventCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.origeek.imageViewer.previewer.ImagePreviewer
import com.origeek.imageViewer.previewer.ImagePreviewerState
import com.origeek.imageViewer.previewer.VerticalDragType
import com.origeek.imageViewer.previewer.rememberPreviewerState
import dev.aaa1115910.biliapi.entity.Picture
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import dev.aaa1115910.biliapi.entity.user.DynamicItem
import dev.aaa1115910.bv.mobile.component.home.dynamic.DynamicContent
import dev.aaa1115910.bv.mobile.component.home.dynamic.DynamicHeader
import dev.aaa1115910.bv.mobile.component.reply.Comments
import dev.aaa1115910.bv.mobile.component.reply.Replies
import dev.aaa1115910.bv.mobile.viewmodel.CommentViewModel
import dev.aaa1115910.bv.mobile.viewmodel.DynamicDetailViewModel
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapList
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.min

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun DynamicDetailScreen(
    modifier: Modifier = Modifier,
    dynamicDetailViewModel: DynamicDetailViewModel = koinViewModel(),
    commentViewModel: CommentViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val pictures = remember { mutableStateListOf<Picture>() }
    val dynamicDetailState = rememberDynamicDetailState(
        dynamicDetailViewModel = dynamicDetailViewModel,
        commentViewModel = commentViewModel,
        imagePreviewerState = rememberPreviewerState(
            verticalDragType = VerticalDragType.UpAndDown,
            pageCount = { pictures.size },
            getKey = { pictures[it].key }
        )
    )

    val onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit =
        { newPictures, afterSetPictures ->
            pictures.swapList(newPictures)
            afterSetPictures()
        }

    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
        DynamicDetailMobileContent(
            modifier = modifier,
            dynamicDetailState = dynamicDetailState,
            onShowPreviewer = onShowPreviewer,
        )
    } else {
        DynamicDetailScreenPadContent(
            modifier = modifier,
            dynamicDetailState = dynamicDetailState,
            onShowPreviewer = onShowPreviewer,
        )
    }

    ImagePreviewer(
        modifier = Modifier
            .fillMaxSize(),
        state = dynamicDetailState.imagePreviewerState,
        imageLoader = { index ->
            val imageRequest = ImageRequest.Builder(context)
                .data(pictures[index].url)
                .size(Size.ORIGINAL)
                .build()
            rememberAsyncImagePainter(imageRequest)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DynamicDetailMobileContent(
    modifier: Modifier = Modifier,
    dynamicDetailState: DynamicDetailState,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val logger = KotlinLogging.logger { }
    val screenHeight = with(density) { context.resources.displayMetrics.heightPixels.toDp() }

    var showMask by remember { mutableStateOf(false) }
    var showReplies by remember { mutableStateOf(false) }

    val onRepliesCloseAnimationFinish: (Dp) -> Unit = { finishDp ->
        logger.fInfo { "onRepliesCloseAnimationFinish: $finishDp" }
        if (finishDp == screenHeight) {
            showReplies = false
            showMask = false
        }
    }

    var maskAlphaTarget by remember { mutableFloatStateOf(0.5f) }
    val maskAlpha by animateFloatAsState(
        targetValue = maskAlphaTarget,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "replies scrim mask alpha"
    )
    var repliesOffsetYTarget by remember { mutableStateOf(0.dp) }
    val repliesOffsetY by animateDpAsState(
        targetValue = repliesOffsetYTarget,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "replies offset y",
        finishedListener = onRepliesCloseAnimationFinish
    )
    var repliesScaleTarget by remember { mutableFloatStateOf(1f) }
    val repliesScale by animateFloatAsState(
        targetValue = repliesScaleTarget,
        label = "replies scale"
    )
    val repliesRoundCorner by animateDpAsState(
        targetValue = if (repliesScaleTarget == 1f) 0.dp else 28.dp,
        label = "replies round corner"
    )

    val onCloseReplies: () -> Unit = {
        maskAlphaTarget = 0f
        repliesOffsetYTarget = screenHeight
    }

    LaunchedEffect(showMask) {
        maskAlphaTarget = if (showMask) 0.5f else 0f
    }

    LaunchedEffect(showReplies) {
        repliesOffsetYTarget = if (showReplies) 0.dp else screenHeight
        if (showReplies) repliesScaleTarget = 1f
        showMask = showReplies
    }

    PredictiveBackHandler(showMask) { progress: Flow<BackEventCompat> ->
        runCatching {
            progress.collect { backEvent ->
                maskAlphaTarget = (1 - backEvent.progress * 0.8f) * 0.5f
                repliesOffsetYTarget = (backEvent.progress * 200).dp
                repliesScaleTarget = 1 - min(0.6f, backEvent.progress) * 0.2f
            }
            onCloseReplies()
        }.onFailure {
            maskAlphaTarget = 0.5f
            repliesOffsetYTarget = 0.dp
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dynamic Detail") },
                    navigationIcon = {
                        IconButton(onClick = dynamicDetailState.onExitActivity) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (dynamicDetailState.dynamicItem != null) {
                CommentPart(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    previewerState = dynamicDetailState.imagePreviewerState,
                    comments = dynamicDetailState.comments,
                    commentSort = dynamicDetailState.commentSort,
                    isLoading = dynamicDetailState.isLoadingComments,
                    isRefreshing = dynamicDetailState.isRefreshingComments,
                    onLoadMoreComments = dynamicDetailState::loadMoreComments,
                    onRefreshComments = dynamicDetailState::refreshComments,
                    onSwitchCommentSort = dynamicDetailState::switchCommentSort,
                    onShowPreviewer = onShowPreviewer,
                    onShowReplies = { comment ->
                        dynamicDetailState.updateCurrentComment(comment)
                        dynamicDetailState.refreshReplies()
                        showReplies = true
                    },
                    header = {
                        DynamicPart(
                            modifier = Modifier,
                            dynamicItem = dynamicDetailState.dynamicItem,
                            previewerState = dynamicDetailState.imagePreviewerState,
                            onShowPreviewer = onShowPreviewer
                        )
                    }
                )
            } else {
                CircularProgressIndicator()
            }
        }

        // Dark mask
        if (showMask)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = maskAlpha))
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = { }
                    )
            ) {}

        // replies
        if (showReplies) {
            ReplyPart(
                modifier = Modifier
                    .offset(y = repliesOffsetY)
                    .scale(repliesScale)
                    .clip(
                        RoundedCornerShape(
                            topStart = repliesRoundCorner,
                            topEnd = repliesRoundCorner
                        )
                    ),
                comment = dynamicDetailState.replyComment,
                sort = dynamicDetailState.replySort,
                replies = dynamicDetailState.replies,
                previewerState = dynamicDetailState.imagePreviewerState,
                repliesCount = dynamicDetailState.replyComment?.repliesCount ?: 0,
                isLoading = dynamicDetailState.isLoadingReplies,
                isRefreshing = dynamicDetailState.isRefreshingReplies,
                onShowPreviewer = onShowPreviewer,
                onCloseReplies = onCloseReplies,
                onSwitchSort = dynamicDetailState::switchReplySort,
                onRefreshReplies = dynamicDetailState::refreshReplies,
                onLoadMoreReplies = dynamicDetailState::loadMoreReplies,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DynamicDetailScreenPadContent(
    modifier: Modifier = Modifier,
    dynamicDetailState: DynamicDetailState,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val screenWidth = with(density) { context.resources.displayMetrics.widthPixels.toDp() }

    var showReplies by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Dynamic Detail") },
                navigationIcon = {
                    IconButton(onClick = dynamicDetailState.onExitActivity) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                DynamicPart(
                    modifier = Modifier
                        .width(screenWidth / 3 - 10.dp)
                        .verticalScroll(rememberScrollState()),
                    dynamicItem = dynamicDetailState.dynamicItem,
                    previewerState = dynamicDetailState.imagePreviewerState,
                    onShowPreviewer = onShowPreviewer
                )
                AnimatedVisibility(
                    visible = dynamicDetailState.dynamicItem != null,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally()
                ) {
                    CommentPart(
                        modifier = Modifier.width(screenWidth / 3 - 10.dp),
                        previewerState = dynamicDetailState.imagePreviewerState,
                        comments = dynamicDetailState.comments,
                        commentSort = dynamicDetailState.commentSort,
                        isLoading = dynamicDetailState.isLoadingComments,
                        isRefreshing = dynamicDetailState.isRefreshingComments,
                        onLoadMoreComments = dynamicDetailState::loadMoreComments,
                        onRefreshComments = dynamicDetailState::refreshComments,
                        onSwitchCommentSort = dynamicDetailState::switchCommentSort,
                        onShowPreviewer = onShowPreviewer,
                        onShowReplies = { comment ->
                            dynamicDetailState.updateCurrentComment(comment)
                            dynamicDetailState.refreshReplies()
                            showReplies = true
                        }
                    )
                }
                AnimatedVisibility(
                    visible = showReplies,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally()
                ) {
                    ReplyPart(
                        modifier = Modifier.width(screenWidth / 3 - 10.dp),
                        comment = dynamicDetailState.replyComment,
                        sort = dynamicDetailState.replySort,
                        replies = dynamicDetailState.replies,
                        previewerState = dynamicDetailState.imagePreviewerState,
                        repliesCount = dynamicDetailState.replyComment?.repliesCount ?: 0,
                        isLoading = dynamicDetailState.isLoadingReplies,
                        isRefreshing = dynamicDetailState.isRefreshingReplies,
                        enableTopPadding = false,
                        onShowPreviewer = onShowPreviewer,
                        onCloseReplies = { showReplies = false },
                        onSwitchSort = dynamicDetailState::switchReplySort,
                        onRefreshReplies = dynamicDetailState::refreshReplies,
                        onLoadMoreReplies = dynamicDetailState::loadMoreReplies,
                    )
                }
            }
        }
    }
}

data class DynamicDetailState(
    val context: Context,
    val scope: CoroutineScope,
    val dynamicDetailViewModel: DynamicDetailViewModel,
    val commentViewModel: CommentViewModel,
    val imagePreviewerState: ImagePreviewerState
) {
    val dynamicItem get() = dynamicDetailViewModel.dynamicItem
    val comments get() = commentViewModel.comments
    val replies get() = commentViewModel.replies
    var replyComment by mutableStateOf<Comment?>(null)

    val commentSort get() = commentViewModel.commentSort
    val replySort get() = commentViewModel.replySort
    val isRefreshingComments get() = commentViewModel.refreshingComments
    val isRefreshingReplies get() = commentViewModel.refreshingReplies
    val isLoadingComments get() = commentViewModel.updatingComments
    val isLoadingReplies get() = commentViewModel.updatingReplies
    val hasMoreComments get() = commentViewModel.hasMoreComments
    val hasMoreReplies get() = commentViewModel.hasMoreReplies

    fun loadMoreComments() {
        scope.launch(Dispatchers.IO) {
            commentViewModel.loadMoreComment()
        }
    }

    fun loadMoreReplies() {
        scope.launch(Dispatchers.IO) {
            commentViewModel.loadMoreReplies()
        }
    }

    fun updateCurrentComment(comment: Comment) {
        replyComment = comment
        commentViewModel.commentId = comment.oid
        commentViewModel.commentType = comment.type
        commentViewModel.rpid = comment.rpid
    }

    fun switchCommentSort(newSort: CommentSort) {
        scope.launch(Dispatchers.IO) {
            commentViewModel.switchCommentSort(newSort)
        }
    }

    fun switchReplySort(newSort: CommentSort) {
        scope.launch(Dispatchers.IO) {
            commentViewModel.switchReplySort(newSort)
        }
    }

    fun refreshComments() {
        scope.launch(Dispatchers.IO) {
            commentViewModel.refreshComments()
        }
    }

    fun refreshReplies() {
        scope.launch(Dispatchers.IO) {
            commentViewModel.refreshReplies()
        }
    }

    val onExitActivity: () -> Unit = { (context as Activity).finish() }
}

@Composable
fun rememberDynamicDetailState(
    dynamicDetailViewModel: DynamicDetailViewModel,
    commentViewModel: CommentViewModel,
    imagePreviewerState: ImagePreviewerState
): DynamicDetailState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BackHandler(imagePreviewerState.canClose || imagePreviewerState.animating) {
        if (imagePreviewerState.canClose) scope.launch {
            imagePreviewerState.closeTransform()
        }
    }

    LaunchedEffect(Unit) {
        val intent = (context as Activity).intent
        val dynamicId = intent.getStringExtra("dynamicId")
        dynamicId?.let { dynamicDetailViewModel.dynamicId = dynamicId } ?: context.finish()

        scope.launch(Dispatchers.IO) {
            dynamicDetailViewModel.loadDynamic()
            if (dynamicDetailViewModel.dynamicItem?.commentId != null && dynamicDetailViewModel.dynamicItem?.commentType != null) {
                commentViewModel.commentId = dynamicDetailViewModel.dynamicItem!!.commentId
                commentViewModel.commentType = dynamicDetailViewModel.dynamicItem!!.commentType
                //commentViewModel.loadMoreComment()
            }
        }
    }

    return remember(
        dynamicDetailViewModel,
        commentViewModel,
        imagePreviewerState
    ) {
        DynamicDetailState(
            context = context,
            scope = scope,
            dynamicDetailViewModel = dynamicDetailViewModel,
            commentViewModel = commentViewModel,
            imagePreviewerState = imagePreviewerState
        )
    }
}

@Composable
private fun DynamicPart(
    modifier: Modifier = Modifier,
    dynamicItem: DynamicItem?,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        if (dynamicItem != null) {
            DynamicHeader(
                modifier = Modifier
                    .padding(12.dp),
                author = dynamicItem.author
            )
        }
        if (dynamicItem != null) {
            DynamicContent(
                modifier = Modifier
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                dynamicItem = dynamicItem,
                previewerState = previewerState,
                onShowPreviewer = onShowPreviewer,
                onClick = { }
            )
        }
    }
}

@Composable
private fun CommentPart(
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    previewerState: ImagePreviewerState,
    comments: List<Comment>,
    commentSort: CommentSort,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadMoreComments: () -> Unit,
    onRefreshComments: () -> Unit,
    onSwitchCommentSort: (CommentSort) -> Unit,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit,
    onShowReplies: (comment: Comment) -> Unit
) {
    Comments(
        modifier = modifier,
        header = header,
        previewerState = previewerState,
        comments = comments,
        commentSort = commentSort,
        isLoading = isLoading,
        isRefreshing = isRefreshing,
        onLoadMoreComments = onLoadMoreComments,
        onRefreshComments = onRefreshComments,
        onSwitchCommentSort = onSwitchCommentSort,
        onShowPreviewer = onShowPreviewer,
        onShowReplies = onShowReplies,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ReplyPart(
    modifier: Modifier = Modifier,
    previewerState: ImagePreviewerState,
    comment: Comment?,
    sort: CommentSort,
    replies: List<Comment>,
    repliesCount: Int,
    isLoading: Boolean,
    isRefreshing: Boolean,
    enableTopPadding: Boolean = true,
    onSwitchSort: (CommentSort) -> Unit,
    onShowPreviewer: (List<Picture>, () -> Unit) -> Unit,
    onCloseReplies: () -> Unit,
    onRefreshReplies: () -> Unit,
    onLoadMoreReplies: () -> Unit
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = { Text("Replies") },
                navigationIcon = {
                    IconButton(onClick = onCloseReplies) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                windowInsets = if (enableTopPadding) TopAppBarDefaults.windowInsets
                else WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
        }
    ) { innerPadding ->
        Replies(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            previewerState = previewerState,
            rootComment = comment,
            replySort = sort,
            replies = replies,
            repliesCount = repliesCount,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            onSwitchReplySort = onSwitchSort,
            onShowPreviewer = onShowPreviewer,
            onLoadMoreReplies = onLoadMoreReplies,
            onRefreshReplies = onRefreshReplies,
        )
    }
}
