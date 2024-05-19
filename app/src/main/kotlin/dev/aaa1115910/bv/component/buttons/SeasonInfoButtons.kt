package dev.aaa1115910.bv.component.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.OutlinedButtonDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SeasonInfoButtons(
    modifier: Modifier = Modifier,
    lastPlayedIndex: Int,
    lastPlayedTitle: String = "",
    following: Boolean,
    isPublished: Boolean,
    publishDate: String,
    onPlay: () -> Unit,
    onClickFollow: (follow: Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isPublished) {
            Button(onClick = onPlay) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null
                    )
                    Text(text = if (lastPlayedIndex == -1) "开始播放" else lastPlayedTitle)
                }

            }
        } else {
            Button(onClick = {}) {
                Text(text = publishDate)
            }
        }
        FollowSeasonButton(
            following = following,
            onClick = onClickFollow
        )
    }
}

@Composable
fun FollowSeasonButton(
    modifier: Modifier = Modifier,
    following: Boolean,
    onClick: (follow: Boolean) -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = { onClick(!following) },
        colors = OutlinedButtonDefaults.colors(),
        border = OutlinedButtonDefaults.border()
    ) {
        Box(
            modifier = Modifier.size(20.dp)
        ) {
            if (following) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
fun SeasonInfoButtonsPreview() {
    BVTheme {
        SeasonInfoButtons(
            lastPlayedIndex = 3,
            lastPlayedTitle = "拯救灵依计划",
            following = false,
            isPublished = true,
            publishDate = "2021-10-01",
            onPlay = {},
            onClickFollow = {}
        )
    }
}