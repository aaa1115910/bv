package dev.aaa1115910.bv.tv.screens.user

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.QrImage
import dev.aaa1115910.bv.entity.BvScheme
import dev.aaa1115910.bv.entity.db.UserDB
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.tv.activities.user.LoginActivity
import dev.aaa1115910.bv.tv.activities.user.UserLockSettingsActivity
import dev.aaa1115910.bv.tv.component.TvAlertDialog
import dev.aaa1115910.bv.tv.screens.user.lock.UnlockSwitchUserContent
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.UserSwitchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@Composable
fun UserSwitchScreen(
    modifier: Modifier = Modifier,
    userSwitchViewModel: UserSwitchViewModel = koinViewModel(),
    userRepository: UserRepository = getKoin().get()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val userList = userSwitchViewModel.userDbList

    var showUnlock by remember { mutableStateOf(false) }
    var unlockUser: UserDB? by remember { mutableStateOf(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    //userSwitchViewModel.updateUserDbList()
                    userSwitchViewModel.updateData()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val unlockFocusRequester = remember { FocusRequester() }

    LaunchedEffect(showUnlock) {
        if (showUnlock) unlockFocusRequester.requestFocus()
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp)
    ) {
        Box {
            UserSwitchContent(
                userList = userList,
                currentUid = userRepository.uid,
                loadingUserList = userSwitchViewModel.loading,
                onAddUser = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                },
                onDeleteUser = { user ->
                    scope.launch(Dispatchers.IO) {
                        userSwitchViewModel.deleteUser(user)
                        if (userList.isEmpty()) (context as Activity).finish()
                    }
                },
                onSwitchUser = { user ->
                    if (user.uid != userRepository.uid && user.lock.isNotBlank()) {
                        unlockUser = user
                        showUnlock = true
                    } else {
                        scope.launch(Dispatchers.IO) {
                            userSwitchViewModel.switchUser(user)
                            (context as Activity).finish()
                        }
                    }
                },
                onShowUserLockSettings = { uid ->
                    UserLockSettingsActivity.actionStart(context, uid)
                }
            )

            if (showUnlock) {
                UnlockSwitchUserContent(
                    modifier = Modifier.focusRequester(unlockFocusRequester),
                    userList = userList,
                    unlockUser = unlockUser!!,
                    onUnlockSuccess = { user ->
                        scope.launch(Dispatchers.IO) {
                            userSwitchViewModel.switchUser(user)
                            (context as Activity).finish()
                        }
                    },
                    onCancel = {
                        showUnlock = false
                    }
                )
            }
        }
    }
}

@Composable
private fun UserSwitchContent(
    modifier: Modifier = Modifier,
    userList: List<UserDB> = emptyList(),
    currentUid: Long,
    loadingUserList: Boolean,
    onSwitchUser: (UserDB) -> Unit,
    onDeleteUser: (UserDB) -> Unit,
    onAddUser: () -> Unit,
    onShowUserLockSettings: (Long) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var choosedUser by remember {
        mutableStateOf(
            UserDB(
                uid = -1,
                username = "None",
                avatar = "https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg",
                auth = ""
            )
        )
    }

    var isInManagerMode by remember { mutableStateOf(false) }
    var showUserMenuDialog by remember { mutableStateOf(false) }
    var showAuthDataDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(loadingUserList) {
        if (!loadingUserList) focusRequester.requestFocus()
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.user_switch_title),
                    style = MaterialTheme.typography.displaySmall
                )
            }

            LazyRow(
                modifier = Modifier.focusRequester(focusRequester),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(items = userList) { user ->
                    UserItem(
                        avatar = user.avatar,
                        username = user.username,
                        lockEnabled = user.lock.isNotBlank(),
                        onClick = {
                            if (isInManagerMode) {
                                choosedUser = user
                                showUserMenuDialog = true
                            } else {
                                onSwitchUser(user)
                            }
                        }
                    )
                }
                if (!isInManagerMode) {
                    item {
                        AddUserItem(
                            onClick = onAddUser
                        )
                    }
                }
            }

            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp),
                onClick = { isInManagerMode = !isInManagerMode }
            ) {
                if (isInManagerMode) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                        Text(stringResource(R.string.user_switch_button_exit_manage_account))
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        Text(stringResource(R.string.user_switch_button_manage_account))
                    }
                }
            }
        }
    }

    UserMenuDialog(
        show = showUserMenuDialog,
        onHideDialog = { showUserMenuDialog = false },
        username = choosedUser.username,
        uid = choosedUser.uid,
        showTokenButton = choosedUser.uid == currentUid || choosedUser.lock.isBlank(),
        onShowUserAuthData = { showAuthDataDialog = true },
        onDeleteUser = { showDeleteConfirmDialog = true },
        onShowUserLockSettings = { uid ->
            isInManagerMode = false
            onShowUserLockSettings(uid)
        }
    )

    UserAuthDataDialog(
        show = showAuthDataDialog,
        onHideDialog = { showAuthDataDialog = false },
        userDB = choosedUser
    )

    DeleteConfirmDialog(
        show = showDeleteConfirmDialog,
        onHideDialog = { showDeleteConfirmDialog = false },
        userDB = choosedUser,
        onConfirm = {
            onDeleteUser(choosedUser)
            showDeleteConfirmDialog = false
        }
    )
}

