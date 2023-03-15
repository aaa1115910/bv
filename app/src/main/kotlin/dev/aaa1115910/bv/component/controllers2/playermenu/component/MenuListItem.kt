package dev.aaa1115910.bv.component.controllers2.playermenu.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
    var hasFocus by remember { mutableStateOf(false) }

    val buttonBackgroundColor =
        if (hasFocus) MaterialTheme.colorScheme.primary
        else if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        else Color.Transparent

    Surface(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.hasFocus
                if (hasFocus) onFocus()
            }
            .clickable { onClick() },
        color = buttonBackgroundColor,
        shape = MaterialTheme.shapes.small
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
            //if(!expanded){
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