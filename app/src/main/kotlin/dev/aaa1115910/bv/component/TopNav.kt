package dev.aaa1115910.bv.component

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material.LocalContentColor
import androidx.tv.material.Tab
import androidx.tv.material.TabRow
import coil.compose.AsyncImage
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.settings.SettingsActivity
import dev.aaa1115910.bv.activities.user.LoginActivity
import dev.aaa1115910.bv.activities.user.UserInfoActivity
import dev.aaa1115910.bv.util.requestFocus

@Composable
fun TopNav(
    modifier: Modifier = Modifier,
    isLogin: Boolean,
    username: String,
    face: String,
    focusInNav: Boolean,
    settingsButtonFocusRequester: FocusRequester,
    onSelectedChange: (TopNavItem) -> Unit = {},
    onClick: (TopNavItem) -> Unit = {},
    onShowUserPanel: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedNav by remember { mutableStateOf(TopNavItem.Popular) }
    val navList = listOf(TopNavItem.Search, TopNavItem.Popular, TopNavItem.Dynamics)

    val navItemFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        navItemFocusRequester.requestFocus(scope)
    }


    LaunchedEffect(focusInNav) {
        if (focusInNav) {
            navItemFocusRequester.requestFocus(scope)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bug Video",
                    fontSize = 13.sp,
                    modifier = Modifier
                        .alpha(0.5f)
                        .padding(horizontal = 30.dp)
                )

                var selectedTabIndex by remember { mutableStateOf(1) }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    separator = { Spacer(modifier = Modifier.width(12.dp)) },
                ) {
                    navList.forEachIndexed { index, tab ->
                        NavItemTab(
                            topNavItem = tab,
                            navItemFocusRequester = navItemFocusRequester,
                            selected = index == selectedTabIndex,
                            onFocus = {
                                if (tab != TopNavItem.Search) {
                                    onSelectedChange(tab)
                                    selectedNav = tab
                                }
                                selectedTabIndex = index
                            },
                            onClick = { onClick(tab) }
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsIcon(
                    modifier = Modifier.focusRequester(settingsButtonFocusRequester),
                    onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }
                )
                UserIcon(
                    modifier = Modifier.padding(end = 12.dp),
                    isLogin = isLogin,
                    username = username,
                    face = face,
                    onGotoLogin = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    },
                    onGotoInfo = {
                        context.startActivity(Intent(context, UserInfoActivity::class.java))
                    },
                    onFocused = {
                        if (isLogin) {
                            onShowUserPanel()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NavItemTab(
    modifier: Modifier = Modifier,
    topNavItem: TopNavItem,
    navItemFocusRequester: FocusRequester,
    selected: Boolean,
    onClick: () -> Unit,
    onFocus: () -> Unit
) {
    val context = LocalContext.current
    val tabModifier =
        if (topNavItem == TopNavItem.Popular || selected)
            modifier.focusRequester(navItemFocusRequester)
        else modifier

    Tab(
        modifier = tabModifier,
        selected = selected,
        onFocus = onFocus,
        onClick = onClick
    ) {
        if (topNavItem == TopNavItem.Search) {
            Row(
                modifier = Modifier
                    .height(32.dp)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 6.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = LocalContentColor.current,
                )
                AnimatedVisibility(visible = selected) {
                    Text(
                        text = topNavItem.getDisplayName(context),
                        color = LocalContentColor.current,
                        style = MaterialTheme.typography.labelLarge

                    )
                }
            }
        } else {
            Text(
                modifier = Modifier
                    .height(32.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                text = topNavItem.getDisplayName(context),
                color = LocalContentColor.current,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun SettingsIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()

    val iconRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = LinearEasing), RepeatMode.Restart
        )
    )

    IconButton(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus },
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.rotate(if (hasFocus) iconRotate else 0f),
            imageVector = Icons.Rounded.Settings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun UserIcon(
    modifier: Modifier = Modifier,
    isLogin: Boolean,
    username: String,
    face: String,
    onGotoLogin: () -> Unit,
    onGotoInfo: () -> Unit,
    onFocused: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    TextButton(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.hasFocus
                if (it.hasFocus) onFocused()
            },
        onClick = { if (isLogin) onGotoInfo() else onGotoLogin() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            Text(text = if (isLogin) username else "未登录")
            Box {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    color = Color.White
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        model = face,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
    }
}

enum class TopNavItem(private val _displayNameResId: Int) {
    Search(R.string.top_nav_item_search),
    Popular(R.string.top_nav_item_popular),
    Partition(R.string.top_nav_item_partition),
    Anime(R.string.top_nav_item_anime),
    Dynamics(R.string.top_nav_item_dynamics);

    fun getDisplayName(context: Context = BVApp.context): String {
        return context.getString(_displayNameResId)
    }
}