@Composable
fun UserMenuDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    username: String,
    uid: Long,
    showTokenButton: Boolean,
    onShowUserAuthData: () -> Unit,
    onDeleteUser: () -> Unit,
    onShowUserLockSettings: (Long) -> Unit
) {
    val menuFocusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            menuFocusRequester.requestFocus()
        }
    }

    if (show) {
        TvAlertDialog(
            modifier = modifier,
            onDismissRequest = onHideDialog,
            title = { Text(text = username) },
            text = {
                LazyColumn(
                    modifier = Modifier.width(240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    if (showTokenButton) {
                        item {
                            UserMenuButton(
                                modifier = Modifier.focusRequester(menuFocusRequester),
                                text = stringResource(R.string.user_switch_menu_show_token),
                                onClick = {
                                    onHideDialog()
                                    onShowUserAuthData()
                                }
                            )
                        }
                    }

                    item {
                        UserMenuButton(
                            modifier = Modifier
                                .ifElse(
                                    !showTokenButton,
                                    Modifier.focusRequester(menuFocusRequester)
                                ),
                            text = stringResource(R.string.user_switch_menu_user_lock),
                            onClick = {
                                onHideDialog()
                                onShowUserLockSettings(uid)
                            }
                        )
                    }

                    item {
                        UserMenuButton(
                            text = stringResource(R.string.user_switch_menu_delete_account),
                            onClick = {
                                onHideDialog()
                                onDeleteUser()
                            },
                            color = MaterialTheme.colorScheme.errorContainer
                        )
                    }
                }
            },
            dismissButton = {},
            confirmButton = {}
        )
    }
}

