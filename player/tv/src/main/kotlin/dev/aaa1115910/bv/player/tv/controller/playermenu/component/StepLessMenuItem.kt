package dev.aaa1115910.bv.player.tv.controller.playermenu.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon

@Composable
fun StepLessMenuItem(
    modifier: Modifier = Modifier,
    value: Float = 1f,
    text: String,
    step: Float = 0.01f,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit,
    onFocusBackToParent: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .onPreviewKeyEvent {
                println(it)
                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                if (it.key == Key.DirectionRight) onFocusBackToParent()
                false
            }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Rounded.ArrowDropUp, contentDescription = null)
            MenuListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .onPreviewKeyEvent {
                        when (it.key) {
                            Key.DirectionUp -> {
                                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                                if (value >= range.endInclusive - step) {
                                    onValueChange(range.endInclusive)
                                } else {
                                    onValueChange(value + step)
                                }
                                return@onPreviewKeyEvent true
                            }

                            Key.DirectionDown -> {
                                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                                if (value - step <= range.start) {
                                    onValueChange(range.start)
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
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
        }
    }
}

@Composable
fun StepLessMenuItem(
    modifier: Modifier = Modifier,
    value: Int = 100,
    text: String,
    step: Int = 1,
    range: IntRange = 0..100,
    onValueChange: (Int) -> Unit,
    onFocusBackToParent: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .onPreviewKeyEvent {
                println(it)
                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                if (it.key == Key.DirectionRight) onFocusBackToParent()
                false
            }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Rounded.ArrowDropUp, contentDescription = null)
            MenuListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .onPreviewKeyEvent {
                        when (it.key) {
                            Key.DirectionUp -> {
                                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                                if (value >= range.last - step) {
                                    onValueChange(range.last)
                                } else {
                                    onValueChange(value + step)
                                }
                                return@onPreviewKeyEvent true
                            }

                            Key.DirectionDown -> {
                                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                                if (value - step <= range.first) {
                                    onValueChange(range.first)
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
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
        }
    }
}
