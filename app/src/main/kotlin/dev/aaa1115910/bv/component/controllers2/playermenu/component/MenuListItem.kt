package dev.aaa1115910.bv.component.controllers2.playermenu.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.tv.material3.ToggleableSurfaceDefaults

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
    Surface(
        modifier = modifier.onFocusChanged { if (it.hasFocus) onFocus() },
        checked = selected,
        onCheckedChange = { onClick() },
        color = ToggleableSurfaceDefaults.color(
            color = Color.Transparent,
            focusedColor = MaterialTheme.colorScheme.primary,
            pressedColor = MaterialTheme.colorScheme.primary,
            selectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            focusedSelectedColor = MaterialTheme.colorScheme.primary,
            pressedSelectedColor = MaterialTheme.colorScheme.primary
        ),
        contentColor = ToggleableSurfaceDefaults.contentColor(
            color = MaterialTheme.colorScheme.onPrimary,
            focusedColor = MaterialTheme.colorScheme.onPrimary,
            pressedColor = MaterialTheme.colorScheme.onPrimary,
            selectedColor = MaterialTheme.colorScheme.onPrimary,
            focusedSelectedColor = MaterialTheme.colorScheme.onPrimary,
            pressedSelectedColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = ToggleableSurfaceDefaults.shape(MaterialTheme.shapes.small)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = 6.dp,
                    horizontal = if (expanded) 24.dp else 6.dp
                )
        ) {
            AnimatedVisibility(visible = expanded) {
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
        AnimatedVisibility(
            modifier = Modifier.padding(6.dp),
            visible = !expanded,
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