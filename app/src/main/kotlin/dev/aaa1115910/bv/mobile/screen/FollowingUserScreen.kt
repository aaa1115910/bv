package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.user.FollowedUser
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.mobile.activities.UserSpaceActivity
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.viewmodel.user.FollowViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun FollowingUserScreen(
    modifier: Modifier = Modifier,
    followViewModel: FollowViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val onClickUser: (FollowedUser) -> Unit = { followedUser ->
        UserSpaceActivity.actionStart(
            context = context,
            mid = followedUser.mid,
            name = followedUser.name
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(R.string.title_mobile_activity_following_user)) },
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (followViewModel.updating) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Loading()
            }
        }

        if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
            LazyColumn(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            ) {
                if (!followViewModel.updating) {
                    items(items = followViewModel.followedUsers) { followedUser ->
                        FollowingUserListItem(
                            followedUser = followedUser,
                            onClick = onClickUser
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                columns = GridCells.Fixed(2)
            ) {
                if (!followViewModel.updating) {
                    items(items = followViewModel.followedUsers) { followedUser ->
                        FollowingUserListItem(
                            followedUser = followedUser,
                            onClick = onClickUser
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowingUserListItem(
    modifier: Modifier = Modifier,
    followedUser: FollowedUser,
    onClick: (FollowedUser) -> Unit = {}
) {
    ListItem(
        modifier = modifier
            .clickable { onClick(followedUser) },
        headlineContent = { Text(text = followedUser.name) },
        supportingContent = {
            Text(
                text = followedUser.sign,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray, CircleShape)
                    .clip(CircleShape),
                model = followedUser.avatar,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    )
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(text = "Loading")
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun FollowingUserListItemPreview() {
    BVMobileTheme {
        FollowingUserListItem(
            followedUser = FollowedUser(
                mid = 1L,
                name = "UP name",
                avatar = "https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg@450w_450h_progressive.webp",
                sign = "This is a sign"
            )
        )
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    BVMobileTheme {
        Surface {
            Loading()
        }
    }
}