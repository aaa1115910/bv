package dev.aaa1115910.bv.component.videocard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.aaa1115910.bv.component.UpIcon
import dev.aaa1115910.bv.component.formatMinSec
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SmallVideoCard(
    modifier: Modifier = Modifier,
    title: String,
    cover: String,
    upName: String,
    play: Int? = null,
    playString: String = "",
    danmaku: Int? = null,
    danmakuString: String = "",
    time: Int? = null,
    timeString: String = "",
    onClick: () -> Unit = {},
    onFocus: () -> Unit = {}
) {
    var isFocussed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocussed) 1f else 0.9f)
    val borderAlpha by animateFloatAsState(if (isFocussed) 1f else 0f)

    LaunchedEffect(isFocussed) {
        if (isFocussed) onFocus()
    }

    Card(
        modifier = modifier
            .scale(scale)
            .onFocusChanged { focusState -> isFocussed = focusState.isFocused }
            .focusable()
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = borderAlpha),
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6f)
                        .clip(MaterialTheme.shapes.large),
                    model = cover,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = time?.toLong()?.formatMinSec() ?: timeString,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UpIcon()
                    Text(
                        text = upName,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SmallVideoCardPreview() {
    BVTheme {
        Surface {
            SmallVideoCard(
                title = "震惊！太震惊了！真的是太震惊了！我的天呐！真TMD震惊！",
                cover = "http://i2.hdslb.com/bfs/archive/af17fc07b8f735e822563cc45b7b5607a491dfff.jpg",
                upName = "bishi",
                play = 2333,
                danmaku = 666,
                time = 2333 * 1000
            )
        }
    }
}