package dev.aaa1115910.bv.component

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowScope
import androidx.tv.material3.Text
import dev.aaa1115910.bv.BVApp

@Composable
fun TopNav(
    modifier: Modifier = Modifier,
    items: List<TopNavItem>,
    isLargePadding:Boolean,
    onSelectedChanged: (TopNavItem) -> Unit = {},
    onClick: (TopNavItem) -> Unit = {}
) {
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    var selectedNav by remember { mutableStateOf(items.first()) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val verticalPadding by animateDpAsState(
        targetValue = if (isLargePadding) 24.dp else 12.dp,
        label = "top nav vertical padding"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp, verticalPadding),
        horizontalArrangement = Arrangement.Center
    ) {
        TabRow(
            modifier = Modifier
                .then(focusRestorerModifiers.parentModifier),
            selectedTabIndex = selectedTabIndex,
            separator = { Spacer(modifier = Modifier.width(12.dp)) },
        ) {
            items.forEachIndexed { index, tab ->
                NavItemTab(
                    modifier = Modifier
                        .ifElse(index == 0, focusRestorerModifiers.childModifier),
                    topNavItem = tab,
                    selected = index == selectedTabIndex,
                    onFocus = {
                        selectedNav = tab
                        selectedTabIndex = index
                        onSelectedChanged(tab)
                    },
                    onClick = { onClick(tab) }
                )
            }
        }
    }
}

@Composable
private fun TabRowScope.NavItemTab(
    modifier: Modifier = Modifier,
    topNavItem: TopNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    onFocus: () -> Unit
) {
    val context = LocalContext.current

    Tab(
        modifier = modifier,
        selected = selected,
        onFocus = onFocus,
        onClick = onClick
    ) {
        Text(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            text = topNavItem.getDisplayName(context),
            color = LocalContentColor.current,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

interface TopNavItem {
    fun getDisplayName(context: Context = BVApp.context): String
}

enum class HomeTopNavItem(private val displayName: String) : TopNavItem {
    Recommend("推荐"),
    Popular("热门"),
    Dynamics("动态");

    override fun getDisplayName(context: Context): String {
        return displayName
    }
}

enum class UgcTopNavItem(private val displayName: String) : TopNavItem {
    Douga("动画"),
    Game("游戏"),
    Kichiku("鬼畜"),
    Music("音乐"),
    Dance("舞蹈"),
    Cinephile("影视"),
    Ent("娱乐"),
    Knowledge("知识"),
    Tech("科技"),
    Information("资讯"),
    Food("美食"),
    Life("生活"),
    Car("汽车"),
    Fashion("时尚"),
    Sports("体育"),
    Animal("动物圈");

    override fun getDisplayName(context: Context): String {
        return displayName
    }
}

enum class PgcTopNavItem(private val displayName: String) : TopNavItem {
    Anime("番剧"),
    GuoChuang("国创"),
    Movie("电影"),
    Documentary("纪录片"),
    Tv("电视剧"),
    Variety("综艺");

    override fun getDisplayName(context: Context): String {
        return displayName
    }
}