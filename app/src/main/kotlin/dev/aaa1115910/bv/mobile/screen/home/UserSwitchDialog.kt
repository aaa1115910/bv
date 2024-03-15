package dev.aaa1115910.bv.mobile.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.aaa1115910.bv.entity.db.UserDB
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

@Composable
fun UserSwitchDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    currentUser: UserDB,
    userList: List<UserDB>,
    onSwitchUser: (UserDB) -> Unit,
    onAddUser: () -> Unit,
    onDeleteUser: (UserDB) -> Unit
) {
    if (show) {
        UserSwitchContent(
            modifier = modifier,
            currentUser = currentUser,
            userList = userList,
            onClose = onHideDialog,
            onSwitchUser = onSwitchUser,
            onAddUser = onAddUser,
            onDeleteUser = onDeleteUser
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSwitchContent(
    modifier: Modifier = Modifier,
    currentUser: UserDB,
    userList: List<UserDB>,
    onClose: () -> Unit,
    onSwitchUser: (UserDB) -> Unit,
    onAddUser: () -> Unit,
    onDeleteUser: (UserDB) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Bug Video") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
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
                        onClick = {}
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
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
                        modifier = Modifier.clickable { /*TODO remove account*/ },
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
    }
}

@Composable
private fun UserItem(
    modifier: Modifier = Modifier,
    username: String,
    avatar: String,
    uid: Long,
    onClick: () -> Unit
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
        }
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

@Preview
@Composable
fun UserSwitchContentPreview() {
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
        UserSwitchContent(
            currentUser = currentUser,
            userList = userList,
            onClose = {},
            onSwitchUser = {},
            onAddUser = {},
            onDeleteUser = {}
        )
    }
}