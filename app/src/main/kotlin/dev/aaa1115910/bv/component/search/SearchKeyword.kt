package dev.aaa1115910.bv.component.search

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DenseListItem
import androidx.tv.material3.Text
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun SearchKeyword(
    modifier: Modifier = Modifier,
    keyword: String,
    icon: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(data = icon)
            .size(Size.ORIGINAL)
            .build(),
        imageLoader = imageLoader,
        contentScale = ContentScale.FillHeight
    )

    if (icon != "" && painter.state is AsyncImagePainter.State.Success) {
        DenseListItem(
            modifier = modifier,
            selected = false,
            onClick = onClick,
            headlineContent = {
                Text(
                    text = keyword,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                Image(
                    modifier = Modifier.height(16.dp),
                    painter = painter,
                    contentDescription = null,
                )
            }
        )
    } else {
        DenseListItem(
            modifier = modifier,
            selected = false,
            onClick = onClick,
            headlineContent = {
                Text(
                    text = keyword,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}