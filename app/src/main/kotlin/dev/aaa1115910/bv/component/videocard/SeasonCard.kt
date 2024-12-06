package dev.aaa1115910.bv.component.videocard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SeasonCard(
    modifier: Modifier = Modifier,
    data: SeasonCardData,
    coverHeight: Dp? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onFocus: () -> Unit = {}
) {
    val localDensity = LocalDensity.current
    var coverRealWidth by remember { mutableStateOf(0.dp) }

    Surface(
        modifier = modifier.onFocusChanged { if (it.hasFocus) onFocus() },
        onClick = onClick,
        onLongClick = onLongClick,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            pressedContainerColor = MaterialTheme.colorScheme.surface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 3.dp, color = Color.White),
                shape = MaterialTheme.shapes.large
            )
        )
    ) {
        Column {
            val coverModifier = if (coverHeight != null) {
                Modifier.height(coverHeight)
            } else {
                Modifier.fillMaxWidth()
            }
            val textBoxModifier = if (coverHeight != null) {
                Modifier.width((0.75 * coverHeight.value).dp)
            } else {
                Modifier
            }

            Box(
                modifier = Modifier.clip(MaterialTheme.shapes.large),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    modifier = coverModifier
                        .aspectRatio(0.75f)
                        .clip(MaterialTheme.shapes.large)
                        .onGloballyPositioned { coordinates ->
                            coverRealWidth = with(localDensity) { coordinates.size.width.toDp() }
                        },
                    model = data.cover,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )

                if (data.rating != null) {
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            // 无法使用 fillMaxWidth 来确定宽度
                            .width(coverRealWidth)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .fillMaxWidth()
                            .padding(8.dp, 0.dp),
                        text = data.rating,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.End
                    )
                }
            }

            Column(
                modifier = textBoxModifier.padding(8.dp)
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (data.subTitle != null) {
                    Text(
                        text = data.subTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun SeasonCardPreview() {
    BVTheme {
        LazyVerticalGrid(columns = GridCells.Fixed(6)) {
            repeat(6) {
                item {
                    SeasonCard(
                        data = SeasonCardData(
                            seasonId = 40794,
                            title = "007：没空去死",
                            cover = "http://i0.hdslb.com/bfs/bangumi/image/8d211c396aad084d6fa413015200dda6ed260768.png",
                            rating = "8.6"
                        )
                    )
                }
            }
        }
    }
}

