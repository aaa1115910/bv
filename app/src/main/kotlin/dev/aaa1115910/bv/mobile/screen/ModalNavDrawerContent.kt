package dev.aaa1115910.bv.mobile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

@Composable
fun ModalNavDrawerContent(
    modifier: Modifier = Modifier,
    avatar: String,
    username: String,
    isLogin: Boolean,
    onCloseDrawer: () -> Unit,
    onLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onGoMyFollowingUser: () -> Unit = {},
    onGoHistory: () -> Unit = {},
    onGoFavorite: () -> Unit = {},
    onGoSetting: () -> Unit = {},
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        Spacer(Modifier.height(12.dp))
        UserPanel(
            avatar = avatar,
            username = username,
            isLogin = isLogin,
            onLogin = onLogin,
            onLogout = onLogout
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text(text = MobileMainScreenNav.Home.displayName) },
            icon = { Icon(imageVector = MobileMainScreenNav.Home.icon, contentDescription = null) },
            selected = false,
            onClick = {
                onCloseDrawer()
                onGoHome()
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text(text = MobileMainScreenNav.FollowingUser.displayName) },
            icon = {
                Icon(
                    imageVector = MobileMainScreenNav.FollowingUser.icon,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = {
                onCloseDrawer()
                onGoMyFollowingUser()
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text(text = MobileMainScreenNav.History.displayName) },
            icon = {
                Icon(
                    imageVector = MobileMainScreenNav.History.icon,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = {
                onCloseDrawer()
                onGoHistory()
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text(text = MobileMainScreenNav.Favorite.displayName) },
            icon = {
                Icon(
                    imageVector = MobileMainScreenNav.Favorite.icon,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = {
                onCloseDrawer()
                onGoFavorite()
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text(text = "我追的番") },
            icon = { Icon(imageVector = Icons.Default.History, contentDescription = null) },
            selected = false,
            onClick = {
                onCloseDrawer()
                //TODO navigate to item
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text(text = "现在不看") },
            icon = { Icon(imageVector = Icons.Default.History, contentDescription = null) },
            selected = false,
            onClick = {
                onCloseDrawer()
                //TODO navigate to item
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text(text = MobileMainScreenNav.Setting.displayName) },
            icon = {
                Icon(
                    imageVector = MobileMainScreenNav.Setting.icon,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = {
                onCloseDrawer()
                onGoSetting()
            }
        )
    }
}

@Composable
private fun UserPanel(
    modifier: Modifier = Modifier,
    avatar: String,
    username: String,
    isLogin: Boolean,
    onLogin: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.Gray)
                    .clickable { if (isLogin) onLogout() else onLogin() }
            ) {
                if (isLogin) {
                    AsyncImage(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        model = avatar,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
            if (isLogin) {
                Text(text = username)
            } else {
                Text(text = "未登录")
            }
        }
    }
}

@Preview
@Composable
private fun ModalNavDrawerContentPreview() {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val navController = rememberNavController()
    BVMobileTheme {
        Surface {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalNavDrawerContent(
                        avatar = "",
                        username = "老师好我叫何同学",
                        isLogin = true,
                        onCloseDrawer = {}
                    )
                }
            ) {}
        }
    }
}

@Preview
@Composable
private fun UserPanelPreview() {
    BVMobileTheme {
        UserPanel(
            avatar = "",
            username = "老师好我叫何同学",
            isLogin = true
        )
    }
}