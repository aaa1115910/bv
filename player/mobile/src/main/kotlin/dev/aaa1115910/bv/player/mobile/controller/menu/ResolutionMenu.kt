package dev.aaa1115910.bv.player.mobile.controller.menu

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
import dev.aaa1115910.bv.util.ifElse

@Composable
private fun ResolutionMenu(
    modifier: Modifier = Modifier,
    currentResolutionCode: Int,
    availableResolutionMap: Map<Int, String>,
    onClickResolution: (Int) -> Unit
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
                    items = availableResolutionMap
                        .toList()
                        .sortedByDescending { it.first }
                ) { (code, name) ->
                    ResolutionListItem(
                        text = name,
                        selected = currentResolutionCode == code,
                        onClick = {
                            println("click resolution menu: $name($code)")
                            onClickResolution(code)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ResolutionMenuController(
    modifier: Modifier = Modifier,
    show: Boolean,
    currentResolutionCode: Int,
    availableResolutionMap: Map<Int, String>,
    onHideController: () -> Unit = {},
    onClickResolution: (Int) -> Unit
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
            ResolutionMenu(
                modifier = Modifier,
                currentResolutionCode = currentResolutionCode,
                availableResolutionMap = availableResolutionMap,
                onClickResolution = onClickResolution
            )
        }
    }
}

@Composable
private fun ResolutionListItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit = {}
) {
    val textColor = if (selected) MaterialTheme.colorScheme.primary else Color.White

    Surface(
        modifier = modifier
            .size(200.dp, 48.dp),
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

private val availableResolutionMap = mapOf(
    127 to "8K",
    126 to "Dolby Vision",
    125 to "HDR",
    120 to "4K",
    116 to "1080P 60FPS",
    112 to "1080P+",
    80 to "1080P",
    74 to "720P 60FPS",
    64 to "720P",
    32 to "480P",
    16 to "360P",
    6 to "240P"
)

@Preview
@Composable
private fun ResolutionListItemSelectedPreview() {
    MaterialTheme {
        ResolutionListItem(
            text = "1080P 60FPS",
            selected = true
        )
    }
}

@Preview
@Composable
private fun ResolutionListItemUnselectedPreview() {
    MaterialTheme {
        ResolutionListItem(
            text = "1080P 60FPS",
            selected = false
        )
    }
}

@Preview
@Composable
private fun ResolutionMenuPreview() {
    MaterialTheme {
        ResolutionMenu(
            currentResolutionCode = 32,
            availableResolutionMap = availableResolutionMap,
            onClickResolution = {}
        )
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun ResolutionMenuControllerPreview() {
    MaterialTheme {
        ResolutionMenuController(
            show = true,
            currentResolutionCode = 32,
            availableResolutionMap = availableResolutionMap,
            onClickResolution = {}
        )
    }
}