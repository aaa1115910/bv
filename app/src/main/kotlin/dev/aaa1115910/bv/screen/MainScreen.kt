package dev.aaa1115910.bv.screen

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.rememberDrawerState
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.settings.SettingsActivity
import dev.aaa1115910.bv.activities.user.FavoriteActivity
import dev.aaa1115910.bv.activities.user.FollowingSeasonActivity
import dev.aaa1115910.bv.activities.user.HistoryActivity
import dev.aaa1115910.bv.activities.user.ToViewActivity
import dev.aaa1115910.bv.activities.user.LoginActivity
import dev.aaa1115910.bv.activities.user.UserInfoActivity
import dev.aaa1115910.bv.component.UserPanel
import dev.aaa1115910.bv.screen.main.DrawerContent
import dev.aaa1115910.bv.screen.main.DrawerItem
import dev.aaa1115910.bv.screen.main.HomeContent
import dev.aaa1115910.bv.screen.main.PgcContent
import dev.aaa1115910.bv.screen.main.UgcContent
import dev.aaa1115910.bv.screen.search.SearchInputScreen
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    recommendViewModel: RecommendViewModel = koinViewModel(),
    popularViewModel: PopularViewModel = koinViewModel(),
    dynamicViewModel: DynamicViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val logger = KotlinLogging.logger("MainScreen")
    var showUserPanel by remember { mutableStateOf(false) }
    var lastPressBack: Long by remember { mutableLongStateOf(0L) }
    var selectedDrawerItem by remember { mutableStateOf(DrawerItem.Home) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val mainFocusRequester = remember { FocusRequester() }
    val ugcFocusRequester = remember { FocusRequester() }
    val pgcFocusRequester = remember { FocusRequester() }
    val searchFocusRequester = remember { FocusRequester() }

    val handleBack = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPressBack < 1000 * 3) {
            logger.fInfo { "Exiting bug video" }
            (context as Activity).finish()
        } else {
            lastPressBack = currentTime
            R.string.home_press_back_again_to_exit.toast(context)
        }
    }

    val onFocusToContent = {
        when (selectedDrawerItem) {
            DrawerItem.Home -> mainFocusRequester.requestFocus()
            DrawerItem.UGC -> ugcFocusRequester.requestFocus()
            DrawerItem.PGC -> pgcFocusRequester.requestFocus()
            DrawerItem.Search -> searchFocusRequester.requestFocus()
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        runCatching {
            mainFocusRequester.requestFocus()
        }.onFailure {
            logger.fException(it) { "request default focus requester failed" }
        }
    }

    BackHandler {
        handleBack()
    }

    NavigationDrawer(
        modifier = modifier,
        drawerContent = {
            DrawerContent(
                isLogin = userViewModel.isLogin,
                avatar = userViewModel.face,
                username = userViewModel.username,
                //avatar = "https://i2.hdslb.com/bfs/face/ef0457addb24141e15dfac6fbf45293ccf1e32ab.jpg",
                //username = "碧诗",
                onDrawerItemChanged = { selectedDrawerItem = it },
                onOpenSettings = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
                onShowUserPanel = {
                    showUserPanel = true
                },
                onFocusToContent = onFocusToContent,
                onLogin = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
            )
        },
        drawerState = drawerState
    ) {
        Box(
            modifier = Modifier
        ) {
            AnimatedContent(
                targetState = selectedDrawerItem,
                label = "main animated content",
                transitionSpec = {
                    val coefficient = 20
                    if (targetState.ordinal < initialState.ordinal) {
                        fadeIn() + slideInVertically { -it / coefficient } togetherWith
                                fadeOut() + slideOutVertically { it / coefficient }
                    } else {
                        fadeIn() + slideInVertically { it / coefficient } togetherWith
                                fadeOut() + slideOutVertically { -it / coefficient }
                    }
                }
            ) { screen ->
                when (screen) {
                    DrawerItem.Home -> HomeContent(navFocusRequester = mainFocusRequester)
                    DrawerItem.UGC -> UgcContent(navFocusRequester = ugcFocusRequester)
                    DrawerItem.PGC -> PgcContent(navFocusRequester = pgcFocusRequester)
                    DrawerItem.Search -> SearchInputScreen(defaultFocusRequester = searchFocusRequester)
                    else -> {}
                }
            }

            AnimatedVisibility(
                visible = showUserPanel,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.Center),
                        visible = showUserPanel,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut()
                    ) {
                        UserPanel(
                            modifier = Modifier
                                .padding(12.dp),
                            username = userViewModel.username,
                            face = userViewModel.face,
                            onHide = { showUserPanel = false },
                            onGoMy = {
                                context.startActivity(Intent(context, UserInfoActivity::class.java))
                            },
                            onGoHistory = {
                                context.startActivity(Intent(context, HistoryActivity::class.java))
                            },
                            onGoFavorite = {
                                context.startActivity(Intent(context, FavoriteActivity::class.java))
                            },
                            onGoFollowing = {
                                context.startActivity(
                                    Intent(
                                        context,
                                        FollowingSeasonActivity::class.java
                                    )
                                )
                            },
                            onGoLater = {
                                context.startActivity(Intent(context, ToViewActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}
