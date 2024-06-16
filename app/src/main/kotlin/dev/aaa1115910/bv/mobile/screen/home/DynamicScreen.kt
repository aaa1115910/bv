package dev.aaa1115910.bv.mobile.screen.home

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.origeek.imageViewer.previewer.ImagePreviewerState
import com.origeek.imageViewer.previewer.rememberPreviewerState
import dev.aaa1115910.biliapi.entity.Picture
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import dev.aaa1115910.biliapi.entity.user.DynamicItem
import dev.aaa1115910.biliapi.entity.user.DynamicType
import dev.aaa1115910.bv.mobile.component.home.dynamic.DynamicContent
import dev.aaa1115910.bv.mobile.component.home.dynamic.DynamicHeader
import dev.aaa1115910.bv.mobile.component.home.dynamic.DynamicItem
import dev.aaa1115910.bv.mobile.component.reply.Comments
import dev.aaa1115910.bv.mobile.component.reply.Replies
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.mobile.viewmodel.CommentViewModel
import dev.aaa1115910.bv.player.mobile.util.ifElse
import dev.aaa1115910.bv.util.getLane
import dev.aaa1115910.bv.util.isScrolledToEnd
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.navigation.koinNavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicScreen(
    modifier: Modifier = Modifier,
    dynamicViewModel: DynamicViewModel = koinNavViewModel(),
    commentViewModel: CommentViewModel = koinViewModel(),
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val dynamicGridState = rememberLazyStaggeredGridState()
    val endOfGridReached by remember { derivedStateOf { dynamicGridState.isScrolledToEnd() } }
    val lane by remember { derivedStateOf { dynamicGridState.getLane() } }
    var currentDynamicItem by remember { mutableStateOf<DynamicItem?>(null) }

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    var backProgressValue by remember { mutableFloatStateOf(0f) }
    val backProgress by animateFloatAsState(targetValue = backProgressValue, label = "BackProgress")

    val onClickDynamicItem: (DynamicItem) -> Unit = { dynamicItem ->
        currentDynamicItem = dynamicItem
        commentViewModel.apply {
            commentId = dynamicItem.commentId
            commentType = dynamicItem.commentType
        }
        scope.launch(Dispatchers.IO) {
            commentViewModel.refreshComments()
        }
        openBottomSheet = true
        /*if (dynamicItem.type == DynamicType.Av)
            VideoPlayerActivity.actionStart(
                context = context,
                aid = dynamicItem.video!!.aid,
                fromSeason = dynamicItem.video!!.seasonId != 0
            )*/
    }

    LaunchedEffect(Unit) {
        dynamicViewModel.loadMoreAll()
    }

    LaunchedEffect(endOfGridReached) {
        dynamicViewModel.loadMoreAll()
    }

    PredictiveBackHandler(openBottomSheet) { progress ->
        progress.collect { backEvent ->
            backProgressValue = backEvent.progress
        }
        scope.launch {
            bottomSheetState.hide()
        }.invokeOnCompletion {
            openBottomSheet = false
            backProgressValue = 0f
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Dynamic") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {
            LazyVerticalStaggeredGrid(
                modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                columns = StaggeredGridCells.Adaptive(300.dp),
                state = dynamicGridState,
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(if (lane == 1) 0.dp else 8.dp)
            ) {
                items(items = dynamicViewModel.dynamicAllList) { dynamicItem ->
                    DynamicItem(
                        modifier = Modifier
                            .ifElse(lane != 1, Modifier.clip(MaterialTheme.shapes.medium)),
                        dynamicItem = dynamicItem,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        onClick = onClickDynamicItem
                    )
                }
            }
        }
    }

    DynamicSheet(
        openBottomSheet = openBottomSheet,
        bottomSheetState = bottomSheetState,
        backProgress = backProgress,
        onHideBottomSheet = { openBottomSheet = false },

        dynamicItem = currentDynamicItem,
        previewerState = previewerState,
        onShowPreviewer = onShowPreviewer,
        onClick = onClickDynamicItem,

        comments = commentViewModel.comments,
        replies = commentViewModel.replies,
        replyRootComment = commentViewModel.replyRootComment,
        commentSort = commentViewModel.commentSort,
        replySort = commentViewModel.replySort,
        commentsSize = 0,
        repliesSize = 0,

        onClickComment = { comment ->
            scope.launch(Dispatchers.IO) {
                commentViewModel.rpid = comment.rpid
                commentViewModel.refreshReplies()
            }
        },
        onSwitchCommentSort = { commentSort ->
            scope.launch(Dispatchers.IO) {
                commentViewModel.switchCommentSort(commentSort)
            }
        },
        onSwitchReplySort = { replySort ->
            scope.launch(Dispatchers.IO) {
                commentViewModel.replySort = replySort
                commentViewModel.refreshReplies()
            }
        },
        onClearReplies = {
            commentViewModel.rpid = 0
            commentViewModel.replies.clear()
            commentViewModel.replyRootComment = null
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSheet(
    modifier: Modifier = Modifier,
    openBottomSheet: Boolean,
    bottomSheetState: SheetState,
    backProgress: Float,
    onHideBottomSheet: () -> Unit,

    dynamicItem: DynamicItem?,
    previewerState: ImagePreviewerState = rememberPreviewerState(pageCount = { 0 }),
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit = { _, _ -> },
    onClick: (DynamicItem) -> Unit,

    comments: List<Comment>,
    replies: List<Comment>,
    replyRootComment: Comment?,
    commentSort: CommentSort,
    replySort: CommentSort,
    commentsSize: Int,
    repliesSize: Int,

    onClickComment: (Comment) -> Unit,
    onSwitchCommentSort: (CommentSort) -> Unit,
    onSwitchReplySort: (CommentSort) -> Unit,
    onClearReplies: () -> Unit
) {
    var paddingTopValue = if (openBottomSheet) 0.dp else 300.dp
    val paddingTop by animateDpAsState(targetValue = paddingTopValue, label = "paddingTop")

    /*LaunchedEffect(previewerState.visible) {
        if (previewerState.visible){
            onHideBottomSheet()
        }else{
            bottomSheetState.show()
        }
    }*/
    if (openBottomSheet) {
        ModalBottomSheet(
            modifier = modifier
                .statusBarsPadding()
                .padding(top = paddingTop)
                .graphicsLayer(
                    scaleX = 1F - (0.1F * backProgress),
                    scaleY = 1F - (0.1F * backProgress),
                    transformOrigin = remember { TransformOrigin(0.5f, 1f) },
                ),
            onDismissRequest = onHideBottomSheet,
            sheetState = bottomSheetState,
            properties = ModalBottomSheetDefaults.properties(
                isFocusable = false
            ),
            dragHandle = null
        ) {
            DynamicSheetContent(
                dynamicItem = dynamicItem,
                previewerState = previewerState,
                onShowPreviewer = onShowPreviewer,
                onClick = onClick,
                comments = comments,
                replies = replies,
                replyRootComment = replyRootComment,
                commentSort = commentSort,
                replySort = replySort,
                commentsSize = commentsSize,
                repliesSize = repliesSize,
                onClickComment = onClickComment,
                onSwitchCommentSort = onSwitchCommentSort,
                onSwitchReplySort = onSwitchReplySort,
                onClearReplies = onClearReplies
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DynamicSheetContent(
    modifier: Modifier = Modifier,
    dynamicItem: DynamicItem?,
    previewerState: ImagePreviewerState = rememberPreviewerState(pageCount = { 0 }),
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit = { _, _ -> },
    onClick: (DynamicItem) -> Unit,
    comments: List<Comment>,
    replies: List<Comment>,
    replyRootComment: Comment?,
    commentSort: CommentSort,
    replySort: CommentSort,
    commentsSize: Int,
    repliesSize: Int,
    onClickComment: (Comment) -> Unit,
    onSwitchCommentSort: (CommentSort) -> Unit,
    onSwitchReplySort: (CommentSort) -> Unit,
    onClearReplies: () -> Unit
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator()

    ListDetailPaneScaffold(
        modifier = modifier,
        value = scaffoldNavigator.scaffoldValue,
        directive = scaffoldNavigator.scaffoldDirective,
        listPane = {
            AnimatedPane(
                modifier = Modifier.fillMaxSize(),
            ) {
                Surface(
                    onClick = {
                        //scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                ) {
                    DynamicSheetContentDetail(
                        dynamicItem = dynamicItem,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        onClick = onClick,
                        comments = comments,
                        replies = replies,
                        replyRootComment = replyRootComment,
                        commentSort = commentSort,
                        replySort = replySort,
                        commentsSize = commentsSize,
                        repliesSize = repliesSize,
                        onClickComment = onClickComment,
                        onSwitchCommentSort = onSwitchCommentSort,
                        onSwitchReplySort = onSwitchReplySort,
                        onClearReplies = onClearReplies
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane(modifier = Modifier) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    onClick = {
                        scaffoldNavigator.navigateBack()
                    }
                ) {
                    DynamicSheetContentReply(
                        comment = replyRootComment,
                        sort = replySort,
                        replies = replies,
                        onSwitchSort = onSwitchReplySort,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        repliesCount = repliesSize
                    )
                }
            }
        }
    )
}

@Composable
fun DynamicSheetContentDetail(
    modifier: Modifier = Modifier,
    dynamicItem: DynamicItem?,
    previewerState: ImagePreviewerState = rememberPreviewerState(pageCount = { 0 }),
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit = { _, _ -> },
    onClick: (DynamicItem) -> Unit,
    comments: List<Comment>,
    replies: List<Comment>,
    replyRootComment: Comment?,
    commentSort: CommentSort,
    replySort: CommentSort,
    commentsSize: Int,
    repliesSize: Int,
    onClickComment: (Comment) -> Unit,
    onSwitchCommentSort: (CommentSort) -> Unit,
    onSwitchReplySort: (CommentSort) -> Unit,
    onClearReplies: () -> Unit
) {
    val horizontalPadding = 12.dp
    Column(
        modifier = modifier
    ) {
        Comments(
            header = {
                Column {
                    if (dynamicItem != null) {
                        DynamicHeader(
                            modifier = Modifier.padding(horizontal = horizontalPadding),
                            author = dynamicItem.author
                        )
                    }
                    if (dynamicItem != null) {
                        DynamicContent(
                            dynamicItem = dynamicItem,
                            horizontalPadding = horizontalPadding,
                            previewerState = previewerState,
                            onShowPreviewer = onShowPreviewer,
                            onClick = onClick
                        )
                    }
                }
            },
            previewerState = previewerState,
            comments = comments,
            commentSort = commentSort,
            refreshingComments = false,
            onLoadMoreComments = { /*TODO*/ },
            onRefreshComments = { /*TODO*/ },
            onSwitchCommentSort = onSwitchCommentSort,
            onShowPreviewer = onShowPreviewer,
            onShowReplies = { _, _ -> }
        )
    }
}

@Composable
fun DynamicSheetContentReply(
    modifier: Modifier = Modifier,
    comment: Comment?,
    sort: CommentSort,
    replies: List<Comment>,
    onSwitchSort: (CommentSort) -> Unit,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (List<Picture>, () -> Unit) -> Unit,
    repliesCount: Int
) {
    Replies(
        modifier = modifier,
        comment = comment,
        sort = sort,
        replies = replies,
        onSwitchSort = onSwitchSort,
        previewerState = previewerState,
        onShowPreviewer = onShowPreviewer,
        repliesCount = repliesCount
    )
}


private val exampleAuthorData = DynamicItem.DynamicAuthorModule(
    author = "author",
    avatar = "",
    mid = 0,
    pubTime = "54 分钟前 投稿了视频",
    pubAction = ""
)

private val exampleFooterData = DynamicItem.DynamicFooterModule(
    like = 2,
    comment = 61,
    share = 8,
)

private val exampleVideoData = DynamicItem.DynamicVideoModule(
    aid = 0,
    title = "title",
    cover = "",
    duration = "23:45",
    play = "xx play",
    danmaku = "xx dm",
    seasonId = 0,
    cid = 0,
    text = "desc"
)

private val exampleDynamicItemData = DynamicItem(
    type = DynamicType.Av,
    author = exampleAuthorData,
    video = exampleVideoData,
    footer = exampleFooterData
)

@Preview
@Composable
private fun DynamicSheetContentDetailPreview() {
    BVMobileTheme {
        Surface {
            DynamicSheetContentDetail(
                dynamicItem = exampleDynamicItemData,
                onClick = {},
                comments = emptyList(),
                replies = emptyList(),
                replyRootComment = null,
                commentSort = CommentSort.Hot,
                replySort = CommentSort.Time,
                commentsSize = 0,
                repliesSize = 0,
                onClickComment = {},
                onSwitchCommentSort = {},
                onSwitchReplySort = {},
                onClearReplies = {}
            )
        }
    }
}

@Preview
@Composable
private fun DynamicSheetContentReplyPreview() {
    BVMobileTheme {
        Surface {
            DynamicSheetContentReply(
                comment = null,
                sort = CommentSort.Hot,
                replies = emptyList(),
                onSwitchSort = {},
                previewerState = rememberPreviewerState(pageCount = { 0 }),
                onShowPreviewer = { _, _ -> },
                repliesCount = 0
            )
        }
    }
}