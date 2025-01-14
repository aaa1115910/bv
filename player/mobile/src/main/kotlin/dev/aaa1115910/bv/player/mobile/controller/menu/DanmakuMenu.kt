package dev.aaa1115910.bv.player.mobile.controller.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.mobile.noRippleClickable
import dev.aaa1115910.bv.util.ifElse

@Composable
private fun DanmakuMenu(
    modifier: Modifier = Modifier,
    enabledDanmakuTypes: List<DanmakuType>,
    danmakuScale: Float,
    danmakuOpacity: Float,
    danmakuArea: Float,
    onEnabledDanmakuTypeChange: (List<DanmakuType>) -> Unit,
    onDanmakuScaleChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit
) {
    Surface(
        modifier = modifier.noRippleClickable { },
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
                modifier = Modifier.width(300.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp)
            ) {
                item {
                    EnabledDanmakuType(
                        enabledDanmakuTypes = enabledDanmakuTypes,
                        onEnabledDanmakuTypeChange = onEnabledDanmakuTypeChange
                    )
                }
                item {
                    DanmakuOpacity(
                        danmakuOpacity = danmakuOpacity,
                        onDanmakuOpacityChange = onDanmakuOpacityChange
                    )
                }
                item {
                    DanmakuArea(
                        danmakuArea = danmakuArea,
                        onDanmakuAreaChange = onDanmakuAreaChange
                    )
                }
                item {
                    DanmakuScale(
                        danmakuScale = danmakuScale,
                        onDanmakuScaleChange = onDanmakuScaleChange
                    )
                }
            }
        }
    }
}

@Composable
private fun EnabledDanmakuType(
    modifier: Modifier = Modifier,
    enabledDanmakuTypes: List<DanmakuType>,
    onEnabledDanmakuTypeChange: (List<DanmakuType>) -> Unit
) {
    val onClickEnabledDanmakuTypeButton: (DanmakuType, Boolean) -> Unit = { danmakuType, blocked ->
        val newEnabledDanmakuTypes = enabledDanmakuTypes.toMutableList()
        newEnabledDanmakuTypes.remove(DanmakuType.All)
        if (!blocked) {
            newEnabledDanmakuTypes.add(danmakuType)
        } else {
            newEnabledDanmakuTypes.remove(danmakuType)
        }
        if (newEnabledDanmakuTypes.size == 3) {
            newEnabledDanmakuTypes.add(DanmakuType.All)
        }
        onEnabledDanmakuTypeChange(newEnabledDanmakuTypes)
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = "屏蔽类型",
            color = Color.White
        )
        Row {
            EnabledDanmakuTypeButton(
                danmakuType = DanmakuType.Top,
                selected = !enabledDanmakuTypes.contains(DanmakuType.Top),
                onEnabledStateChange = { onClickEnabledDanmakuTypeButton(DanmakuType.Top, it) }
            )
            EnabledDanmakuTypeButton(
                danmakuType = DanmakuType.Bottom,
                selected = !enabledDanmakuTypes.contains(DanmakuType.Bottom),
                onEnabledStateChange = { onClickEnabledDanmakuTypeButton(DanmakuType.Bottom, it) }
            )
            EnabledDanmakuTypeButton(
                danmakuType = DanmakuType.Rolling,
                selected = !enabledDanmakuTypes.contains(DanmakuType.Rolling),
                onEnabledStateChange = { onClickEnabledDanmakuTypeButton(DanmakuType.Rolling, it) }
            )
        }
    }
}

@Composable
private fun EnabledDanmakuTypeButton(
    modifier: Modifier = Modifier,
    danmakuType: DanmakuType,
    selected: Boolean,
    onEnabledStateChange: (Boolean) -> Unit
) {
    val colors = ButtonDefaults.textButtonColors(
        contentColor = if (selected) MaterialTheme.colorScheme.primary else Color.White
    )
    TextButton(
        modifier = modifier,
        colors = colors,
        onClick = { onEnabledStateChange(!selected) }
    ) {
        Text(text = danmakuType.name)
    }
}

@Composable
private fun DanmakuOpacity(
    modifier: Modifier = Modifier,
    danmakuOpacity: Float,
    onDanmakuOpacityChange: (Float) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "不透明度",
                color = Color.White
            )
            Text(
                text = "${(danmakuOpacity * 100).toInt()}%",
                color = Color.White
            )
        }

        Slider(value = danmakuOpacity, onValueChange = onDanmakuOpacityChange)
    }
}

@Composable
private fun DanmakuArea(
    modifier: Modifier = Modifier,
    danmakuArea: Float,
    onDanmakuAreaChange: (Float) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "显示区域",
                color = Color.White
            )
            Text(
                text = "${(danmakuArea * 100).toInt()}%",
                color = Color.White
            )
        }

        Slider(value = danmakuArea, onValueChange = onDanmakuAreaChange)
    }
}

@Composable
private fun DanmakuScale(
    modifier: Modifier = Modifier,
    danmakuScale: Float,
    onDanmakuScaleChange: (Float) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "字体缩放",
                color = Color.White
            )
            Text(
                text = "${(danmakuScale * 100).toInt()}%",
                color = Color.White
            )
        }

        Slider(
            value = danmakuScale,
            onValueChange = onDanmakuScaleChange,
            valueRange = 0.5f..2f,
        )
    }
}

@Composable
fun DanmakuMenuController(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideController: () -> Unit = {},
    enabledDanmakuTypes: List<DanmakuType>,
    danmakuScale: Float,
    danmakuOpacity: Float,
    danmakuArea: Float,
    onEnabledDanmakuTypesChange: (List<DanmakuType>) -> Unit,
    onDanmakuScaleChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit
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
            DanmakuMenu(
                modifier = Modifier,
                enabledDanmakuTypes = enabledDanmakuTypes,
                danmakuScale = danmakuScale,
                danmakuOpacity = danmakuOpacity,
                danmakuArea = danmakuArea,
                onEnabledDanmakuTypeChange = onEnabledDanmakuTypesChange,
                onDanmakuScaleChange = onDanmakuScaleChange,
                onDanmakuOpacityChange = onDanmakuOpacityChange,
                onDanmakuAreaChange = onDanmakuAreaChange
            )
        }
    }
}

@Preview
@Composable
private fun ResolutionMenuPreview() {
    MaterialTheme {
        DanmakuMenu(
            enabledDanmakuTypes = listOf(DanmakuType.Bottom),
            danmakuScale = 1f,
            danmakuOpacity = 1f,
            danmakuArea = 1f,
            onEnabledDanmakuTypeChange = {},
            onDanmakuScaleChange = {},
            onDanmakuOpacityChange = {},
            onDanmakuAreaChange = {}
        )
    }
}

@Preview
@Composable
private fun EnabledDanmakuTypePreview() {
    MaterialTheme {
        EnabledDanmakuType(
            enabledDanmakuTypes = listOf(DanmakuType.Bottom),
            onEnabledDanmakuTypeChange = {}
        )
    }
}

@Preview
@Composable
private fun DanmakuOpacityPreview() {
    MaterialTheme {
        DanmakuOpacity(
            danmakuOpacity = 0.6f,
            onDanmakuOpacityChange = {}
        )
    }
}

@Preview
@Composable
private fun DanmakuAreaPreview() {
    MaterialTheme {
        DanmakuArea(
            danmakuArea = 0.6f,
            onDanmakuAreaChange = {}
        )
    }
}

@Preview
@Composable
private fun DanmakuScalePreview() {
    MaterialTheme {
        DanmakuScale(
            danmakuScale = 1f,
            onDanmakuScaleChange = {}
        )
    }
}