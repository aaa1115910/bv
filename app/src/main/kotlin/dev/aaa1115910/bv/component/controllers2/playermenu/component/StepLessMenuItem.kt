package dev.aaa1115910.bv.component.controllers2.playermenu.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.BottomTip
import dev.aaa1115910.bv.util.requestFocus

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StepLessMenuItem(
    modifier: Modifier = Modifier,
    value: Float = 1f,
    text: String,
    step: Float = 0.01f,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    isFocusing: Boolean,
    onValueChange: (Float) -> Unit,
    onFocusBackToParent: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus(scope)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .onPreviewKeyEvent {
                println(it)
                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                val result = it.key == Key.DirectionRight
                if (result) onFocusBackToParent()
                result
            }
    ) {
        MenuListItem(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .focusRequester(focusRequester)
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

        BottomTip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            text = stringResource(R.string.video_controller_menu_danmaku_area_tip)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StepLessMenuItem(
    modifier: Modifier = Modifier,
    value: Int = 100,
    text: String,
    step: Int = 1,
    range: IntRange = 0..100,
    isFocusing: Boolean,
    onValueChange: (Int) -> Unit,
    onFocusBackToParent: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus(scope)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .onPreviewKeyEvent {
                println(it)
                if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                val result = it.key == Key.DirectionRight
                if (result) onFocusBackToParent()
                result
            }
    ) {
        MenuListItem(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .focusRequester(focusRequester)
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

        BottomTip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            text = stringResource(R.string.video_controller_menu_danmaku_area_tip)
        )
    }
}