package dev.aaa1115910.bv.component.search

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchKeyword(
    modifier: Modifier = Modifier,
    keyword: String,
    icon: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var hasFocus by remember { mutableStateOf(false) }

    val buttonBackgroundColor =
        if (hasFocus) MaterialTheme.colorScheme.primary
        else Color.Transparent

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Surface(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.hasFocus
            },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = buttonBackgroundColor,
            focusedContainerColor = buttonBackgroundColor,
            pressedContainerColor = buttonBackgroundColor
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.small),
        onClick = onClick
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp,
                        horizontal = 12.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != "") {
                    Image(
                        modifier = Modifier.height(16.dp),
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(data = icon)
                                .apply { size(Size.ORIGINAL) }
                                .build(),
                            imageLoader = imageLoader,
                            contentScale = ContentScale.FillHeight
                        ),
                        contentDescription = null,
                    )
                }
                Text(
                    text = keyword,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}