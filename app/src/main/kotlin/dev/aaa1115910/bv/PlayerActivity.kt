package dev.aaa1115910.bv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_MENU
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.exoplayer.ExoPlayer
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.bv.component.VideoPlayer
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : ComponentActivity() {
    companion object {
        private val logger = KotlinLogging.logger { }
        fun actionStart(context: Context, avid: Int, cid: Int) {
            context.startActivity(
                Intent(context, PlayerActivity::class.java).apply {
                    putExtra("avid", avid)
                    putExtra("cid", cid)
                }
            )
        }
    }

    private val playerViewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val player = ExoPlayer
            .Builder(this)
            .setSeekForwardIncrementMs(1000 * 10)
            .setSeekBackIncrementMs(1000 * 5)
            .build()
        val danmakuPlayer = DanmakuPlayer(SimpleRenderer())
        playerViewModel.preparePlayer(player)
        playerViewModel.prepareDanmakuPlayer(danmakuPlayer)

        if (intent.hasExtra("avid")) {
            val aid = intent.getIntExtra("avid", 170001)
            val cid = intent.getIntExtra("cid", 170001)
            logger.info { "Launch parameter: [aid=$aid, cid=$cid]" }
            playerViewModel.loadPlayUrl(this@PlayerActivity, aid, cid)
        } else {
            logger.info { "Null launch parameter" }
        }

        //playerViewModel.loadVideo()
        //playerViewModel.loadVideo(this)
        setContent {
            BVTheme {
                BVPlayer(

                )
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val key = when (keyCode) {
            KEYCODE_DPAD_UP -> Keys.Up
            KEYCODE_DPAD_DOWN -> Keys.Down
            KEYCODE_DPAD_LEFT -> Keys.Left
            KEYCODE_DPAD_RIGHT -> Keys.Right
            KEYCODE_MENU -> Keys.Menu
            KEYCODE_BACK -> Keys.Back
            KEYCODE_DPAD_CENTER -> Keys.Center
            else -> Keys.Other
        }
        playerViewModel.lastPressedKey = key
        playerViewModel.lastPressedTime = System.currentTimeMillis()
        println("pressed $keyCode, event: $event")
        if (playerViewModel.showingRightMenu) {
            return key == Keys.Other || key == Keys.Back
        }
        return key != Keys.Other
    }
}

@Composable
fun BVPlayer(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel = koinViewModel()
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (playerViewModel.show) {
            VideoPlayer(
                player = playerViewModel.player!!,
                danmakuPlayer = playerViewModel.danmakuPlayer!!
            )
        }
        if (BuildConfig.DEBUG) {
            Column {
                Text("${playerViewModel.show}")
            }

        }
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.White),
            text = "${playerViewModel.errorMessage}"
        )
    }
}

enum class RequestState {
    Ready, Doing, Done, Success, Failed
}

enum class Keys {
    Up, Down, Left, Right, Menu, Back, Center, Other
}