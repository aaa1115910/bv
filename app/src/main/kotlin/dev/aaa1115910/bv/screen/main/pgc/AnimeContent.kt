package dev.aaa1115910.bv.screen.main.pgc

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.repositories.PgcType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.anime.AnimeIndexActivity
import dev.aaa1115910.bv.activities.anime.AnimeTimelineActivity
import dev.aaa1115910.bv.activities.user.FollowingSeasonActivity
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.pgc.PgcAnimeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnimeContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    pgcViewModel: PgcAnimeViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val onOpenTimeline: () -> Unit = {
        context.startActivity(Intent(context, AnimeTimelineActivity::class.java))
    }
    val onOpenFollowing: () -> Unit = {
        context.startActivity(Intent(context, FollowingSeasonActivity::class.java))
    }
    val onOpenIndex: () -> Unit = {
        context.startActivity(Intent(context, AnimeIndexActivity::class.java))
    }
    val onOpenGamerAni: () -> Unit = {
        val packageManager = context.packageManager
        val gamerAniPackageName = "tw.com.gamer.android.animad"
        packageManager.getLeanbackLaunchIntentForPackage(gamerAniPackageName)?.let {
            context.startActivity(it)
        } ?: run {
            R.string.anime_home_button_gamer_ani_launch_failed.toast(context)
        }
    }

    PgcScaffold(
        lazyListState = lazyListState,
        pgcViewModel = pgcViewModel,
        pgcType = PgcType.Anime,
        featureButtons = {
            AnimeFeatureButtons(
                modifier = Modifier.padding(vertical = 24.dp),
                onOpenTimeline = onOpenTimeline,
                onOpenFollowing = onOpenFollowing,
                onOpenIndex = onOpenIndex,
                onOpenGamerAni = onOpenGamerAni
            )
        }
    )
}

@Composable
private fun AnimeFeatureButtons(
    modifier: Modifier = Modifier,
    onOpenTimeline: () -> Unit,
    onOpenFollowing: () -> Unit,
    onOpenIndex: () -> Unit,
    onOpenGamerAni: () -> Unit = {}
) {
    val buttonWidth = 185.dp
    val buttons = listOf(
        Triple(
            stringResource(R.string.anime_home_button_timeline),
            Icons.Rounded.Alarm,
            onOpenTimeline
        ),
        Triple(
            stringResource(R.string.anime_home_button_following),
            Icons.Rounded.Favorite,
            onOpenFollowing
        ),
        Triple(
            stringResource(R.string.anime_home_button_index),
            Icons.AutoMirrored.Rounded.List,
            onOpenIndex
        ),
        Triple(
            stringResource(R.string.anime_home_button_gamer_ani),
            painterResource(R.drawable.ic_gamer_ani),
            onOpenGamerAni
        )
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) {
        items(items = buttons) { (title, icon, onClick) ->
            when (icon) {
                is ImageVector -> AnimeFeatureButton(
                    modifier = Modifier.width(buttonWidth),
                    title = title,
                    icon = icon,
                    onClick = onClick
                )

                is Painter -> AnimeFeatureButton(
                    modifier = Modifier.width(buttonWidth),
                    title = title,
                    icon = icon,
                    onClick = onClick
                )

                else -> {}
            }
        }
    }
}

@Composable
fun AnimeFeatureButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun AnimeFeatureButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = icon,
                    contentDescription = null
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}



@Preview(device = "id:tv_1080p")
@Composable
fun AnimeFeatureButtonsPreview() {
    BVTheme {
        AnimeFeatureButtons(
            modifier = Modifier,
            onOpenTimeline = {},
            onOpenFollowing = {},
            onOpenIndex = {},
            onOpenGamerAni = {}
        )
    }
}
