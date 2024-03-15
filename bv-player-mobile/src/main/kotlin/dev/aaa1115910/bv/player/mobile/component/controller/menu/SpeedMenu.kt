package dev.aaa1115910.bv.player.mobile.component.controller.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.player.mobile.util.ifElse

@Composable
private fun SpeedMenu(
    modifier: Modifier = Modifier,
    currentSpeed: Float,
    onClickSpeed: (Float) -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 32.dp)
            ) {
                items(
                    items = availableSpeedList
                        .toList()
                        .sortedByDescending { it.first }
                ) { (speedNum, speedName) ->
                    SpeedListItem(
                        text = speedName,
                        selected = currentSpeed == speedNum,
                        onClick = {
                            println("click speed menu: $speedName($speedNum)")
                            onClickSpeed(speedNum)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SpeedMenuController(
    modifier: Modifier = Modifier,
    show: Boolean,
    currentSpeed: Float,
    onHideController: () -> Unit = {},
    onClickSpeed: (Float) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .ifElse(show, Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { if (show) onHideController() }
                )
            }),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = show,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            SpeedMenu(
                modifier = Modifier,
                currentSpeed = currentSpeed,
                onClickSpeed = onClickSpeed
            )
        }
    }
}

@Composable
private fun SpeedListItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit = {}
) {
    val textColor = if (selected) MaterialTheme.colorScheme.primary else Color.White

    Surface(
        modifier = modifier
            .size(120.dp, 48.dp),
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 32.dp),
                text = text,
                color = textColor
            )
        }
    }
}

private val availableSpeedList = mapOf(
    2.0f to "2x",
    1.5f to "1.5x",
    1.0f to "1.0x",
    0.75f to "0.75x",
    0.5f to "0.5x"
)

@Preview
@Composable
private fun SpeedListItemSelectedPreview() {
    MaterialTheme {
        SpeedListItem(
            text = "1.0x",
            selected = true
        )
    }
}

@Preview
@Composable
private fun SpeedListItemUnselectedPreview() {
    MaterialTheme {
        SpeedListItem(
            text = "1.0x",
            selected = false
        )
    }
}

@Preview
@Composable
private fun ResolutionMenuPreview() {
    MaterialTheme {
        SpeedMenu(
            currentSpeed = 1.0f,
            onClickSpeed = {}
        )
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun SpeedMenuControllerPreview() {
    MaterialTheme {
        SpeedMenuController(
            show = true,
            currentSpeed = 1.0f,
            onClickSpeed = {}
        )
    }
}