package dev.aaa1115910.bv.tv.screens.user

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.tv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.tv.manager.FollowStateManager
import dev.aaa1115910.bv.tv.util.ProvideListBringIntoViewSpec
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.user.UserSpaceViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UpSpaceScreen(
    modifier: Modifier = Modifier,
    userSpaceViewModel: UserSpaceViewModel = koinViewModel(),
    userRepository: UserRepository = getKoin().get()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }
    var currentIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 4 } }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )
    
    var showFollowButton by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }
    
    // 监听关注状态变化
    val followStateMap by FollowStateManager.followStateMap.collectAsState()
    
    // 当关注状态map变化时，更新当前用户的关注状态
    LaunchedEffect(followStateMap, userSpaceViewModel.upMid) {
        if (userSpaceViewModel.upMid > 0) {
            FollowStateManager.getFollowState(userSpaceViewModel.upMid)?.let { following ->
                isFollowing = following
            }
        }
    }

    val listFocusRequester = remember { FocusRequester() }
    LaunchedEffect(userSpaceViewModel.tvSpaceVideos.isNotEmpty()) {
        listFocusRequester.requestFocus()
    }

    val updateFollowingState: () -> Unit = {
        scope.launch(Dispatchers.IO) {
            val userMid = userSpaceViewModel.upMid
            
            // 先检查缓存中是否有关注状态
            val cachedState = FollowStateManager.getFollowState(userMid)
            if (cachedState != null) {
                withContext(Dispatchers.Main) {
                    showFollowButton = true && Prefs.isLogin
                    isFollowing = cachedState
                }
                return@launch
            }
            
            // 缓存中没有，调用API获取
            logger.fInfo { "Checking is following user $userMid" }
            val success = userRepository.checkIsFollowing(
                mid = userMid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Following user result: $success" }
            withContext(Dispatchers.Main) {
                showFollowButton = success != null && Prefs.isLogin
                if (success != null) {
                    isFollowing = success
                    // 更新到缓存中
                    FollowStateManager.updateFollowState(userMid, success)
                }
            }
        }
    }

    val addFollow: (afterModify: (success: Boolean) -> Unit) -> Unit = { afterModify ->
        scope.launch(Dispatchers.IO) {
            val userMid = userSpaceViewModel.upMid
            logger.fInfo { "Add follow to user $userMid" }
            val success = userRepository.followUser(
                mid = userMid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Add follow result: $success" }
            // 更新缓存状态
            if (success) {
                FollowStateManager.updateFollowState(userMid, true)
            }
            afterModify(success)
        }
    }

    val delFollow: (afterModify: (success: Boolean) -> Unit) -> Unit = { afterModify ->
        scope.launch(Dispatchers.IO) {
            val userMid = userSpaceViewModel.upMid
            logger.fInfo { "Del follow to user $userMid" }
            val success = userRepository.unfollowUser(
                mid = userMid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Del follow result: $success" }
            // 更新缓存状态
            if (success) {
                FollowStateManager.updateFollowState(userMid, false)
            }
            afterModify(success)
        }
    }

    LaunchedEffect(Unit) {
        val intent = (context as Activity).intent
        if (intent.hasExtra("mid")) {
            val mid = intent.getLongExtra("mid", 0)
            val name = intent.getStringExtra("name") ?: ""
            val face = intent.getStringExtra("face") ?: ""
            userSpaceViewModel.upMid = mid
            userSpaceViewModel.upName = name
            userSpaceViewModel.upFace = face
            userSpaceViewModel.update()
            updateFollowingState()
        } else {
            context.finish()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(userSpaceViewModel.upFace.isNotBlank()) {
                        val imageUrl = userSpaceViewModel.upFace+"@128w_128h_1c_1s.webp"
                        AsyncImage(
                            modifier = Modifier
                                .size(titleFontSize.dp)
                                .clip(CircleShape),
                            model = imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            onError = { error ->
                                userSpaceViewModel.upFace = ""

                                println("Failed to load avatar: $imageUrl")
                                println("Error message: ${error.result.throwable}")
                            },
                            onSuccess = { println("Avatar loaded successfully: $imageUrl") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = userSpaceViewModel.upName,
                        fontSize = titleFontSize.sp
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    // 关注按钮
                    if (showFollowButton) {
                        Surface(
                            modifier = Modifier
                                .offset(y = if (showLargeTitle) 4.dp else 2.dp)
                                .scale(if (showLargeTitle) 1f else 0.65f),
                            onClick = {
                                if (isFollowing) {
                                    delFollow { success ->
                                        scope.launch(Dispatchers.Main) {
                                            if (success) {
                                                "已取消关注".toast(context)
                                            } else {
                                                "取消关注失败".toast(context)
                                            }
                                        }
                                    }
                                } else {
                                    addFollow { success ->
                                        scope.launch(Dispatchers.Main) {
                                            if (success) {
                                                "关注成功".toast(context)
                                            } else {
                                                "关注失败".toast(context)
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ClickableSurfaceDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                pressedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            ),
                            shape = ClickableSurfaceDefaults.shape(
                                shape = MaterialTheme.shapes.small
                            ),
                            border = ClickableSurfaceDefaults.border(
                                focusedBorder = Border(
                                    border = BorderStroke(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    ),
                                    shape = MaterialTheme.shapes.small
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isFollowing) {
                                    Icon(
                                        imageVector = Icons.Rounded.Done,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.video_info_followed),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.video_info_follow),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.load_data_count,
                                userSpaceViewModel.tvSpaceVideos.size
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        AnimatedVisibility(visible = userSpaceViewModel.noMore) {
                            Text(
                                text = stringResource(R.string.load_data_no_more),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        ProvideListBringIntoViewSpec(padding = 26.dp) {
            LazyVerticalGrid(
                modifier = Modifier.padding(innerPadding),
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                itemsIndexed(
                    items = userSpaceViewModel.tvSpaceVideos,
                    key = { index, _ -> index }
                ) { index, video ->
                    SmallVideoCard(
                        modifier = Modifier.ifElse(index == 0, Modifier.focusRequester(listFocusRequester)),
                        data = video,
                        onClick = {
                            VideoInfoActivity.actionStart(
                                context = context,
                                aid = video.avid,
                                proxyArea = ProxyArea.checkProxyArea(video.title)
                            )
                        },
                        onFocus = {
                            currentIndex = index
                            if (index + 12 > userSpaceViewModel.tvSpaceVideos.size) {
                                userSpaceViewModel.update()
                            }
                        }
                    )
                }
            }
        }
    }
}

//https://i2.hdslb.com/bfs/face/ea9b2fd60b04b123d0b48477838f60532b6271cd.jpg
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UpFacePreview() {
    BVTheme {
        AsyncImage(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            model = "https://i2.hdslb.com/bfs/face/ea9b2fd60b04b123d0b48477838f60532b6271cd.jpg@80h_80w_1c.webp",
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}