package dev.aaa1115910.bv.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun RemoteControlPanelInfo() {
    ConstraintLayout {
        val (panelBorder, dPadBorder, centerBorder, backBorder) = createRefs()
        val borderWidth = 3.dp
        val contentColor = MaterialTheme.colorScheme.onSurface
        val tipTextStyle = MaterialTheme.typography.labelLarge.copy(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            color = contentColor
        )

        Box(
            modifier = Modifier
                .constrainAs(panelBorder) {
                    centerTo(parent)
                }
                .size(200.dp, 400.dp)
                .border(borderWidth, contentColor, RoundedCornerShape(100.dp))
        ) {}
        Box(
            modifier = Modifier
                .constrainAs(dPadBorder) {
                    top.linkTo(panelBorder.top, 8.dp)
                    start.linkTo(panelBorder.start)
                    end.linkTo(panelBorder.end)
                }
                .size(180.dp)
                .border(borderWidth, contentColor, CircleShape)
        ) {}
        Box(
            modifier = Modifier
                .constrainAs(centerBorder) {
                    centerTo(dPadBorder)
                }
                .size(70.dp)
                .border(borderWidth, contentColor, CircleShape)
        ) {}
        Box(
            modifier = Modifier
                .constrainAs(backBorder) {
                    top.linkTo(dPadBorder.bottom, 16.dp)
                    start.linkTo(panelBorder.start, 16.dp)
                }
                .size(70.dp)
                .border(borderWidth, contentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = contentColor
            )
        }

        val (tipLineBack, tipBack) = createRefs()
        Spacer(
            modifier = Modifier
                .constrainAs(tipLineBack) {
                    end.linkTo(backBorder.start)
                    top.linkTo(backBorder.top)
                    bottom.linkTo(backBorder.bottom)
                }
                .width(80.dp)
                .height(borderWidth / 2)
                .background(contentColor)
        )
        Text(
            modifier = Modifier
                .constrainAs(tipBack) {
                    top.linkTo(tipLineBack.top)
                    bottom.linkTo(tipLineBack.bottom)
                    end.linkTo(tipLineBack.start, 8.dp)
                },
            text = stringResource(R.string.remote_control_panel_demo_tip_back),
            style = tipTextStyle
        )

        val (tipLineCenter, tipCenter) = createRefs()
        Spacer(
            modifier = Modifier
                .constrainAs(tipLineCenter) {
                    start.linkTo(centerBorder.end)
                    top.linkTo(centerBorder.top)
                    bottom.linkTo(centerBorder.bottom)
                }
                .width(125.dp)
                .height(borderWidth / 2)
                .background(contentColor)
        )
        Text(
            modifier = Modifier
                .constrainAs(tipCenter) {
                    top.linkTo(tipLineCenter.top)
                    bottom.linkTo(tipLineCenter.bottom)
                    start.linkTo(tipLineCenter.end, 8.dp)
                },
            text = stringResource(R.string.remote_control_panel_demo_tip_center),
            style = tipTextStyle
        )

        val (tipLineUp, tipUp) = createRefs()
        Spacer(
            modifier = Modifier
                .constrainAs(tipLineUp) {
                    start.linkTo(dPadBorder.end, (-80).dp)
                    top.linkTo(dPadBorder.top, 20.dp)
                }
                .width(150.dp)
                .height(borderWidth / 2)
                .background(contentColor)
        )
        Text(
            modifier = Modifier
                .constrainAs(tipUp) {
                    top.linkTo(tipLineUp.top)
                    bottom.linkTo(tipLineUp.bottom)
                    start.linkTo(tipLineUp.end, 8.dp)
                },
            text = stringResource(R.string.remote_control_panel_demo_tip_up),
            style = tipTextStyle
        )

        val (tipLineDown, tipDown) = createRefs()
        Spacer(
            modifier = Modifier
                .constrainAs(tipLineDown) {
                    start.linkTo(dPadBorder.end, (-80).dp)
                    bottom.linkTo(dPadBorder.bottom, 20.dp)
                }
                .width(150.dp)
                .height(borderWidth / 2)
                .background(contentColor)
        )
        Text(
            modifier = Modifier
                .constrainAs(tipDown) {
                    top.linkTo(tipLineDown.top)
                    bottom.linkTo(tipLineDown.bottom)
                    start.linkTo(tipLineDown.end, 8.dp)
                },
            text = stringResource(R.string.remote_control_panel_demo_tip_down),
            style = tipTextStyle
        )

        val (tipLineLR1, tipLineLR2, tipLineLR3, tipLR) = createRefs()
        Spacer(
            modifier = Modifier
                .constrainAs(tipLineLR1) {
                    end.linkTo(dPadBorder.end, 20.dp)
                    top.linkTo(dPadBorder.top, 60.dp)
                }
                .rotate(45f)
                .width(40.dp)
                .height(borderWidth / 2)
                .background(contentColor)
        )
        Spacer(
            modifier = Modifier
                .constrainAs(tipLineLR3) {
                    end.linkTo(tipLineLR1.end, 120.dp)
                    top.linkTo(tipLineLR1.top)
                }
                .rotate(45f)
                .width(40.dp)
                .height(borderWidth / 2)
                .background(contentColor)
        )
        Spacer(modifier = Modifier
            .constrainAs(tipLineLR2) {
                end.linkTo(tipLineLR1.start, (-7).dp)
                top.linkTo(tipLineLR1.top, (-14).dp)
            }
            .width(200.dp)
            .height(borderWidth / 2)
            .background(contentColor)
        )
        Text(
            modifier = Modifier
                .constrainAs(tipLR) {
                    top.linkTo(tipLineLR2.top)
                    bottom.linkTo(tipLineLR2.bottom)
                    end.linkTo(tipLineLR2.start, 8.dp)
                },
            text = stringResource(R.string.remote_control_panel_demo_tip_lr),
            style = tipTextStyle
        )
    }
}

@Composable
fun RemoteControlPanelDemo(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        runCatching {
            focusRequester.requestFocus()
        }
    }

    Surface(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxSize()
                .clickable { onConfirm() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RemoteControlPanelInfo()
                Text(
                    text = stringResource(R.string.remote_control_panel_demo_tip_bottom),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun RemoteControlPanelInfoPreview() {
    BVTheme {
        Surface {
            RemoteControlPanelInfo()
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun RemoteControlPanelDemoPreview() {
    BVTheme {
        Surface {
            RemoteControlPanelDemo()
        }
    }
}