package dev.aaa1115910.bv.screen.main.pgc

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.pgc.PgcIndexActivity
import dev.aaa1115910.bv.activities.pgc.anime.AnimeTimelineActivity
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
        PgcIndexActivity.actionStart(context = context, pgcType = PgcType.Anime)
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
    PgcFeatureButtons(
        modifier = modifier,
        buttons = buttons
    )
}

@Preview(device = "id:tv_1080p")
@Composable
private fun AnimeFeatureButtonsPreview() {
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
