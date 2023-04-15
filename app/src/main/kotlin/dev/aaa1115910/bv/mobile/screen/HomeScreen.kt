package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.activities.user.LoginActivity
import dev.aaa1115910.bv.mobile.screen.home.ModalNavDrawerContent
import dev.aaa1115910.bv.mobile.screen.home.PopularScreen
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomeScreen(
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
                onLogout = {}
            )
        }
    ) {
        Scaffold(
            topBar = {
                HomeSearchTopBar(
                    query = searchText,
                    active = activeSearch,
                    sizeClass = windowSizeClass.widthSizeClass,
                    onQueryChange = { searchText = it },
                    onActiveChange = { activeSearch = it },
                    onOpenNavDrawer = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) return@Scaffold

                var selectedItem by remember { mutableStateOf(0) }
                val items = listOf("首页", "动态")

                Column {
                    AnimatedVisibility(visible = !activeSearch) {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Filled.Favorite,
                                            contentDescription = item
                                        )
                                    },
                                    label = { Text(item) },
                                    selected = selectedItem == index,
                                    onClick = { selectedItem = index }
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row {
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    var selectedItem by remember { mutableStateOf(0) }
                    val items = listOf("Home", "Search", "Settings")
                    val icons =
                        listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.Settings)
                    NavigationRail(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationRailItem(
                                icon = { Icon(icons[index], contentDescription = item) },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }
                    }
                }
                PopularScreen(
                    modifier = Modifier.padding(innerPadding),
                    gridState = lazyGridState
                )
            }

        }
    }
}

@Composable
private fun HomeSearchTopBar(
    modifier: Modifier = Modifier,
    query: String,
    active: Boolean,
    sizeClass: WindowWidthSizeClass,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onOpenNavDrawer: () -> Unit
) {
    val context = LocalContext.current
    var state by remember { mutableStateOf(0) }
    val titles = listOf("Tab 1", "Tab 2", "Tab 3 with lots of text")

    Box(
        modifier = modifier
            .padding(bottom = 8.dp),
    ) {
        when (sizeClass) {
            WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> {
                Column(
                    modifier = Modifier
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HomeSearchBar(
                            query = query,
                            active = active,
                            onQueryChange = onQueryChange,
                            onActiveChange = onActiveChange,
                            onOpenNavDrawer = onOpenNavDrawer
                        )
                    }

                    TabRow(selectedTabIndex = state) {
                        titles.forEachIndexed { index, title ->
                            Tab(
                                selected = state == index,
                                onClick = { state = index },
                                text = {
                                    Text(
                                        text = title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }
                }
            }

            WindowWidthSizeClass.Expanded -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 64.dp)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.Start),
                    verticalAlignment = Alignment.Bottom
                ) {
                    DockedHomeSearchBar(
                        query = query,
                        active = active,
                        onQueryChange = onQueryChange,
                        onActiveChange = onActiveChange
                    )
                    TabRow(selectedTabIndex = state) {
                        titles.forEachIndexed { index, title ->
                            Tab(
                                selected = state == index,
                                onClick = { state = index },
                                text = {
                                    Text(
                                        text = title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    active: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onOpenNavDrawer: () -> Unit
) {
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { onActiveChange(false) },
        active = active,
        onActiveChange = onActiveChange,
        leadingIcon = {
            if (!active) {
                IconButton(onClick = onOpenNavDrawer) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                }
            } else {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            if (!active) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null)
            }
        },
    ) { }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DockedHomeSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    active: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    DockedSearchBar(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 8.dp),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { onActiveChange(false) },
        active = active,
        onActiveChange = onActiveChange,
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (!active) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null)
            }
        },
    ) { }
}
