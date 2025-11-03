package dev.aaa1115910.bv.tv.component.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonBorder
import androidx.tv.material3.ButtonColors
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    isLike: Boolean,
    onToggleLike: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp), // 减小内边距
    colors: ButtonColors = ButtonDefaults.colors(),
    border: ButtonBorder = ButtonDefaults.border()
) {
    Button(
        modifier = modifier,
        onClick = {onToggleLike()},
        contentPadding = contentPadding,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp)), // 设置为小圆角
        colors = colors,
        border = border,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp), // 减小间距
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp),
                imageVector = if (isLike) Icons.Rounded.ThumbUp else Icons.Outlined.ThumbUp,
                contentDescription = null,
                tint = if (isLike) Color(0xfffb7299) else LocalContentColor.current
            )
            Text(
                text = stringResource(R.string.like_button_text)
            )
        }
    }
}

@Preview
@Composable
fun LikeButtonEnablePreview() {
    BVTheme {
        LikeButton(
            isLike = true
        )
    }
}

@Preview
@Composable
fun LikeButtonDisablePreview() {
    BVTheme {
        LikeButton(
            isLike = false
        )
    }
}
