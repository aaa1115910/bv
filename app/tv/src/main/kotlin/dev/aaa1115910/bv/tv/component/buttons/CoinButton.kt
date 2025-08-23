package dev.aaa1115910.bv.tv.component.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun CoinButton(
    modifier: Modifier = Modifier,
    isCoin: Boolean,
    onAddCoin: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), // 减小内边距
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp)), // 设置为小圆角
        onClick = {onAddCoin()}
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 减小间距
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCoin) Icons.Rounded.Paid else Icons.Outlined.Paid,
                contentDescription = null,
                tint = if (isCoin) Color(0xfffb7299) else Color.Gray
            )
            Text(
                text = stringResource(R.string.coin_button_text)
            )
        }
    }
}

@Preview
@Composable
fun CoinButtonEnablePreview() {
    BVTheme {
        CoinButton(
            isCoin = true
        )
    }
}

@Preview
@Composable
fun CoinButtonDisablePreview() {
    BVTheme {
        CoinButton(
            isCoin = false
        )
    }
}
