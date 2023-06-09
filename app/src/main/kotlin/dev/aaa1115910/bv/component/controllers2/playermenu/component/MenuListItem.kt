package dev.aaa1115910.bv.component.controllers2.playermenu.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.tv.material3.ToggleableSurfaceDefaults
import dev.aaa1115910.bv.ui.theme.BVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MenuListItem(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    expanded: Boolean = true,
    selected: Boolean,
    textAlign: TextAlign = TextAlign.Center,
    onFocus: () -> Unit = {},
    onClick: () -> Unit
) {
    val itemWidth by animateDpAsState(
        targetValue = if (expanded) 200.dp else 44.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "MenuListItem width [$text]"
    )

    Surface(
        modifier = modifier
            .height(44.dp)
            .width(itemWidth)
            .onFocusChanged { if (it.hasFocus) onFocus() },
        checked = selected,
        onCheckedChange = { onClick() },
        colors = ToggleableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            pressedContainerColor = MaterialTheme.colorScheme.primary,
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            focusedSelectedContainerColor = MaterialTheme.colorScheme.primary,
            pressedSelectedContainerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            focusedContentColor = MaterialTheme.colorScheme.onPrimary,
            pressedContentColor = MaterialTheme.colorScheme.onPrimary,
            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
            focusedSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
            pressedSelectedContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = ToggleableSurfaceDefaults.shape(MaterialTheme.shapes.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    vertical = 6.dp,
                    horizontal = if (expanded) 24.dp else 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = expanded,
                label = "MenuListItem text [$text]",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //if(expanded){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = textAlign,
                    maxLines = 1
                )
            }

        }
        Row(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = !expanded,
                label = "MenuListItem icon [$text]",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (icon == null) {
                    Box(modifier = Modifier.size(32.dp))
                } else {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MenuListItemPreview() {
    var expanded by remember { mutableStateOf(true) }
    BVTheme {
        MenuListItem(
            text = "MenuListItem",
            icon = Icons.Default.Home,
            expanded = expanded,
            selected = true,
            textAlign = TextAlign.Center,
            onFocus = {},
            onClick = { expanded = !expanded }
        )
    }
}