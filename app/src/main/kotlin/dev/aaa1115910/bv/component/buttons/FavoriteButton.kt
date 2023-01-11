package dev.aaa1115910.bv.component.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.aaa1115910.bv.component.Button
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        icon = {
            if (isFavorite) {
                Icon(imageVector = Icons.Rounded.Favorite, contentDescription = null)
            } else {
                Icon(imageVector = Icons.Rounded.FavoriteBorder, contentDescription = null)
            }
        },
        text = "收藏",
        onClick=onClick
    )
}

@Preview
@Composable
fun FavoriteButtonEnablePreview() {
    BVTheme {
        FavoriteButton(
            isFavorite = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun FavoriteButtonDisablePreview() {
    BVTheme {
        FavoriteButton(
            isFavorite = false,
            onClick = {}
        )
    }
}