package dev.aaa1115910.bv.component

import android.view.KeyEvent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CrueltyFree
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.requestFocus

private val lineHeight = 80.dp

@Composable
fun UserPanel(
    modifier: Modifier = Modifier,
    username: String,
    face: String,
    onHide: () -> Unit,
    onGoMy: () -> Unit,
    onGoHistory: () -> Unit,
    onGoFavorite: () -> Unit,
    onGoFollowing: () -> Unit,
    onGoLater: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    Surface(
        modifier = modifier
            .width(300.dp)
            .onPreviewKeyEvent {
                when (it.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_BACK -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) onHide()
                        return@onPreviewKeyEvent true
                    }
                }
                false
            },
        shape = MaterialTheme.shapes.medium
    ) {
        TvLazyVerticalGrid(
            columns = TvGridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp)
        ) {
            item(
                span = { TvGridItemSpan(2) },
            ) {
                UserPanelMyItem(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                    return@onPreviewKeyEvent true
                                }

                                KeyEvent.KEYCODE_DPAD_LEFT -> {
                                    if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) onHide()
                                    return@onPreviewKeyEvent true
                                }
                            }
                            false
                        },
                    username = username,
                    face = face,
                    onClick = {
                        onGoMy()
                        onHide()
                    }
                )
            }
            item {
                UserPanelSmallItem(
                    modifier = Modifier
                        .onPreviewKeyEvent {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_LEFT -> {
                                    if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) onHide()
                                    return@onPreviewKeyEvent true
                                }
                            }
                            false
                        },
                    title = "历史记录",
                    icon = Icons.Rounded.History,
                    onClick = {
                        onGoHistory()
                        onHide()
                    }
                )
            }
            item {
                UserPanelSmallItem(
                    modifier = Modifier,
                    title = "私人藏品",
                    icon = Icons.Rounded.FavoriteBorder,
                    onClick = {
                        onGoFavorite()
                        onHide()
                    }
                )
            }
            item {
                UserPanelSmallItem(
                    modifier = Modifier
                        .onPreviewKeyEvent {
                            println(it.nativeKeyEvent)
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_LEFT -> {
                                    if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) onHide()
                                    return@onPreviewKeyEvent true
                                }
                            }
                            false
                        },
                    title = "我追的番",
                    icon = Icons.Rounded.CrueltyFree,
                    onClick = {
                        onGoFollowing()
                        onHide()
                    }
                )
            }
            item {
                UserPanelSmallItem(
                    modifier = Modifier,
                    title = "现在不看",
                    icon = Icons.Rounded.Schedule,
                    onClick = {
                        onGoLater()
                        onHide()
                    }
                )
            }
        }
    }
}

@Composable
private fun UserPanelMyItem(
    modifier: Modifier = Modifier,
    username: String,
    face: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(lineHeight),
        onClick = onClick,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 40.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = username)
                Text(text = "")
            }
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

@Composable
private fun UserPanelSmallItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(4.dp)
            .height(lineHeight),
        onClick = onClick,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart),
                imageVector = icon,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomStart),
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }
}


@Preview
@Composable
private fun UserPanelPreview() {
    BVTheme {
        UserPanel(
            username = "",
            face = "",
            onHide = {},
            onGoMy = {},
            onGoHistory = {},
            onGoFollowing = {},
            onGoFavorite = {},
            onGoLater = {}
        )
    }
}