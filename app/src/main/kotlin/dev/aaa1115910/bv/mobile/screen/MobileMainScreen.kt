package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.aaa1115910.bv.mobile.activities.LoginActivity
import dev.aaa1115910.bv.mobile.activities.SettingsActivity
import dev.aaa1115910.bv.mobile.screen.home.DynamicScreen
import dev.aaa1115910.bv.mobile.screen.home.FollowingUserScreen
import dev.aaa1115910.bv.mobile.screen.home.HomeScreen
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    val navController = rememberNavController()

    var searchText by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }

    var currentScreen by remember { mutableStateOf(MobileMainScreenNav.Home) }

    val openNavDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    val goHome = {
        navController.navigate("home") {
            popUpTo("home")
            launchSingleTop = true
        }
        currentScreen = MobileMainScreenNav.Home
    }

    val goHistory = {
        navController.navigate("history") {
            popUpTo("home")
        }
        currentScreen = MobileMainScreenNav.History
    }

    val goFavorite = {
        navController.navigate("favorite") {
            popUpTo("home")
        }
        currentScreen = MobileMainScreenNav.Favorite
    }

    val goSettings = {
        /*navController.navigate("setting") {
            popUpTo("home")
        }
        currentScreen = MobileMainScreenNav.Setting*/
        context.startActivity(Intent(context, SettingsActivity::class.java))
    }

    val goMyFollowingUser = {
        navController.navigate("followingUser") {
            popUpTo("home")
        }
        currentScreen = MobileMainScreenNav.FollowingUser
    }

    val goDynamic = {
        navController.navigate("dynamic") {
            popUpTo("dynamic")
        }
        currentScreen = MobileMainScreenNav.Dynamic
    }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) { homeViewModel.loadMore() }
    }

    LaunchedEffect(Unit) {
        userViewModel.updateUserInfo()

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            logger.info { "current route: ${destination.route}" }
            if (destination.route == "home") {
                currentScreen = MobileMainScreenNav.Home
            }
        }
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
                onGoHome = goHome,
                onGoHistory = goHistory,
                onGoFavorite = goFavorite,
                onGoSetting = goSettings,
                onGoMyFollowingUser = goMyFollowingUser,
            )
        }
    ) {
        Row {
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                MainNavRail(
                    currentScreen = currentScreen,
                    onCurrentScreenChange = {
                        when (it) {
                            MobileMainScreenNav.Home -> goHome()
                            MobileMainScreenNav.Search -> goHistory()
                            MobileMainScreenNav.Dynamic -> goDynamic()
                            MobileMainScreenNav.Setting -> goSettings()
                            MobileMainScreenNav.History -> goHistory()
                            MobileMainScreenNav.Favorite -> goFavorite()
                            MobileMainScreenNav.FollowingUser -> goMyFollowingUser()
                        }
                    },
                )
            }

            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
                        HomeScreenForPhone(
                            drawerState = drawerState,
                            currentScreen = currentScreen,
                            onCurrentScreenChange = { currentScreen = it },
                            windowSize = windowSizeClass.widthSizeClass,
                        )
                    } else {
                        HomeScreen(
                            drawerState = drawerState,
                            gridState = lazyGridState,
                            windowSize = windowSizeClass.widthSizeClass,
                            onSearchActiveChange = { activeSearch = it },
                            onShowSwitchUser = {}
                        )
                    }
                }
                composable("dynamic") {
                    DynamicScreen()
                }
                composable("history") {
                    Text(text = "History")
                }
                composable("search") {
                    Text(text = "Search")
                }
                composable("setting") {
                    Text(text = "Setting")
                }
                composable("favorite") {
                    Text(text = "Favorite")
                }
                composable("followingUser") {
                    FollowingUserScreen(
                        onBack = goHome
                    )
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