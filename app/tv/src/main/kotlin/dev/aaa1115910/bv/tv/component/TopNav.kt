package dev.aaa1115910.bv.tv.component

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowScope
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.entity.ugc.UgcTypeV2
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.getDisplayName
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.isKeyDown
import kotlinx.coroutines.delay

@Composable
fun TopNav(
    modifier: Modifier = Modifier,
    items: List<TopNavItem>,
    isLargePadding: Boolean,
    initialSelectedItem: TopNavItem? = null,
    onSelectedChanged: (TopNavItem) -> Unit = {},
    onClick: (TopNavItem) -> Unit = {},
    onLeftKeyEvent: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    var selectedNav by remember(initialSelectedItem) {
        mutableStateOf(initialSelectedItem ?: items.first())
    }

    var selectedTabIndex by remember(initialSelectedItem) {
        mutableIntStateOf(
            if (initialSelectedItem != null) {
                val index = items.indexOf(initialSelectedItem)
                if (index >= 0) index else 0
            } else 0
        )
    }

    var tabMoved by remember { mutableStateOf(true) }

    LaunchedEffect(selectedNav) {
        delay(200)
        onSelectedChanged(selectedNav)
        // 别急着向下移动焦点，动画还没结束
        delay(400)
        tabMoved = true
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 2.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TabRow(
            modifier = Modifier
                .focusRestorer(focusRequester)
                .onPreviewKeyEvent {
                    if (it.isKeyDown()) {
                        if (it.key == Key.DirectionLeft && selectedTabIndex == 0) {
                            onLeftKeyEvent()
                            return@onPreviewKeyEvent true
                        }
                        if (it.key == Key.DirectionDown) {
                            return@onPreviewKeyEvent !tabMoved
                        }
                    }
                    false
                },
            selectedTabIndex = selectedTabIndex,
            separator = { Spacer(modifier = Modifier.width(12.dp)) },
        ) {
            items.forEachIndexed { index, tab ->
                NavItemTab(
                    modifier = Modifier
                        .ifElse(index == selectedTabIndex, Modifier.focusRequester(focusRequester)),
                    topNavItem = tab,
                    selected = index == selectedTabIndex,
                    onFocus = {
                        tabMoved = tab == selectedNav
                        selectedNav = tab
                        selectedTabIndex = index
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
    Dynamics("动态"),
    History("历史"),
    Favorite("收藏"),
    FollowingSeason("追番"),
    ToView("稍后看");

    override fun getDisplayName(context: Context): String {
        return displayName
    }
}

enum class UgcTopNavItem(private val ugcType: UgcTypeV2) : TopNavItem {
    Douga(UgcTypeV2.Douga),
    Game(UgcTypeV2.Game),
    Kichiku(UgcTypeV2.Kichiku),
    Music(UgcTypeV2.Music),
    Dance(UgcTypeV2.Dance),
    Cinephile(UgcTypeV2.Cinephile),
    Ent(UgcTypeV2.Ent),
    Knowledge(UgcTypeV2.Knowledge),
    Tech(UgcTypeV2.Tech),
    Information(UgcTypeV2.Information),
    Food(UgcTypeV2.Food),
    ShortPlay(UgcTypeV2.Shortplay),
    Car(UgcTypeV2.Car),
    Fashion(UgcTypeV2.Fashion),
    Sports(UgcTypeV2.Sports),
    Animal(UgcTypeV2.Animal),
    Vlog(UgcTypeV2.Vlog),
    Painting(UgcTypeV2.Painting),
    Ai(UgcTypeV2.Ai),
    Home(UgcTypeV2.Home),
    Outdoors(UgcTypeV2.Outdoors),
    Gym(UgcTypeV2.Gym),
    Handmake(UgcTypeV2.Handmake),
    Travel(UgcTypeV2.Travel),
    Rural(UgcTypeV2.Rural),
    Parenting(UgcTypeV2.Parenting),
    Health(UgcTypeV2.Health),
    Emotion(UgcTypeV2.Emotion),
    LifeJoy(UgcTypeV2.LifeJoy),
    LifeExperience(UgcTypeV2.LifeExperience),
    Mysticism(UgcTypeV2.Mysticism);

    override fun getDisplayName(context: Context): String {
        return ugcType.getDisplayName(context)
    }
}

enum class PgcTopNavItem(private val pgcType: PgcType) : TopNavItem {
    Anime(PgcType.Anime),
    GuoChuang(PgcType.GuoChuang),
    Movie(PgcType.Movie),
    Documentary(PgcType.Documentary),
    Tv(PgcType.Tv),
    Variety(PgcType.Variety);

    override fun getDisplayName(context: Context): String {
        return pgcType.getDisplayName(context)
    }
}