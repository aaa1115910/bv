package dev.aaa1115910.bv.mobile.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Segment
import androidx.compose.material.icons.rounded.FiberNew
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.origeek.imageViewer.previewer.ImagePreviewer
import com.origeek.imageViewer.previewer.VerticalDragType
import com.origeek.imageViewer.previewer.rememberPreviewerState
import dev.aaa1115910.biliapi.entity.Picture
import dev.aaa1115910.bv.component.DevelopingTipContent
import dev.aaa1115910.bv.mobile.activities.FavoriteActivity
import dev.aaa1115910.bv.mobile.activities.FollowingUserActivity
import dev.aaa1115910.bv.mobile.activities.HistoryActivity
import dev.aaa1115910.bv.mobile.activities.LoginActivity
import dev.aaa1115910.bv.mobile.activities.SettingsActivity
import dev.aaa1115910.bv.mobile.component.home.UserDialog
import dev.aaa1115910.bv.mobile.screen.home.DynamicScreen
import dev.aaa1115910.bv.mobile.screen.home.HomeScreen
import dev.aaa1115910.bv.screen.user.UserSwitchViewModel
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MobileMainScreen(
    modifier: Modifier = Modifier,
    popularViewModel: PopularViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    userSwitchViewModel: UserSwitchViewModel = koinViewModel()
) {
    val logger = KotlinLogging.logger("MobileMainScreen")
    val state = rememberMobileMainScreenState(
        popularViewModel = popularViewModel,
        userViewModel = userViewModel,
        userSwitchViewModel = userSwitchViewModel
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val navSuiteType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    val pictures = remember { mutableStateListOf<Picture>() }
    val previewerState = rememberPreviewerState(
        verticalDragType = VerticalDragType.UpAndDown,
        pageCount = { pictures.size },
        getKey = { pictures[it].key }
    )

    val onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit =
        { newPictures, afterSetPictures ->
            pictures.swapList(newPictures)
            logger.fInfo { "update image previewer pictures list: $newPictures" }
            afterSetPictures()
        }

    val verticalNavOrder = listOf(
        MobileMainScreenNav.Search, MobileMainScreenNav.Home,
        MobileMainScreenNav.Zone, MobileMainScreenNav.Dynamic
    ).map { it.name }
    val horizontalNavOrder = listOf(
        MobileMainScreenNav.Home, MobileMainScreenNav.Zone,
        MobileMainScreenNav.Search, MobileMainScreenNav.Dynamic,
    ).map { it.name }

    val compareNavIndex: (String?, String?) -> Boolean = { a, b ->
        if (navSuiteType == NavigationSuiteType.NavigationBar) {
            horizontalNavOrder.indexOf(a) < horizontalNavOrder.indexOf(b)
        } else {
            verticalNavOrder.indexOf(a) < verticalNavOrder.indexOf(b)
        }
    }

    val navEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            val coefficient = 10
            if (navSuiteType == NavigationSuiteType.NavigationBar) {
                if (compareNavIndex(
                        targetState.destination.route,
                        initialState.destination.route
                    )
                ) {
                    fadeIn() + slideInHorizontally { -it / coefficient }
                } else {
                    fadeIn() + slideInHorizontally { it / coefficient }
                }
            } else {
                if (compareNavIndex(
                        targetState.destination.route,
                        initialState.destination.route
                    )
                ) {
                    fadeIn() + slideInVertically { -it / coefficient }
                } else {
                    fadeIn() + slideInVertically { it / coefficient }
                }
            }
        }

    val navExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            val coefficient = 10
            if (navSuiteType == NavigationSuiteType.NavigationBar) {
                if (compareNavIndex(
                        targetState.destination.route,
                        initialState.destination.route
                    )
                ) {
                    fadeOut() + slideOutHorizontally { it / coefficient }
                } else {
                    fadeOut() + slideOutHorizontally { -it / coefficient }
                }
            } else {
                if (compareNavIndex(
                        targetState.destination.route,
                        initialState.destination.route
                    )
                ) {
                    fadeOut() + slideOutVertically { it / coefficient }
                } else {
                    fadeOut() + slideOutVertically { -it / coefficient }
                }
            }
        }

    BackHandler(previewerState.canClose || previewerState.animating) {
        if (previewerState.canClose) scope.launch {
            previewerState.closeTransform()
        }
    }

    BackHandler(state.showUserDialog) {
        state.hideUserDialog()
    }

    Box(
        modifier = modifier,
    ) {
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                NavigationSuit(
                    mobileMainScreenState = state,
                    navigationSuiteType = navSuiteType,
                    avatar = userViewModel.face,
                    onNavigate = state::navigate,
                    onShowUserDialog = state::showUserDialog
                )
            }
        ) {
            NavHost(
                navController = state.navController,
                startDestination = MobileMainScreenNav.Home.name,
                enterTransition = navEnterTransition,
                exitTransition = navExitTransition
            ) {
                composable(MobileMainScreenNav.Home.name) {
                    HomeScreen(
                        rcmdGridState = state.rcmdGridState,
                        popularGridState = state.popularGridState,
                        windowSize = state.windowSizeClass.widthSizeClass,
                        onShowUserDialog = state::showUserDialog
                    )
                }

                composable(MobileMainScreenNav.Dynamic.name) {
                    BackHandler(previewerState.canClose || previewerState.animating) {
                        if (previewerState.canClose) scope.launch {
                            previewerState.closeTransform()
                        }
                    }

                    DynamicScreen(
                        dynamicGridState = state.dynamicGridState,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        // dynamicViewModel = dynamicViewModel
                    )
                }

                composable(MobileMainScreenNav.Search.name) {
                    DevelopingTipContent()
                }
                composable(MobileMainScreenNav.Zone.name) {
                    DevelopingTipContent()
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
                .data(pictures[index].url)
                .size(Size.ORIGINAL)
                .build()
            rememberAsyncImagePainter(imageRequest)
        }
    )

    UserDialog(
        show = state.showUserDialog,
        windowWidthSizeClass = windowSizeClass.widthSizeClass,
        onHideDialog = { state.showUserDialog = false },
        currentUser = userSwitchViewModel.currentUser.takeIf { it.id != -1 },
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
        },
        onOpenFollowingUser = {
            context.startActivity(
                Intent(context, FollowingUserActivity::class.java)
            )
        },
        onOpenHistory = {
            context.startActivity(
                Intent(context, HistoryActivity::class.java)
            )
        },
        onOpenFollowingPgc = {},
        onOpenFavorite = {
            context.startActivity(
                Intent(context, FavoriteActivity::class.java)
            )
        },
        onOpenToView = {},
        onOpenSettings = { context.startActivity(Intent(context, SettingsActivity::class.java)) }
    )
}

