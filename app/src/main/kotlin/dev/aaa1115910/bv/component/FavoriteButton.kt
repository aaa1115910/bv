package dev.aaa1115910.bv.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.util.focusedBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .focusedBorder(MaterialTheme.shapes.medium),
        onClick = { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isFavorite) {
                Icon(imageVector = Icons.Rounded.Favorite, contentDescription = null)
            } else {
                Icon(imageVector = Icons.Rounded.FavoriteBorder, contentDescription = null)
            }
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "收藏",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
fun FavoriteButtonEnablePreview() {
    FavoriteButton(
        isFavorite = true,
        onClick = {}
    )
}

@Preview
@Composable
fun FavoriteButtonDisablePreview() {
    FavoriteButton(
        isFavorite = false,
        onClick = {}
    )
}