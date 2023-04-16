package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FiberNew
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import dev.aaa1115910.bv.activities.user.LoginActivity
import dev.aaa1115910.bv.mobile.screen.home.DynamicScreen
import dev.aaa1115910.bv.mobile.screen.home.HomeScreen
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MobileMainScreen(
    modifier: Modifier = Modifier,
    homeViewModel: PopularViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }
    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val lazyGridState = rememberLazyGridState()

    var searchText by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }

    var currentScreen by remember { mutableStateOf(MobileMainScreenNav.Home) }

    val openNavDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) { homeViewModel.loadMore() }
    }

    LaunchedEffect(Unit) {
        userViewModel.updateUserInfo()
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = !activeSearch,
        drawerContent = {
            ModalNavDrawerContent(
                avatar = userViewModel.face,
                username = userViewModel.username,
                isLogin = Prefs.isLogin,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onLogin = { context.startActivity(Intent(context, LoginActivity::class.java)) },
                onLogout = { },
                onGoHome = { currentScreen = MobileMainScreenNav.Home },
                onGoHistory = { currentScreen = MobileMainScreenNav.History },
                onGoFavorite = { currentScreen = MobileMainScreenNav.Favorite },
                onGoSetting = { currentScreen = MobileMainScreenNav.Setting },
                onGoMyFollowingUser = { currentScreen = MobileMainScreenNav.FollowingUser },
            )
        }
    ) {
        Row {
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                MainNavRail(
                    currentScreen = currentScreen,
                    onCurrentScreenChange = { currentScreen = it },
                )
            }

            when (currentScreen) {
                MobileMainScreenNav.Home, MobileMainScreenNav.Dynamic -> {
                    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
                        HomeScreenForPhone(
                            currentScreen = currentScreen,
                            onCurrentScreenChange = { currentScreen = it },
                            windowSize = windowSizeClass.widthSizeClass,
                        )
                    } else {
                        when (currentScreen) {
                            MobileMainScreenNav.Home -> {
                                HomeScreen(
                                    gridState = lazyGridState,
                                    windowSize = windowSizeClass.widthSizeClass,
                                    onSearchActiveChange = { activeSearch = it }
                                )
                            }

                            MobileMainScreenNav.Dynamic -> {
                                DynamicScreen()
                            }

                            else -> {}
                        }
                    }
                }

                MobileMainScreenNav.History -> {
                    Text(text = "History")
                }

                MobileMainScreenNav.Search -> {
                    Text(text = "Search")
                }

                MobileMainScreenNav.Setting -> {
                    Text(text = "Setting")
                }

                MobileMainScreenNav.Favorite -> {
                    Text(text = "Favorite")
                }

                MobileMainScreenNav.FollowingUser -> {
                    Text(text = "FollowingUser")
                }
            }
        }
    }
}

@Composable
private fun MainNavRail(
    modifier: Modifier = Modifier,
    currentScreen: MobileMainScreenNav,
    onCurrentScreenChange: (MobileMainScreenNav) -> Unit
) {
    NavigationRail(
        modifier = modifier
    ) {
        MobileMainScreenNav.values().forEach { nav ->
            NavigationRailItem(
                icon = { Icon(imageVector = nav.icon, contentDescription = nav.displayName) },
                label = { Text(text = nav.displayName) },
                selected = currentScreen == nav,
                onClick = { onCurrentScreenChange(nav) }
            )
        }
    }
}


enum class MobileMainScreenNav(val displayName: String, val icon: ImageVector) {
    Home("首页", Icons.Rounded.Home),
    Search("搜索", Icons.Rounded.Search),
    Dynamic("动态", Icons.Rounded.FiberNew),
    Setting("设置", Icons.Rounded.Settings),
    History("历史记录", Icons.Rounded.History),
    Favorite("私人藏品", Icons.Rounded.FavoriteBorder),
    FollowingUser("我推的UP", Icons.Rounded.People)
}