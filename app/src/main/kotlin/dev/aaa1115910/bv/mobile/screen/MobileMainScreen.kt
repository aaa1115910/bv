package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FiberNew
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.origeek.imageViewer.previewer.ImagePreviewer
import com.origeek.imageViewer.previewer.VerticalDragType
import com.origeek.imageViewer.previewer.rememberPreviewerState
import dev.aaa1115910.bv.mobile.activities.LoginActivity
import dev.aaa1115910.bv.mobile.activities.SettingsActivity
import dev.aaa1115910.bv.mobile.screen.home.DynamicScreen
import dev.aaa1115910.bv.mobile.screen.home.FollowingUserScreen
import dev.aaa1115910.bv.mobile.screen.home.HomeScreen
import dev.aaa1115910.bv.mobile.screen.home.UserSwitchDialog
import dev.aaa1115910.bv.screen.user.UserSwitchViewModel
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun MobileMainScreen(
    modifier: Modifier = Modifier,
    homeViewModel: PopularViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    userSwitchViewModel: UserSwitchViewModel = koinViewModel()
) {
    val state = rememberMobileMainScreenState(
        homeViewModel = homeViewModel,
        userViewModel = userViewModel,
        userSwitchViewModel = userSwitchViewModel
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val navSuiteType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    val navigationItems = when (navSuiteType) {
        NavigationSuiteType.NavigationBar -> listOf(
            MobileMainScreenNav.Home,
            MobileMainScreenNav.Dynamic
        )

        NavigationSuiteType.NavigationRail -> MobileMainScreenNav.entries
        else -> MobileMainScreenNav.entries
    }

    val pictures = remember { mutableStateListOf<String>() }
    val previewerState = rememberPreviewerState(
        verticalDragType = VerticalDragType.UpAndDown,
        pageCount = { pictures.size },
        getKey = { pictures[it] }
    )

    val onShowPreviewer: (newPictures: List<String>, afterSetPictures: () -> Unit) -> Unit =
        { newPictures, afterSetPictures ->
            pictures.swapList(newPictures)
            afterSetPictures()
        }

    BackHandler(previewerState.canClose || previewerState.animating) {
        if (previewerState.canClose) scope.launch {
            previewerState.closeTransform()
        }
    }

    BackHandler(state.showUserSwitchDialog) {
        state.hideUserSwitch()
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = state.drawerState,
        gesturesEnabled = !state.activeSearch,
        drawerContent = {
            ModalNavDrawerContent(
                avatar = userViewModel.face,
                username = userViewModel.username,
                isLogin = Prefs.isLogin,
                onCloseDrawer = { scope.launch { state.drawerState.close() } },
                onLogin = { context.startActivity(Intent(context, LoginActivity::class.java)) },
                onLogout = { },
                onGoHome = { state.navigate(MobileMainScreenNav.Home) },
                onGoHistory = { state.navigate(MobileMainScreenNav.History) },
                onGoFavorite = { state.navigate(MobileMainScreenNav.Favorite) },
                onGoSetting = { state.navigate(MobileMainScreenNav.Setting) },
                onGoMyFollowingUser = { state.navigate(MobileMainScreenNav.FollowingUser) },
            )
        }
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                navigationItems.forEach { navItem ->
                    item(
                        icon = { Icon(navItem.icon, contentDescription = navItem.displayName) },
                        label = { Text(navItem.displayName) },
                        selected = state.currentNavItem == navItem,
                        onClick = { state.navigate(navItem) }
                    )
                }
            }
        ) {
            NavHost(
                navController = state.navController,
                startDestination = MobileMainScreenNav.Home.name
            ) {
                composable(MobileMainScreenNav.Home.name) {
                    HomeScreen(
                        drawerState = state.drawerState,
                        gridState = state.lazyGridState,
                        windowSize = state.windowSizeClass.widthSizeClass,
                        onSearchActiveChange = { state.activeSearch = it },
                        onShowSwitchUser = state::showUserSwitch
                    )
                }

                composable(MobileMainScreenNav.Dynamic.name) {
                    BackHandler(previewerState.canClose || previewerState.animating) {
                        if (previewerState.canClose) scope.launch {
                            previewerState.closeTransform()
                        }
                    }

                    DynamicScreen(
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer
                    )
                }

                composable(MobileMainScreenNav.History.name) {
                    Text(text = "History")
                }
                composable(MobileMainScreenNav.Search.name) {
                    Text(text = "Search")
                }
                composable(MobileMainScreenNav.Setting.name) {
                    Text(text = "Setting")
                }
                composable(MobileMainScreenNav.Favorite.name) {
                    Text(text = "Favorite")
                }
                composable(MobileMainScreenNav.FollowingUser.name) {
                    FollowingUserScreen(
                        onBack = {}
                    )
                }
            }
        }
    }

    ImagePreviewer(
        modifier = Modifier
            .fillMaxSize(),
        state = previewerState,
        imageLoader = { index ->
            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(pictures[index])
                .size(Size.ORIGINAL)
                .build()
            rememberAsyncImagePainter(imageRequest)
        }
    )

    UserSwitchDialog(
        show = state.showUserSwitchDialog,
        onHideDialog = { state.showUserSwitchDialog = false },
        currentUser = userSwitchViewModel.currentUser,
        userList = userSwitchViewModel.userDbList,
        onSwitchUser = { user ->
            scope.launch(Dispatchers.IO) {
                userSwitchViewModel.switchUser(user)
            }
        },
        onAddUser = { context.startActivity(Intent(context, LoginActivity::class.java)) },
        onDeleteUser = { user ->
            scope.launch(Dispatchers.IO) {
                userSwitchViewModel.deleteUser(user)
            }
        }
    )
}

