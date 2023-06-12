package dev.aaa1115910.bv.component.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import dev.aaa1115910.bv.component.Button
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SeasonInfoButtons(
    modifier: Modifier = Modifier,
    lastPlayedIndex: Int,
    lastPlayedTitle: String = "",
    following: Boolean,
    onPlay: () -> Unit,
    onClickFollow: (follow: Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            text = if (lastPlayedIndex == -1) "开始播放" else "继续播放 $lastPlayedTitle",
            onClick = onPlay
        )
        FollowSeasonButton(
            following = following,
            onClick = onClickFollow
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FollowSeasonButton(
    modifier: Modifier = Modifier,
    following: Boolean,
    onClick: (follow: Boolean) -> Unit
) {
    Button(
        modifier = modifier,
        icon = {
            if (following) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        },
        text = if (following) "已追番" else "追番",
        onClick = { onClick(!following) },
        color=MaterialTheme.colorScheme.secondary
    )
}

@Preview
@Composable
fun SeasonInfoButtonsPreview() {
    BVTheme {
        SeasonInfoButtons(
            lastPlayedIndex = 3,
            lastPlayedTitle = "拯救灵依计划",
            following = false,
            onPlay = {},
            onClickFollow = {}
        )
    }
}