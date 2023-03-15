package dev.aaa1115910.bv.component.controllers2.playermenu.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.BottomTip

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StepLessMenuItem(
    modifier: Modifier = Modifier,
    value: Float = 1f,
    text:String,
    step: Float = 0.01f,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit
) {
    Box(
        modifier = modifier.fillMaxHeight()
    ) {
        dev.aaa1115910.bv.component.controllers.MenuListItem(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .onPreviewKeyEvent {
                    when (it.key) {
                        Key.DirectionUp -> {
                            if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                            if (value >= range.endInclusive - step) {
                                onValueChange(1f)
                            } else {
                                onValueChange(value + step)
                            }
                            return@onPreviewKeyEvent true
                        }

                        Key.DirectionDown -> {
                            if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                            if (value <= step) {
                                onValueChange(0f)
                            } else {
                                onValueChange(value - step)
                            }
                            return@onPreviewKeyEvent true
                        }
                    }
                    false
                },
            text = text,
            selected = false
        ) { }

        BottomTip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            text = stringResource(R.string.video_controller_menu_danmaku_area_tip)
        )
    }
}