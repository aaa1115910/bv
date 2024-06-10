package dev.aaa1115910.bv.activities.video

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.aaa1115910.bv.component.RemoteControlPanelDemo
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs

class RemoteControllerPanelDemoActivity : ComponentActivity() {
    companion object {
        fun actionStart(
            context: Context,
            avid: Long,
            cid: Long,
            title: String,
            partTitle: String,
            played: Int,
            fromSeason: Boolean,
            subType: Int? = null,
            epid: Int? = null,
            seasonId: Int? = null,
            isVerticalVideo: Boolean = false,
            proxyArea: ProxyArea = ProxyArea.MainLand,
            playerIconIdle: String = "",
            playerIconMoving: String = ""
        ) {
            context.startActivity(
                Intent(context, RemoteControllerPanelDemoActivity::class.java).apply {
                    putExtra("avid", avid)
                    putExtra("cid", cid)
                    putExtra("title", title)
                    putExtra("partTitle", partTitle)
                    putExtra("played", played)
                    putExtra("fromSeason", fromSeason)
                    putExtra("subType", subType)
                    putExtra("epid", epid)
                    putExtra("seasonId", seasonId)
                    putExtra("isVerticalVideo", isVerticalVideo)
                    putExtra("proxy_area", proxyArea.ordinal)
                    putExtra("playerIconIdle", playerIconIdle)
                    putExtra("playerIconMoving", playerIconMoving)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                RemoteControllerPanelDemoScreen()
            }
        }
    }
}

@Composable
fun RemoteControllerPanelDemoScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val intent = (context as Activity).intent

    val continueToPlayerV3 = {
        Prefs.showedRemoteControllerPanelDemo = true
        VideoPlayerV3Activity.actionStart(
            context = context,
            avid = intent.getLongExtra("avid", 0),
            cid = intent.getLongExtra("cid", 0),
            title = intent.getStringExtra("title") ?: "",
            partTitle = intent.getStringExtra("partTitle") ?: "",
            played = intent.getIntExtra("played", 0),
            fromSeason = intent.getBooleanExtra("fromSeason", false),
            subType = intent.getIntExtra("subType", 0),
            epid = intent.getIntExtra("epid", 0),
            seasonId = intent.getIntExtra("seasonId", 0),
            isVerticalVideo = intent.getBooleanExtra("isVerticalVideo", false),
            proxyArea = ProxyArea.entries[intent.getIntExtra("proxy_area", 0)],
            playerIconIdle = intent.getStringExtra("playerIconIdle") ?: "",
            playerIconMoving = intent.getStringExtra("playerIconMoving") ?: ""
        )
        context.finish()
    }

    RemoteControlPanelDemo(
        modifier = modifier.fillMaxSize(),
        onConfirm = continueToPlayerV3
    )
}