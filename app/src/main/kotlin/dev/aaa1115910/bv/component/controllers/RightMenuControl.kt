package dev.aaa1115910.bv.component.controllers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency
import mu.KotlinLogging

@Composable
fun RightMenuControl(
    modifier: Modifier = Modifier,
    resolutionMap: Map<Int, String> = emptyMap(),
    currentResolution: Int? = null,
    currentDanmakuEnabled: Boolean = true,
    currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    onChooseResolution: (Int) -> Unit,
    onSwitchDanmaku: (Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit,
    menuWidth: Dp = 200.dp,
    secondMenuHorizontalPadding: Dp = 86.dp
) {
    val logger = KotlinLogging.logger { }
    val focusRequester = remember { FocusRequester() }

    var inQualityMenu by remember { mutableStateOf(false) }
    var inDanmakuSwitchMenu by remember { mutableStateOf(false) }
    var inDanmakuSizeMenu by remember { mutableStateOf(false) }
    var inDanmakuTransparencyMenu by remember { mutableStateOf(false) }

    var showAnySecondMenu by remember { mutableStateOf(false) }

    val qualityMap by remember { mutableStateOf(resolutionMap.toSortedMap(compareByDescending { it })) }
    LaunchedEffect(inQualityMenu || inDanmakuSwitchMenu || inDanmakuSizeMenu || inDanmakuTransparencyMenu) {
        showAnySecondMenu =
            inQualityMenu || inDanmakuSwitchMenu || inDanmakuSizeMenu || inDanmakuTransparencyMenu
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = inQualityMenu) {
                    TvLazyColumn(
                        modifier = Modifier.width(menuWidth),
                        contentPadding = PaddingValues(vertical = secondMenuHorizontalPadding)
                    ) {
                        items(items = qualityMap.keys.toList()) { id ->
                            ControllerMenuItem(
                                text = qualityMap[id] ?: "unknown: $id",
                                selected = currentResolution == id
                            ) {
                                logger.info { "Choose video quality: ${qualityMap[id]}" }
                                onChooseResolution(id)
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = inDanmakuSwitchMenu) {
                    TvLazyColumn(
                        modifier = Modifier.width(menuWidth),
                        contentPadding = PaddingValues(vertical = secondMenuHorizontalPadding)
                    ) {
                        item {
                            ControllerMenuItem(
                                text = "开启",
                                selected = currentDanmakuEnabled
                            ) {
                                logger.info { "Choose danmaku enabled: true" }
                                onSwitchDanmaku(true)
                            }
                        }
                        item {
                            ControllerMenuItem(
                                text = "关闭",
                                selected = !currentDanmakuEnabled
                            ) {
                                logger.info { "Choose danmaku enabled: false" }
                                onSwitchDanmaku(false)
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = inDanmakuSizeMenu) {
                    TvLazyColumn(
                        modifier = Modifier.width(menuWidth),
                        contentPadding = PaddingValues(vertical = secondMenuHorizontalPadding)
                    ) {
                        items(items = DanmakuSize.values()) { danmakuSize ->
                            ControllerMenuItem(
                                text = "${danmakuSize.scale}x",
                                selected = currentDanmakuSize == danmakuSize
                            ) {
                                logger.info { "Choose danmaku size: $danmakuSize" }
                                onDanmakuSizeChange(danmakuSize)
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = inDanmakuTransparencyMenu) {
                    TvLazyColumn(
                        modifier = Modifier.width(menuWidth),
                        contentPadding = PaddingValues(vertical = secondMenuHorizontalPadding)
                    ) {
                        items(items = DanmakuTransparency.values()) { danmakuTransparency ->
                            ControllerMenuItem(
                                text = "${danmakuTransparency.transparency}",
                                selected = currentDanmakuTransparency == danmakuTransparency
                            ) {
                                logger.info { "Choose danmaku transparency: $danmakuTransparency" }
                                onDanmakuTransparencyChange(danmakuTransparency)
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = true) {
                    Column(
                        modifier = Modifier.width(menuWidth)
                    ) {
                        ControllerMenuItem(
                            modifier = Modifier.focusRequester(focusRequester),
                            text = "清晰度",
                            selected = inQualityMenu
                        ) {
                            logger.info { "Click quality menu" }
                            inQualityMenu = !inQualityMenu
                            inDanmakuSwitchMenu = false
                            inDanmakuSizeMenu = false
                            inDanmakuTransparencyMenu = false
                        }
                        ControllerMenuItem(
                            text = "弹幕开关",
                            selected = inDanmakuSwitchMenu
                        ) {
                            logger.info { "Click danmaku enabled menu" }
                            inQualityMenu = false
                            inDanmakuSwitchMenu = !inDanmakuSwitchMenu
                            inDanmakuSizeMenu = false
                            inDanmakuTransparencyMenu = false
                        }
                        ControllerMenuItem(
                            text = "弹幕大小",
                            selected = inDanmakuSizeMenu
                        ) {
                            logger.info { "Click danmaku size menu" }
                            inQualityMenu = false
                            inDanmakuSwitchMenu = false
                            inDanmakuSizeMenu = !inDanmakuSizeMenu
                            inDanmakuTransparencyMenu = false
                        }
                        ControllerMenuItem(
                            text = "弹幕透明",
                            selected = inDanmakuTransparencyMenu
                        ) {
                            logger.info { "Click danmaku transparency menu" }
                            inQualityMenu = false
                            inDanmakuSwitchMenu = false
                            inDanmakuSizeMenu = false
                            inDanmakuTransparencyMenu = !inDanmakuTransparencyMenu
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RightPartControl(
    modifier: Modifier = Modifier
) {

}


@Composable
private fun ControllerMenuItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderAlpha by animateFloatAsState(if (selected) 1f else 0f)

    Surface(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.hasFocus }
            .clickable { onClick() },
        color = if (isFocused) Color.White else Color.Transparent
    ) {
        Box(
            modifier = Modifier.border(
                width = 2.dp,
                color = (if (isFocused) Color.Black else Color.White).copy(alpha = borderAlpha)
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = if (isFocused) Color.Black else Color.White
            )
        }
    }
}

@Preview
@Composable
fun ControllerMenuItemPreview() {
    ControllerMenuItem(
        text = "Menu Item",
        onClick = {}
    )
}
