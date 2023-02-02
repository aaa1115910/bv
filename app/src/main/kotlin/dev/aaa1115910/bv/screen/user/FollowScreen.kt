package dev.aaa1115910.bv.screen.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import coil.compose.AsyncImage
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.UpInfoActivity
import dev.aaa1115910.bv.component.LoadingTip
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.focusedBorder
import dev.aaa1115910.bv.util.focusedScale
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.user.FollowViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
    modifier: Modifier = Modifier,
    followViewModel: FollowViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val defaultFocusRequester = remember { FocusRequester() }

    var currentIndex by remember { mutableStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 3 } }
    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)

    LaunchedEffect(followViewModel.updating) {
        if (!followViewModel.updating) {
            defaultFocusRequester.requestFocus(scope)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(
                    start = 48.dp,
                    top = 24.dp,
                    bottom = 8.dp,
                    end = 48.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.user_homepage_follow),
                        fontSize = titleFontSize.sp
                    )
                    Text(
                        text = stringResource(
                            R.string.load_data_count,
                            followViewModel.followedUsers.size
                        ),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { innerPadding ->
        TvLazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            columns = TvGridCells.Fixed(3),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (!followViewModel.updating) {
                itemsIndexed(items = followViewModel.followedUsers) { index, up ->
                    val upCardModifier =
                        if (index == 0) Modifier.focusRequester(defaultFocusRequester) else Modifier
                    UpCard(
                        modifier = upCardModifier
                            .focusedScale(0.95f),
                        face = up.face,
                        sign = up.sign,
                        username = up.uname,
                        onFocusChange = {
                            if (it) currentIndex = index
                        },
                        onClick = {
                            UpInfoActivity.actionStart(
                                context = context,
                                mid = up.mid,
                                name = up.uname
                            )
                        }
                    )
                }
            } else {
                item(
                    span = { TvGridItemSpan(3) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingTip()
                    }
                }
            }
        }
    }
}

@Composable
fun UpCard(
    modifier: Modifier = Modifier,
    face: String,
    sign: String,
    username: String,
    onFocusChange: (hasFocus: Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .onFocusChanged { onFocusChange(it.hasFocus) }
            .size(280.dp, 80.dp)
            .focusedBorder(MaterialTheme.shapes.large)
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(48.dp)
                    .clip(CircleShape),
                color = Color.White
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    model = face,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }
            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sign,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun UpCardPreview() {
    BVTheme {
        UpCard(
            face = "",
            sign = "一只业余做翻译的Klei迷，动态区UP（自称），缺氧官中反馈可私信",
            username = "username",
            onFocusChange = {},
            onClick = {}
        )
    }
}
