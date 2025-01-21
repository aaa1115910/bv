package dev.aaa1115910.bv.mobile.component.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SupervisorAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.aaa1115910.bv.entity.db.UserDB
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.ifElse

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun UserDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    currentUser: UserDB?,
    userList: List<UserDB>,
    onHideDialog: () -> Unit,
    onSwitchUser: (UserDB) -> Unit,
    onAddUser: () -> Unit,
    onDeleteUser: (UserDB) -> Unit,
    onOpenFollowingUser: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenFavorite: () -> Unit,
    onOpenFollowingPgc: () -> Unit,
    onOpenToView: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    if (show) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onHideDialog
                )
        ) {
            UserDialogContent(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 80.dp)
                    .ifElse(
                        { windowWidthSizeClass != WindowWidthSizeClass.Compact },
                        Modifier.width(500.dp)
                    )
                    .clip(MaterialTheme.shapes.extraLarge)
                    .align(Alignment.TopCenter),
                currentUser = currentUser,
                userList = userList,
                onClose = onHideDialog,
                onSwitchUser = onSwitchUser,
                onAddUser = onAddUser,
                onDeleteUser = onDeleteUser,
                onOpenFollowingUser = onOpenFollowingUser,
                onOpenHistory = onOpenHistory,
                onOpenFavorite = onOpenFavorite,
                onOpenFollowingPgc = onOpenFollowingPgc,
                onOpenToView = onOpenToView,
                onOpenSettings = onOpenSettings
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDialogContent(
    modifier: Modifier = Modifier,
    currentUser: UserDB?,
    userList: List<UserDB>,
    onClose: () -> Unit,
    onSwitchUser: (UserDB) -> Unit,
    onAddUser: () -> Unit,
    onDeleteUser: (UserDB) -> Unit,
    onOpenFollowingUser: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenFavorite: () -> Unit,
    onOpenFollowingPgc: () -> Unit,
    onOpenToView: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    var expandUserManager by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = { Text(text = "Bug Video") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (currentUser == null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        ListItem(
                            modifier = Modifier.clickable { onAddUser() },
                            headlineContent = { Text(text = "登录") },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.PersonAdd,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                } else {
                    Column {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(
                                topStart = MaterialTheme.shapes.extraLarge.topStart,
                                topEnd = MaterialTheme.shapes.extraLarge.topEnd,
                                bottomStart = MaterialTheme.shapes.extraSmall.bottomStart,
                                bottomEnd = MaterialTheme.shapes.extraSmall.bottomEnd
                            )
                        ) {
                            UserItem(
                                username = currentUser.username,
                                avatar = currentUser.avatar,
                                uid = currentUser.uid,
                                expandUserManager = expandUserManager,
                                onClick = {},
                                onExpandUserManagerChange = { expandUserManager = it }
                            )
                        }
                        AnimatedVisibility(
                            visible = expandUserManager,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Card(
                                modifier = Modifier.padding(top = 2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Column {
                                    userList
                                        .filter { it != currentUser }
                                        .forEach { user ->
                                            UserItem(
                                                username = user.username,
                                                avatar = user.avatar,
                                                uid = user.uid,
                                                onClick = {
                                                    onSwitchUser(user)
                                                    onClose()
                                                }
                                            )
                                        }
                                }
                                ListItem(
                                    modifier = Modifier.clickable { onAddUser() },
                                    headlineContent = { Text(text = "添加其他账号") },
                                    leadingContent = {
                                        Icon(
                                            modifier = Modifier
                                                .width(40.dp)
                                                .scale(scaleX = -1f, scaleY = 1f),
                                            imageVector = Icons.Outlined.PersonAdd,
                                            contentDescription = null
                                        )
                                    }
                                )
                                ListItem(
                                    modifier = Modifier.clickable { onDeleteUser(currentUser) },
                                    headlineContent = { Text(text = "移除此设备上的账号") },
                                    leadingContent = {
                                        Icon(
                                            modifier = Modifier
                                                .width(40.dp),
                                            imageVector = Icons.Outlined.PersonRemove,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(
                            topStart = MaterialTheme.shapes.extraSmall.topStart,
                            topEnd = MaterialTheme.shapes.extraSmall.topEnd,
                            bottomStart = MaterialTheme.shapes.extraLarge.bottomStart,
                            bottomEnd = MaterialTheme.shapes.extraLarge.bottomEnd
                        )
                    ) {
                        Column {
                            ListItem(
                                modifier = Modifier.clickable { onOpenFollowingUser() },
                                headlineContent = { Text(text = "我的关注") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.SupervisorAccount,
                                        contentDescription = null
                                    )
                                }
                            )
                            ListItem(
                                modifier = Modifier.clickable { onOpenHistory() },
                                headlineContent = { Text(text = "历史记录") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.History,
                                        contentDescription = null
                                    )
                                }
                            )
                            ListItem(
                                modifier = Modifier.clickable { onOpenFavorite() },
                                headlineContent = { Text(text = "我的收藏") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.Favorite,
                                        contentDescription = null
                                    )
                                }
                            )
                            ListItem(
                                modifier = Modifier.clickable { onOpenFollowingPgc() },
                                headlineContent = { Text(text = "我的追番") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.SupervisorAccount,
                                        contentDescription = null
                                    )
                                }
                            )
                            ListItem(
                                modifier = Modifier.clickable { onOpenToView() },
                                headlineContent = { Text(text = "稍后再看") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.SupervisorAccount,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }

                ListItem(
                    modifier = Modifier.clickable { onOpenSettings() },
                    headlineContent = { Text(text = "设置") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UserDialogContentPreview() {
    var currentUser by remember { mutableStateOf(UserDB(-1, -1, "", "", "")) }
    val userList = remember { mutableStateListOf<UserDB>() }

    LaunchedEffect(Unit) {
        for (i in 0..5) {
            userList.add(
                UserDB(
                    id = i,
                    uid = 100000L + i,
                    username = "User $i",
                    avatar = "",
                    auth = ""
                )
            )
        }
        currentUser = userList[1]
    }

    BVMobileTheme {
        UserDialogContent(
            currentUser = currentUser,
            userList = userList,
            onClose = {},
            onSwitchUser = {},
            onAddUser = {},
            onDeleteUser = {},
            onOpenFollowingUser = {},
            onOpenHistory = {},
            onOpenFavorite = {},
            onOpenFollowingPgc = {},
            onOpenToView = {},
            onOpenSettings = {}
        )
    }
}

@Preview
@Composable
private fun UserDialogContentLoginRequirePreview() {
    var currentUser by remember { mutableStateOf<UserDB?>(null) }
    val userList = remember { mutableStateListOf<UserDB>() }

    BVMobileTheme {
        UserDialogContent(
            currentUser = currentUser,
            userList = userList,
            onClose = {},
            onSwitchUser = {},
            onAddUser = {},
            onDeleteUser = {},
            onOpenFollowingUser = {},
            onOpenHistory = {},
            onOpenFavorite = {},
            onOpenFollowingPgc = {},
            onOpenToView = {},
            onOpenSettings = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun UserDialogPreview() {
    var currentUser by remember { mutableStateOf(UserDB(-1, -1, "", "", "")) }
    val userList = remember { mutableStateListOf<UserDB>() }

    LaunchedEffect(Unit) {
        for (i in 0..5) {
            userList.add(
                UserDB(
                    id = i,
                    uid = 100000L + i,
                    username = "User $i",
                    avatar = "",
                    auth = ""
                )
            )
        }
        currentUser = userList[1]
    }

    BVMobileTheme {
        UserDialog(
            show = true,
            currentUser = currentUser,
            userList = userList,
            onHideDialog = {},
            onSwitchUser = {},
            onAddUser = {},
            onDeleteUser = {},
            onOpenFollowingUser = {},
            onOpenHistory = {},
            onOpenFavorite = {},
            onOpenFollowingPgc = {},
            onOpenToView = {},
            onOpenSettings = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun UserDialogWidthScreenPreview() {
    val windowSize = WindowSizeClass.calculateFromSize(DpSize(1280.dp, 800.dp))
    var currentUser by remember { mutableStateOf(UserDB(-1, -1, "", "", "")) }
    val userList = remember { mutableStateListOf<UserDB>() }

    LaunchedEffect(Unit) {
        for (i in 0..5) {
            userList.add(
                UserDB(
                    id = i,
                    uid = 100000L + i,
                    username = "User $i",
                    avatar = "",
                    auth = ""
                )
            )
        }
        currentUser = userList[1]
    }

    BVMobileTheme {
        UserDialog(
            show = true,
            windowWidthSizeClass = windowSize.widthSizeClass,
            currentUser = currentUser,
            userList = userList,
            onHideDialog = {},
            onSwitchUser = {},
            onAddUser = {},
            onDeleteUser = {},
            onOpenFollowingUser = {},
            onOpenHistory = {},
            onOpenFavorite = {},
            onOpenFollowingPgc = {},
            onOpenToView = {},
            onOpenSettings = {}
        )
    }
}


@Composable
private fun UserItem(
    modifier: Modifier = Modifier,
    username: String,
    avatar: String,
    uid: Long,
    expandUserManager: Boolean = false,
    onClick: () -> Unit,
    onExpandUserManagerChange: ((Boolean) -> Unit)? = null
) {
    ListItem(
        modifier = modifier
            .clickable { onClick() },
        headlineContent = {
            Text(text = username)
        },
        supportingContent = {
            Text(text = "$uid")
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                model = avatar,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        },
        trailingContent = (@Composable {
            if (expandUserManager) {
                IconButton(onClick = { onExpandUserManagerChange?.invoke(false) }) {
                    Icon(imageVector = Icons.Default.ArrowDropUp, contentDescription = null)
                }
            } else {
                IconButton(onClick = { onExpandUserManagerChange?.invoke(true) }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        }).takeIf { onExpandUserManagerChange != null }
    )
}

@Preview
@Composable
private fun UserItemPreview() {
    BVMobileTheme {
        UserItem(
            username = "username",
            avatar = "",
            uid = 123456,
            onClick = {}
        )
    }
}