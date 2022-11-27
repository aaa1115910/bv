package dev.aaa1115910.bv.component.controllers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency

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
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    var inQualityMenu by remember { mutableStateOf(false) }
    var inDanmakuSwitchMenu by remember { mutableStateOf(false) }
    var inDanmakuSizeMenu by remember { mutableStateOf(false) }
    var inDanmakuTransparencyMenu by remember { mutableStateOf(false) }

    var showAnySecondMenu by remember { mutableStateOf(false) }

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
            Row {
                AnimatedVisibility(visible = inQualityMenu) {
                    Column(
                        modifier = Modifier.width(200.dp)
                    ) {
                        resolutionMap.forEach { (id, name) ->
                            ControllerMenuItem(text = name) {
                                onChooseResolution(id)
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = inDanmakuSwitchMenu) {
                    Column(
                        modifier = Modifier.width(200.dp)
                    ) {
                        ControllerMenuItem(text = "开启") {
                            onSwitchDanmaku(true)
                        }
                        ControllerMenuItem(text = "关闭") {
                            onSwitchDanmaku(false)
                        }
                    }
                }
                AnimatedVisibility(visible = inDanmakuSizeMenu) {
                    Column(
                        modifier = Modifier.width(200.dp)
                    ) {
                        DanmakuSize.values().forEach {
                            ControllerMenuItem(text = it.name) {
                                onDanmakuSizeChange(it)
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = inDanmakuTransparencyMenu) {
                    Column(
                        modifier = Modifier.width(200.dp)
                    ) {
                        DanmakuTransparency.values().forEach {
                            ControllerMenuItem(text = it.name) {
                                onDanmakuTransparencyChange(it)
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = true) {
                    Column(
                        modifier = Modifier.width(200.dp)
                    ) {
                        ControllerMenuItem(
                            modifier = Modifier.focusRequester(focusRequester),
                            text = "清晰度"
                        ) {
                            println("click 清晰度")
                            inQualityMenu = !inQualityMenu
                            inDanmakuSwitchMenu = false
                            inDanmakuSizeMenu = false
                            inDanmakuTransparencyMenu = false
                        }
                        ControllerMenuItem(
                            text = "弹幕开关"
                        ) {
                            println("click 弹幕开关")
                            inQualityMenu = false
                            inDanmakuSwitchMenu = !inDanmakuSwitchMenu
                            inDanmakuSizeMenu = false
                            inDanmakuTransparencyMenu = false
                        }
                        ControllerMenuItem(
                            text = "弹幕大小"
                        ) {
                            println("click 弹幕大小")
                            inQualityMenu = false
                            inDanmakuSwitchMenu = false
                            inDanmakuSizeMenu = !inDanmakuSizeMenu
                            inDanmakuTransparencyMenu = false
                        }
                        ControllerMenuItem(
                            text = "弹幕透明度"
                        ) {
                            println("click 弹幕透明度")
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
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            //.focusable()
            .height(60.dp)
            .fillMaxWidth()
            .onFocusChanged { hasFocus = it.hasFocus }
            .clickable { onClick() },
        color = if (hasFocus) Color.Black else Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
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
