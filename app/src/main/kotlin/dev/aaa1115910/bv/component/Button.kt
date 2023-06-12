package dev.aaa1115910.bv.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Button(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    text: String = "",
    height: Dp = 36.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.small,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        icon = icon,
        text = {
            Text(
                text = text,
                style = textStyle
            )
        },
        height = height,
        color = color,
        shape = shape,
        onClick = onClick
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Button(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit = {},
    height: Dp = 36.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.small,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(height),
        onClick = { onClick() },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = color,
            focusedContainerColor = color,
            pressedContainerColor = color
        ),
        shape = ClickableSurfaceDefaults.shape(shape = shape),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 3.dp, color = Color.White), shape = shape
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) icon()
            Box(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                text()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview
@Composable
private fun ButtonsTestPreview() {
    BVTheme {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(2) {
                Button(
                    icon = {
                        Icon(imageVector = Icons.Rounded.Favorite, contentDescription = null)
                    },
                    text = "Favorite"
                ) {}
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview
@Composable
private fun ButtonWithIcon() {
    BVTheme {
        Button(
            icon = { Icon(Icons.Rounded.Favorite, null) },
            text = "Button with icon"
        ) {}
    }
}

@Preview
@Composable
private fun ButtonWithoutIcon() {
    BVTheme {
        Button(text = "Button without icon") {}
    }
}