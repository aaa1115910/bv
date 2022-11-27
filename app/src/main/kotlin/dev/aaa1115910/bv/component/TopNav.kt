package dev.aaa1115910.bv.component

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aaa1115910.bv.LoginActivity
import dev.aaa1115910.bv.UserInfoActivity
import dev.aaa1115910.bv.util.Prefs

val topNavItems = listOf("搜索", "热门推荐", "分区", "番剧", "动态")

@Composable
fun TopNav(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BV",
                    fontSize = 13.sp,
                    modifier = Modifier
                        .alpha(0.5f)
                        .padding(horizontal = 30.dp)
                )

                for (topNavItem in topNavItems) {
                    val content: @Composable (RowScope.() -> Unit) = {
                        if (topNavItem == "搜索") {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Text(text = topNavItem)
                    }
                    TabButton(content = content)
                }
            }
            IconButton(
                onClick = {
                    if(Prefs.isLogin){
                        context.startActivity(Intent(context, UserInfoActivity::class.java))
                    }else {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Color.Gray),
            ) {
                Text(text = "头像")
            }
        }
    }
}

@Composable
private fun TabButton(
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    var backgroundColor by remember { mutableStateOf(Color.Transparent) }
    var contentColor by remember { mutableStateOf(primary) }

    TextButton(
        onClick = {},
        modifier = modifier
            .onFocusChanged {
                if (it.isFocused) {
                    backgroundColor = primary
                    contentColor = onPrimary
                } else {
                    backgroundColor = Color.Transparent
                    contentColor = primary
                }
            }
            .shadow(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
        ),
        content = content
    )
}