package dev.aaa1115910.bv.screen.main

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerScope
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import coil.compose.AsyncImage
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.isDpadRight
import dev.aaa1115910.bv.util.isKeyDown

@Composable
fun NavigationDrawerScope.DrawerContent(
    modifier: Modifier = Modifier,
    isLogin: Boolean = false,
    avatar: String = "",
    username: String = "",
    onDrawerItemChanged: (DrawerItem) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onShowUserPanel: () -> Unit = {},
    onFocusToContent: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(DrawerItem.Home) }
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    LaunchedEffect(selectedItem) {
        onDrawerItemChanged(selectedItem)
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(12.dp)
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.isDpadRight()) {
                    if (keyEvent.isKeyDown()) {
                        onFocusToContent()
                        return@onPreviewKeyEvent true
                    }
                }
                false
            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        NavigationDrawerItem(
            modifier = Modifier,
            onClick = {
                if (isLogin) {
                    onShowUserPanel()
                } else {
                    onLogin()
                }
            },
            selected = selectedItem == DrawerItem.User,
            leadingContent = {
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
                        contentDescription = null
                    )
                }
            }
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = if (isLogin) username
                else DrawerItem.User.displayName,
                maxLines = 1
            )
        }
        LazyColumn(
            modifier = Modifier
                .then(focusRestorerModifiers.parentModifier),
            verticalArrangement = Arrangement.Center
        ) {
            listOf(
                DrawerItem.Search,
                DrawerItem.Home,
                DrawerItem.UGC,
                DrawerItem.PGC,
            ).forEach { item ->
                item {
                    NavigationDrawerItem(
                        modifier = Modifier
                            .onFocusChanged { if (it.hasFocus) selectedItem = item }
                            .ifElse(
                                item == DrawerItem.Home,
                                focusRestorerModifiers.childModifier
                            ),
                        onClick = { selectedItem = item },
                        selected = selectedItem == item,
                        leadingContent = {
                            Icon(
                                imageVector = item.displayIcon,
                                contentDescription = null
                            )
                        }
                    ) {
                        Text(text = item.displayName)
                    }
                }
            }
        }
        NavigationDrawerItem(
            modifier = Modifier,
            onClick = onOpenSettings,
            selected = false,
            leadingContent = {
                Icon(
                    imageVector = DrawerItem.Settings.displayIcon,
                    contentDescription = null
                )
            }
        ) {
            Text(text = DrawerItem.Settings.displayName)
        }
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
private fun DrawerContentClosedPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    BVTheme {
        NavigationDrawer(
            drawerContent = {
                DrawerContent()
            },
            drawerState = drawerState
        ) { }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun DrawerContentOpenPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    BVTheme {
        NavigationDrawer(
            drawerContent = {
                DrawerContent()
            },
            drawerState = drawerState
        ) { }
    }
}