@Composable
fun UserAuthDataDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    userDB: UserDB
) {
    var qrContent by remember { mutableStateOf("") }

    LaunchedEffect(show) {
        if (show) {
            qrContent = BvScheme.QrToken(
                auth = userDB.auth,
                uid = userDB.uid,
                username = userDB.username,
                avatar = userDB.avatar
            ).buildUri()
        }
    }

    BackHandler(show) { onHideDialog() }

    if (show) {
        Scaffold(
            modifier
                .fillMaxSize(),
            topBar = {
                Box(
                    modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = userDB.username,
                        fontSize = 48.sp
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(4f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        QrImage(
                            modifier = Modifier
                                .size(240.dp),
                            content = qrContent
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(6f)
                            .padding(end = 60.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Text(
                                text = "扫码二维码以登录移动端",
                                style = MaterialTheme.typography.displaySmall
                            )

                            Text(text = userDB.auth)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    userDB: UserDB,
    onConfirm: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) focusRequester.requestFocus(scope)
    }

    if (show) {
        TvAlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { Text(text = stringResource(R.string.delete_account_confirm_dialog_title)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.delete_account_confirm_dialog_text,
                        userDB.username,
                        userDB.uid
                    )
                )
            },
            confirmButton = {
                Button(onClick = { onConfirm() }) {
                    Text(text = stringResource(R.string.delete_account_confirm_dialog_confirm))
                }
            },
            dismissButton = {
                OutlinedButton(
                    modifier = Modifier.focusRequester(focusRequester),
                    onClick = { onHideDialog() }
                ) {
                    Text(text = stringResource(R.string.delete_account_confirm_dialog_dismiss))
                }
            }
        )
    }
}

@Composable
fun UserItem(
    modifier: Modifier = Modifier,
    avatar: String,
    username: String,
    lockEnabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (onClick != null) {
            BadgedBox(
                modifier = Modifier.padding(18.dp),
                badge = {
                    if (lockEnabled) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    }
                }
            ) {
                Surface(
                    modifier = Modifier
                        .size(80.dp),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = ClickableSurfaceDefaults.shape(
                        shape = CircleShape
                    ),
                    glow = ClickableSurfaceDefaults.glow(
                        focusedGlow = Glow(
                            elevationColor = MaterialTheme.colorScheme.border,
                            elevation = 16.dp
                        )
                    ),
                    onClick = onClick,
                    border = ClickableSurfaceDefaults.border(
                        focusedBorder = Border(
                            BorderStroke(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.border.copy(alpha = 0.7f)
                            )
                        )
                    )
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        model = avatar,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .padding(18.dp)
                    .size(80.dp),
                colors = SurfaceDefaults.colors(
                    containerColor = Color.DarkGray
                ),
                shape = CircleShape
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    model = avatar,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }
        }
        Box(
            modifier = Modifier.height(26.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(),
                text = username,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AddUserItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .padding(18.dp)
                .size(80.dp),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = ClickableSurfaceDefaults.shape(
                shape = CircleShape
            ),
            glow = ClickableSurfaceDefaults.glow(
                focusedGlow = Glow(
                    elevationColor = MaterialTheme.colorScheme.inverseSurface,
                    elevation = 16.dp
                )
            ),
            onClick = onClick,
            border = ClickableSurfaceDefaults.border(
                focusedBorder = Border(
                    BorderStroke(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.border.copy(alpha = 0.7f)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Box(
            modifier = Modifier.height(26.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(),
                text = stringResource(R.string.user_switch_add_user),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserItemPreview() {
    BVTheme {
        UserItem(
            avatar = "",
            username = "This is a user name",
            onClick = {},
            lockEnabled = true
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddUserItemPreview() {
    BVTheme {
        AddUserItem(
            onClick = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Preview(device = "id:tv_1080p", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserSwitchContentPreview() {
    BVTheme {
        UserSwitchContent(
            userList = listOf(
                UserDB(
                    uid = 0,
                    username = "大楚兴 陈胜王 大楚兴 陈胜王",
                    avatar = "0https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg",
                    auth = "{xxx1}"
                ),
                UserDB(
                    uid = 1,
                    username = "This is a long username",
                    avatar = "0https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg",
                    auth = "{xxx2}",
                    lock = "rdrd"
                ),
                UserDB(
                    uid = 2,
                    username = "\uD835\uDD4F",
                    avatar = "0https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg",
                    auth = "{xxx3}"
                )
            ),
            currentUid = 0L,
            loadingUserList = false,
            onSwitchUser = {},
            onDeleteUser = {},
            onAddUser = {},
            onShowUserLockSettings = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Preview(device = "id:tv_1080p", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserMenuDialogPreview() {
    BVTheme {
        UserMenuDialog(
            show = true,
            onHideDialog = {},
            username = "This is a user name",
            uid = 0,
            showTokenButton = true,
            onShowUserAuthData = {},
            onDeleteUser = {},
            onShowUserLockSettings = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Preview(device = "id:tv_1080p", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserAuthDataDialogPreview() {
    BVTheme {
        UserAuthDataDialog(
            show = true,
            onHideDialog = {},
            userDB = UserDB(
                uid = 0,
                username = "Android Studio Official",
                avatar = "0https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg",
                auth = "this is a long auth data string that is used to test the dialog layout and should be long enough to wrap into multiple lines."
            ),
        )
    }
}

@Composable
private fun UserMenuButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    color: Color? = null
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = ButtonDefaults.shape(shape = MaterialTheme.shapes.medium),
        colors = if (color != null) ButtonDefaults.colors(containerColor = color) else ButtonDefaults.colors(),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}