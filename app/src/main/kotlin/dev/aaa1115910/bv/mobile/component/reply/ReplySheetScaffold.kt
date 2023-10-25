package dev.aaa1115910.bv.mobile.component.reply

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.origeek.imageViewer.previewer.ImagePreviewerState
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.CommentReplyPage
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import dev.aaa1115910.biliapi.repositories.VideoDetailRepository
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplySheetScaffold(
    modifier: Modifier = Modifier,
    aid: Int,
    rpid: Long,
    repliesCount: Int,
    sheetState: BottomSheetScaffoldState,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<Comment.Picture>, afterSetPictures: () -> Unit) -> Unit,
    videoDetailRepository: VideoDetailRepository = getKoin().get(),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val logger = KotlinLogging.logger("ReplySheetScaffold")

    var nextPage by remember { mutableStateOf(CommentReplyPage()) }
    var comment by remember { mutableStateOf<Comment?>(null) }
    val replies = remember { mutableStateListOf<Comment>() }
    var sort by remember { mutableStateOf(CommentSort.Time) }
    var hasNext by remember { mutableStateOf(true) }

    val loadMoreReply = {
        if (hasNext) {
            logger.info { "load more reply: [aid=$aid, rpid=$rpid, next=$nextPage]" }
            scope.launch(Dispatchers.IO) {
                runCatching {
                    val commentRepliesData = videoDetailRepository.getCommentReplies(
                        aid = aid,
                        commentId = rpid,
                        page = nextPage,
                        sort = sort,
                        preferApiType = Prefs.apiType
                    )
                    hasNext = commentRepliesData.hasNext
                    nextPage = commentRepliesData.nextPage
                    if (comment == null) comment = commentRepliesData.rootComment
                    replies.addAll(commentRepliesData.replies)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    val clearData = {
        hasNext = true
        comment = null
        replies.clear()
        nextPage = CommentReplyPage()
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 10
        }
    }

    val switchCommentSort: (CommentSort) -> Unit = { newSort ->
        sort = newSort
        clearData()
        loadMoreReply()
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) loadMoreReply()
    }

    LaunchedEffect(rpid) {
        clearData()
    }

    LaunchedEffect(sheetState.bottomSheetState.currentValue) {
        when (sheetState.bottomSheetState.currentValue) {
            SheetValue.Hidden, SheetValue.PartiallyExpanded -> clearData()
            SheetValue.Expanded -> loadMoreReply()
        }
    }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = sheetState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                state = listState
            ) {
                if (comment != null) {
                    item {
                        CommentItem(
                            comment = comment!!,
                            previewerState = previewerState,
                            onShowPreviewer = onShowPreviewer,
                            showReplies = false
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "相关回复共 $repliesCount 条",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = {
                            switchCommentSort(
                                when (sort) {
                                    CommentSort.Hot -> CommentSort.Time
                                    CommentSort.Time -> CommentSort.Hot
                                    else -> CommentSort.Hot
                                }
                            )
                        }) {
                            Text(
                                text = when (sort) {
                                    CommentSort.Hot -> "按热度"
                                    CommentSort.Time -> "按时间"
                                    else -> ""
                                }
                            )
                        }
                    }
                }

                itemsIndexed(items = replies) { index, reply ->
                    Box {
                        CommentItem(
                            comment = reply,
                            previewerState = previewerState,
                            onShowPreviewer = onShowPreviewer,
                            showReplies = false
                        )

                        if (BuildConfig.DEBUG) {
                            Text(text = "$index")
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun M() {
    BVMobileTheme {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                LargeTopAppBar(title = { Text("Title") })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text("Hello, World!")
                Button(onClick = {
                    scope.launch {
                        sheetState.partialExpand()
                    }
                }) {
                    Text(text = "partialExpand")
                }
                Button(onClick = {
                    scope.launch {
                        sheetState.expand()
                    }
                }) {
                    Text(text = "expand")
                }
                Button(onClick = {
                    scope.launch {
                        sheetState.show()
                    }
                }) {
                    Text(text = "show")
                }
                Button(onClick = {
                    scope.launch {
                        sheetState.hide()
                    }
                }) {
                    Text(text = "hide")
                }
            }
            /*ReplySheet(
                replies = emptyList(),
                repliesCount = 648,
                sheetState = sheetState
            )*/
        }

    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetSample() {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    // App content
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier.toggleable(
                value = skipPartiallyExpanded,
                role = Role.Checkbox,
                onValueChange = { checked -> skipPartiallyExpanded = checked }
            )
        ) {
            Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
            Spacer(Modifier.width(16.dp))
            Text("Skip partially expanded State")
        }
        Button(onClick = { openBottomSheet = !openBottomSheet }) {
            Text(text = "Show Bottom Sheet")
        }
    }

    // Sheet content
    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }
            var text by remember { mutableStateOf("") }
            OutlinedTextField(value = text, onValueChange = { text = it })
            LazyColumn {
                items(50) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SheetPreview() {
    BVMobileTheme {
        val scope = rememberCoroutineScope()
        val sheetState = rememberBottomSheetScaffoldState()
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 0.dp,
            sheetContent = {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "sheet")
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                ) {
                    Text(text = "content")
                    Text("Hello, World!")
                    Button(onClick = {
                        scope.launch {
                            sheetState.bottomSheetState.partialExpand()
                        }
                    }) {
                        Text(text = "partialExpand")
                    }
                    Button(onClick = {
                        scope.launch {
                            sheetState.bottomSheetState.expand()
                        }
                    }) {
                        Text(text = "expand")
                    }
                    Button(onClick = {
                        scope.launch {
                            sheetState.bottomSheetState.show()
                        }
                    }) {
                        Text(text = "show")
                    }
                    Button(onClick = {
                        scope.launch {
                            sheetState.bottomSheetState.hide()
                        }
                    }) {
                        Text(text = "hide")
                    }
                }
            }
        }
    }
}