@Composable
private fun NavigationSuit(
    modifier: Modifier = Modifier,
    mobileMainScreenState: MobileMainScreenState,
    navigationSuiteType: NavigationSuiteType,
    avatar: String,
    onNavigate: (MobileMainScreenNav) -> Unit,
    onShowUserDialog: () -> Unit,
) {
    when (navigationSuiteType) {
        NavigationSuiteType.NavigationBar -> {
            NavigationSuite(
                modifier = modifier
            ) {
                listOf(
                    MobileMainScreenNav.Home,
                    MobileMainScreenNav.Zone,
                    MobileMainScreenNav.Search,
                    MobileMainScreenNav.Dynamic,
                ).forEach { navItem ->
                    item(
                        icon = { Icon(navItem.icon, contentDescription = navItem.displayName) },
                        label = { Text(navItem.displayName) },
                        selected = mobileMainScreenState.currentNavItem == navItem,
                        onClick = { onNavigate(navItem) }
                    )
                }
            }
        }

        NavigationSuiteType.NavigationRail -> {
            NavigationRail(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                NavigationRailItem(
                    icon = {
                        if (avatar.isBlank()) {
                            Icon(Icons.Rounded.Person, contentDescription = "User Avatar")
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(36.dp),
                                    model = avatar,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    },
                    selected = false,
                    onClick = onShowUserDialog
                )
                Spacer(Modifier.weight(1f))
                listOf(
                    MobileMainScreenNav.Search,
                    MobileMainScreenNav.Home,
                    MobileMainScreenNav.Zone,
                    MobileMainScreenNav.Dynamic,
                ).forEach { navItem ->
                    NavigationRailItem(
                        icon = { Icon(navItem.icon, contentDescription = navItem.displayName) },
                        label = { Text(navItem.displayName) },
                        selected = mobileMainScreenState.currentNavItem == navItem,
                        onClick = { onNavigate(navItem) }
                    )
                }
                Spacer(Modifier.weight(1f))
                listOf(MobileMainScreenNav.Setting).forEach { navItem ->
                    NavigationRailItem(
                        icon = { Icon(navItem.icon, contentDescription = navItem.displayName) },
                        label = { Text(navItem.displayName) },
                        selected = mobileMainScreenState.currentNavItem == navItem,
                        onClick = { onNavigate(navItem) }
                    )
                }
            }
        }
    }
}

data class MobileMainScreenState(
    val context: Context,
    val scope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    //val drawerState: DrawerState,
    val rcmdGridState: LazyGridState,
    val popularGridState: LazyGridState,
    val dynamicGridState: LazyStaggeredGridState,
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

    var showUserDialog by mutableStateOf(false)

    fun navigate(navItem: MobileMainScreenNav) {
        logger.fInfo { "Navigate to ${navItem.name}" }

        val navigateToRoute: () -> Unit = {
            val route = navItem.name
            navController.navigate(route) {
                launchSingleTop = true
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = false
                    saveState = true
                }
                restoreState = true
            }
        }

        val notCurrentNavItem = currentNavItem != navItem

        when (navItem) {
            MobileMainScreenNav.Home -> {
                if (notCurrentNavItem) {
                    navigateToRoute()
                } else {
                    scope.launch { rcmdGridState.animateScrollToItem(0) }
                    scope.launch { popularGridState.animateScrollToItem(0) }
                }
            }

            MobileMainScreenNav.Search -> {
                if (notCurrentNavItem) {
                    navigateToRoute()
                }
            }

            MobileMainScreenNav.Setting -> {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }

            MobileMainScreenNav.Dynamic -> {
                if (notCurrentNavItem) {
                    navigateToRoute()
                } else {
                    scope.launch { dynamicGridState.animateScrollToItem(0) }
                }
            }

            MobileMainScreenNav.Zone -> {
                if (notCurrentNavItem) {
                    navigateToRoute()
                }
            }
        }

        @SuppressLint("RestrictedApi")
        val breadcrumb = navController
            .currentBackStack
            .value
            .map { it.destination }
            .filterNot { it is NavGraph }
            .joinToString(" > ") { it.route ?: "null" }
        logger.fInfo { "Navigation Stack: > $breadcrumb" }
    }

    fun showUserDialog() {
        scope.launch(Dispatchers.IO) {
            userSwitchViewModel.updateUserDbList()
            this@MobileMainScreenState.showUserDialog = true
        }
    }

    fun hideUserDialog() {
        this@MobileMainScreenState.showUserDialog = false
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberMobileMainScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(context as Activity),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    rcmdGridState: LazyGridState = rememberLazyGridState(),
    popularGridState: LazyGridState = rememberLazyGridState(),
    dynamicGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    navController: NavHostController = rememberNavController(),
    popularViewModel: PopularViewModel,//= koinNavViewModel(),
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
        if (popularViewModel.popularVideoList.isNotEmpty()) {
            scope.launch(Dispatchers.IO) { popularViewModel.loadMore() }
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
        rcmdGridState,
        popularGridState,
        dynamicGridState,
        navController,
        currentNavItem
    ) {
        MobileMainScreenState(
            context,
            scope,
            windowSizeClass,
            //drawerState,
            rcmdGridState,
            popularGridState,
            dynamicGridState,
            navController,
            currentBackStackEntry,
            currentNavItem,
            popularViewModel,
            userViewModel,
            userSwitchViewModel
        )
    }
}

enum class MobileMainScreenNav(val displayName: String, val icon: ImageVector) {
    Home("首页", Icons.Rounded.Home),
    Zone("分区", Icons.AutoMirrored.Rounded.Segment),
    Search("搜索", Icons.Rounded.Search),
    Dynamic("动态", Icons.Rounded.FiberNew),
    Setting("设置", Icons.Rounded.Settings), ;

    companion object {
        fun fromName(name: String) = entries.firstOrNull { it.name == name } ?: Home
    }
}