data class MobileMainScreenState(
    val context: Context,
    val scope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    val drawerState: DrawerState,
    val lazyGridState: LazyGridState,
    val navController: NavHostController,
    val currentBackStackEntry: NavBackStackEntry?,
    val currentNavItem: MobileMainScreenNav,
    private val homeViewModel: PopularViewModel,
    private val userViewModel: UserViewModel,
    private val userSwitchViewModel: UserSwitchViewModel,
) {
    companion object {
        val logger = KotlinLogging.logger {}
    }

    var activeSearch by mutableStateOf(false)

    var showUserSwitchDialog by mutableStateOf(false)

    fun navigate(navItem: MobileMainScreenNav) {
        logger.info { "Navigate to ${navItem.name}" }
        when (navItem) {
            MobileMainScreenNav.Home -> {
                navController.navigate(navItem.name) {
                    popUpTo(navItem.name)
                    launchSingleTop = true
                }
            }

            MobileMainScreenNav.Search -> {
                navController.navigate(navItem.name) {
                    popUpTo(MobileMainScreenNav.Home.name)
                }
            }

            MobileMainScreenNav.Favorite -> {
                navController.navigate(navItem.name) {
                    popUpTo(MobileMainScreenNav.Home.name)
                }
            }

            MobileMainScreenNav.Setting -> {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }

            MobileMainScreenNav.FollowingUser -> {
                navController.navigate(navItem.name) {
                    popUpTo(MobileMainScreenNav.Home.name)
                }
            }

            MobileMainScreenNav.Dynamic -> {
                navController.navigate(navItem.name) {
                    popUpTo(MobileMainScreenNav.Home.name)
                }
            }

            MobileMainScreenNav.History -> {
                navController.navigate(navItem.name) {
                    popUpTo(MobileMainScreenNav.Home.name)
                }
            }
        }
    }

    fun showUserSwitch() {
        scope.launch(Dispatchers.IO) {
            userSwitchViewModel.updateUserDbList()
            showUserSwitchDialog = true
        }
    }

    fun hideUserSwitch() {
        showUserSwitchDialog = false
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberMobileMainScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(context as Activity),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    navController: NavHostController = rememberNavController(),
    homeViewModel: PopularViewModel,//= koinNavViewModel(),
    userViewModel: UserViewModel,//= koinNavViewModel(),
    userSwitchViewModel: UserSwitchViewModel //= koinNavViewModel()
): MobileMainScreenState {
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentNavItem by remember {
        derivedStateOf {
            MobileMainScreenNav.fromName(currentBackStackEntry?.destination?.route ?: "")
        }
    }

    LaunchedEffect(Unit) {
        if (homeViewModel.popularVideoList.isNotEmpty()) {
            scope.launch(Dispatchers.IO) { homeViewModel.loadMore() }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.updateUserInfo()
    }

    DisposableEffect(lifecycleOwner) {
        var leaveFromThisPage = false
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                leaveFromThisPage = true
            } else if (event == Lifecycle.Event.ON_RESUME) {
                if (leaveFromThisPage) {
                    scope.launch(Dispatchers.IO) {
                        userSwitchViewModel.updateUserDbList()
                    }
                }
                leaveFromThisPage = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return remember(
        context,
        scope,
        windowSizeClass,
        drawerState,
        lazyGridState,
        navController,
        currentNavItem
    ) {
        MobileMainScreenState(
            context,
            scope,
            windowSizeClass,
            drawerState,
            lazyGridState,
            navController,
            currentBackStackEntry,
            currentNavItem,
            homeViewModel,
            userViewModel,
            userSwitchViewModel
        )
    }
}

enum class MobileMainScreenNav(val displayName: String, val icon: ImageVector) {
    Home("首页", Icons.Rounded.Home),
    Search("搜索", Icons.Rounded.Search),
    Dynamic("动态", Icons.Rounded.FiberNew),
    Setting("设置", Icons.Rounded.Settings),
    History("历史记录", Icons.Rounded.History),
    Favorite("私人藏品", Icons.Rounded.FavoriteBorder),
    FollowingUser("我推的UP", Icons.Rounded.People);

    companion object {
        fun fromName(name: String) = entries.firstOrNull { it.name == name } ?: Home
    }
}