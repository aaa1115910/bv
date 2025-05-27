package dev.aaa1115910.bv.tv.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.isDpadRight
import dev.aaa1115910.bv.util.isKeyDown
import dev.aaa1115910.bv.util.onDelayFocusChanged

// 创建全局的FocusRequester映射表，方便外部使用
val drawerItemFocusRequesters = mutableMapOf<DrawerItem, FocusRequester>().apply {
    DrawerItem.entries.filter { it != DrawerItem.User && it != DrawerItem.Settings }
        .forEach { item ->
            this[item] = FocusRequester()
        }
}

// 用于记住每个内容页当前选中的Tab
val currentSelectedTabs = mutableStateMapOf<DrawerItem, Int>()

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    isLogin: Boolean = false,
    avatar: String = "",
    username: String = "",
    onDrawerItemChanged: (DrawerItem) -> Unit = {},
    onDrawerItemfocused: (DrawerItem) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onShowUserPanel: () -> Unit = {},
    onFocusToContent: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(DrawerItem.Home) }
    // 添加一个新的状态用于即时跟踪获得焦点的项目
    var focusedItem by remember { mutableStateOf(DrawerItem.Home) }
    val centerFocusRequester = remember { FocusRequester() }
    var centerPartNotFocused by remember { mutableStateOf(false) }

    var focusOnContent by remember { mutableStateOf(true) }

    LaunchedEffect(selectedItem) {
        tabMoved = false
        delay(200)
        onDrawerItemChanged(selectedItem)
        // 别急着向右移动焦点，动画还没结束
        delay(200)
        tabMoved = true
    }

    LaunchedEffect(focusedItem) {
        onDrawerItemfocused(focusedItem)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.isDpadRight()) {
                    if (keyEvent.isKeyDown()) {
                        if (tabMoved) onFocusToContent()
                        return@onPreviewKeyEvent true
                    }
                }
                false
            }
            .onFocusChanged { focusOnContent = !it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        NavigationRailItem(
            modifier = Modifier
                .onFocusChanged {
                    centerPartNotFocused = it.hasFocus
                    if (it.hasFocus) {
                        focusedItem = DrawerItem.User
                    }
                },
            onClick = {
                if (isLogin) {
                    onShowUserPanel()
                } else {
                    onLogin()
                }
                focusedItem = DrawerItem.User
            },
            selected = focusedItem == DrawerItem.User,
            colors = NavigationRailItemDefaults.colors(
                indicatorColor = if (focusOnContent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.inverseSurface
            ),
            icon = {
                if (isLogin) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        colors = SurfaceDefaults.colors(
                            containerColor = Color.Gray
                        )
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            model = avatar,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    }
                } else {
                    Icon(
                        imageVector = DrawerItem.User.displayIcon,
                        contentDescription = null,
                        tint = if (!focusOnContent && focusedItem == DrawerItem.User) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface
                    )
                }
            },
            label = { Text(
                modifier = Modifier.offset(y = (-4).dp),
                text = if (isLogin) username
                else DrawerItem.User.displayName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            ) }
        )
        LazyColumn(
            modifier = Modifier.focusRestorer(centerFocusRequester),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            listOf(
                DrawerItem.Search,
                DrawerItem.Home,
                DrawerItem.UGC,
                DrawerItem.PGC,
            ).forEach { item ->
                item {
                    NavigationRailItem(
                        modifier = Modifier
                            .focusRequester(drawerItemFocusRequesters[item]!!)
                            // 立即更新focusedItem以反映视觉状态
                            .onFocusChanged {
                                if (it.hasFocus) {
                                    focusedItem = item
                                }
                            }
                            // 延迟更新selectedItem以延迟右侧内容的切换
                            .onDelayFocusChanged {
                                if (it.hasFocus) selectedItem = item
                            }
                            .ifElse(
                                item == DrawerItem.Home,
                                Modifier.focusRequester(centerFocusRequester)
                            ),
                        onClick = {
                            selectedItem = item
                            focusedItem = item
                        },
                        // 使用focusedItem来决定视觉状态
                        selected = !centerPartNotFocused && focusedItem == item,
                        colors = NavigationRailItemDefaults.colors(
                            indicatorColor = if (focusOnContent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.inverseSurface
                        ),
                        icon = {
                            Icon(
                                imageVector = item.displayIcon,
                                contentDescription = null,
                                tint = if (!focusOnContent && focusedItem == item) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface
                            )
                        },
                        label = {
                            Text(
                                modifier = Modifier.offset(y = (-4).dp),
                                text = item.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
        }
        NavigationRailItem(
            modifier = Modifier
                .onFocusChanged {
                    centerPartNotFocused = it.hasFocus
                    if (it.hasFocus) {
                        focusedItem = DrawerItem.Settings
                    }
                },
            onClick = {
                onOpenSettings()
                focusedItem = DrawerItem.Settings
            },
            selected = focusedItem == DrawerItem.Settings,
            colors = NavigationRailItemDefaults.colors(
                indicatorColor = if (focusOnContent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.inverseSurface
            ),
            icon = {
                Icon(
                    imageVector = DrawerItem.Settings.displayIcon,
                    contentDescription = null,
                    tint = if (!focusOnContent && focusedItem == DrawerItem.Settings) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface
                )
            },
            label = {
                Text(
                    modifier = Modifier.offset(y = (-4).dp),
                    text = DrawerItem.Settings.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

enum class DrawerItem(
    val displayName: String,
    val displayIcon: ImageVector
) {
    User(displayName = "点击登录", displayIcon = Icons.Default.AccountCircle),
    Search(displayName = "搜索", displayIcon = Icons.Default.Search),
    Home(displayName = "首页", displayIcon = Icons.Default.Home),
    UGC(displayName = "UGC", displayIcon = Icons.Default.OndemandVideo),
    PGC(displayName = "PGC", displayIcon = Icons.Default.Movie),
    Settings(displayName = "设置", displayIcon = Icons.Default.Settings), ;
}

@Preview(device = "id:tv_1080p")
@Composable
private fun DrawerContentPreview() {
    BVTheme {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(180.dp)
        ) {
            NavigationRail(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(72.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                DrawerContent()
            }
        }
    }
}
