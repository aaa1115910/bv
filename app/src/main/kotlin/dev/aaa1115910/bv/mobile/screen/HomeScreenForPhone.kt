package dev.aaa1115910.bv.mobile.screen

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.origeek.imageViewer.previewer.ImagePreviewerState
import dev.aaa1115910.bv.mobile.activities.LoginActivity
import dev.aaa1115910.bv.mobile.screen.home.DynamicScreen
import dev.aaa1115910.bv.mobile.screen.home.HomeScreen
import dev.aaa1115910.bv.mobile.screen.home.UserSwitchDialog
import dev.aaa1115910.bv.screen.user.UserSwitchViewModel
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreenForPhone(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    homeViewModel: PopularViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    userSwitchViewModel: UserSwitchViewModel = koinViewModel(),
    currentScreen: MobileMainScreenNav,
    onCurrentScreenChange: (MobileMainScreenNav) -> Unit,
    windowSize: WindowWidthSizeClass,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<String>, afterSetPictures: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val lazyGridState = rememberLazyGridState()
    var activeSearch by remember { mutableStateOf(false) }
    var showUserSwitchDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) { homeViewModel.loadMore() }
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

    Box {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                if (windowSize == WindowWidthSizeClass.Expanded) return@Scaffold

                Column {
                    AnimatedVisibility(visible = !activeSearch) {
                        NavigationBar {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        MobileMainScreenNav.Home.icon,
                                        contentDescription = MobileMainScreenNav.Home.displayName
                                    )
                                },
                                label = { Text(text = MobileMainScreenNav.Home.displayName) },
                                selected = currentScreen == MobileMainScreenNav.Home,
                                onClick = { onCurrentScreenChange(MobileMainScreenNav.Home) }
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        MobileMainScreenNav.Dynamic.icon,
                                        contentDescription = MobileMainScreenNav.Dynamic.displayName
                                    )
                                },
                                label = { Text(text = MobileMainScreenNav.Dynamic.displayName) },
                                selected = currentScreen == MobileMainScreenNav.Dynamic,
                                onClick = { onCurrentScreenChange(MobileMainScreenNav.Dynamic) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                MobileMainScreenNav.Home -> {
                    HomeScreen(
                        drawerState = drawerState,
                        gridState = lazyGridState,
                        windowSize = windowSize,
                        onSearchActiveChange = { activeSearch = it },
                        onShowSwitchUser = {
                            scope.launch(Dispatchers.IO) {
                                userSwitchViewModel.updateUserDbList()
                                showUserSwitchDialog = true
                            }
                        }
                    )
                }

                MobileMainScreenNav.Dynamic -> {
                    DynamicScreen(
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer
                    )
                }

                else -> {}
            }
        }
        UserSwitchDialog(
            show = showUserSwitchDialog,
            onHideDialog = { showUserSwitchDialog = false },
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
}

