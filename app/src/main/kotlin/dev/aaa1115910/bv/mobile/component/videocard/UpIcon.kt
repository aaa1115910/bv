package dev.aaa1115910.bv.mobile.component.videocard

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

@Composable
fun UpIcon(
    modifier: Modifier = Modifier,
    //color: Color = MaterialTheme.colorScheme.onSurface
) {
    Icon(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_up),
        contentDescription = null,
        //tint = color
    )
}

@Preview
@Composable
fun UpIconPreview() {
    BVMobileTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            UpIcon()
            Text(text = "bishi")
        }
    }
}