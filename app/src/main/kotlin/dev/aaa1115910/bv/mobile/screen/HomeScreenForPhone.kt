package dev.aaa1115910.bv.mobile.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.aaa1115910.bv.mobile.screen.home.DynamicScreen
import dev.aaa1115910.bv.mobile.screen.home.HomeScreen
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreenForPhone(
    modifier: Modifier = Modifier,
    homeViewModel: PopularViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    currentScreen: MobileMainScreenNav,
    onCurrentScreenChange: (MobileMainScreenNav) -> Unit,
    windowSize: WindowWidthSizeClass
) {
    val scope = rememberCoroutineScope()

    val lazyGridState = rememberLazyGridState()
    var activeSearch by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) { homeViewModel.loadMore() }
    }

    LaunchedEffect(Unit) {
        userViewModel.updateUserInfo()
    }

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
        when (currentScreen) {
            MobileMainScreenNav.Home -> {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    gridState = lazyGridState,
                    windowSize = windowSize,
                    onSearchActiveChange = { activeSearch = it }
                )
            }

            MobileMainScreenNav.Dynamic -> {
                DynamicScreen(
                    modifier = Modifier.padding(innerPadding),
                )
            }

            else -> {}
        }

    }
}

